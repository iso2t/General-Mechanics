package general.mechanics.common.block.entity;

import general.api.crafting.MachineRecipeProcessor;
import general.api.machine.*;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import general.api.machine.power.MachinePowerPlan;
import general.api.machine.power.MachinePowerProfile;
import general.api.machine.upgrade.MachineUpgradeResolvers;
import general.api.network.NetworkServices;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.ResourceSlotKey;
import general.api.transfer.fluid.FluidContainerTransfers;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.FluidTanks;
import general.api.transfer.item.ItemInventoryDefinition;
import general.mechanics.common.menus.FluidInfuserMenu;
import general.mechanics.common.network.NetworkConnectorServices;
import general.mechanics.registries.GenMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Fills one empty fluid-capable item with exactly one bucket from its internal
 * tank. The container mutation, tank drain, output insertion, and completion
 * tick's energy cost are committed atomically.
 */
public class FluidInfuserBlockEntity extends MachineBlockEntity implements MachineMultiblock, MachineLockableItems, MachineFluids, MachineEnergy, MachineNetwork, MenuProvider {

	private static final String PROGRESS_TAG = "infusion_progress";

	public static final int BASE_DURATION = 100;
	public static final int TANK_CAPACITY = FluidTanks.buckets(16);

	private static final MachinePowerProfile BASE_POWER_PROFILE = MachinePowerProfile.base(100_000, 10_000, 30);

	public record StorageSlot(String name) implements ResourceSlotKey {

		public static final StorageSlot INPUT  = new StorageSlot("item_input");
		public static final StorageSlot OUTPUT = new StorageSlot("item_output");
		public static final StorageSlot TANK   = new StorageSlot("fluid_input");
	}

	public static final ItemInventoryDefinition  ITEMS  = ItemInventoryDefinition.builder().input(StorageSlot.INPUT, 64, FluidContainerTransfers::isEmptyContainer).output(StorageSlot.OUTPUT).build();
	public static final FluidInventoryDefinition FLUIDS = FluidInventoryDefinition.builder().tank(StorageSlot.TANK, TANK_CAPACITY).build();

	public static final int INPUT_SLOT  = ITEMS.index(StorageSlot.INPUT);
	public static final int OUTPUT_SLOT = ITEMS.index(StorageSlot.OUTPUT);
	public static final int FLUID_TANK  = FLUIDS.index(StorageSlot.TANK);

	public static final MachineSideConfigurationDefinition SIDES = MachineSideConfigurationDefinition.builder().allow(MachineSideMode.ITEM_INPUT, MachineSideMode.ITEM_OUTPUT, MachineSideMode.FLUID_INPUT, MachineSideMode.ENERGY_INPUT, MachineSideMode.NETWORK).lock(MachineFace.FRONT).defaultMode(MachineFace.LEFT, MachineSideMode.ITEM_INPUT).defaultMode(MachineFace.RIGHT, MachineSideMode.ITEM_OUTPUT).defaultMode(MachineFace.TOP, MachineSideMode.FLUID_INPUT).defaultMode(MachineFace.BACK, MachineSideMode.ENERGY_INPUT).defaultMode(MachineFace.BOTTOM, MachineSideMode.NETWORK).build();

	public static final MachineDefinition MACHINE = MachineDefinition.builder().items(ITEMS, items -> items.lockable(StorageSlot.INPUT).input(StorageSlot.INPUT).output(StorageSlot.OUTPUT)).fluids(FLUIDS, FluidTanks.BUCKET, fluids -> fluids.input(StorageSlot.TANK)).energy(BASE_POWER_PROFILE, energy -> energy.network(ResourceIoMode.INSERT)).sideConfiguration(SIDES).network("FluidInfuser", (runtime, services) -> {
		services.register(NetworkServices.ITEM, NetworkConnectorServices.itemService(runtime::getNetworkItemHandler));
		services.register(NetworkServices.FLUID, NetworkConnectorServices.fluidService(runtime::getNetworkFluidHandler));
		services.register(NetworkServices.ENERGY, NetworkConnectorServices.energyService(runtime::getNetworkEnergyHandler));
	}).multiblock(() -> GenMultiblocks.FLUID_INFUSER, true, MachineUpgradeResolvers.firstProviderAt('U')).litState().build();

	private int progress;

	public FluidInfuserBlockEntity (BlockEntityType<FluidInfuserBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, MACHINE);
	}

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<FluidInfuserBlockEntity> type) {
		MachineCapabilities.register(event, type);
	}

	@Override
	public MachineRecipeProcessor.TickResult serverTick (ServerLevel level) {
		if (!prepareMachineProcessing(level)) return result(MachineRecipeProcessor.Status.IDLE, false);
		MachinePowerPlan power = getPowerProfile().plan(BASE_DURATION);
		int duration = power.duration();
		progress = Math.clamp(progress, 0, duration - 1);

		if (!hasCandidate()) {
			setProgress(0);
			setMachineActive(level, false);
			return result(MachineRecipeProcessor.Status.IDLE, false);
		}

		if (!canComplete()) {
			setMachineActive(level, false);
			return result(MachineRecipeProcessor.Status.BLOCKED, false);
		}

		boolean completing = progress + 1 >= duration;
		try (Transaction transaction = Transaction.openRoot()) {
			int energyCost = power.energyForTick(progress);
			if (getEnergyHandler().extract(energyCost, transaction) != energyCost) {
				setMachineActive(level, false);
				return result(MachineRecipeProcessor.Status.STARVED, false);
			}
			if (completing && !stageContainerFill(transaction)) {
				setMachineActive(level, false);
				return result(MachineRecipeProcessor.Status.BLOCKED, false);
			}
			transaction.commit();
		}

		if (completing) setProgress(0);
		else setProgress(progress + 1);
		setMachineActive(level, true);
		return result(completing ? MachineRecipeProcessor.Status.IDLE : MachineRecipeProcessor.Status.RUNNING, completing);
	}

	public int getInfusionProgress () {
		return progress;
	}

	public int getInfusionDuration () {
		return getPowerProfile().plan(BASE_DURATION).duration();
	}

	public FluidStack getFluidStack () {
		var storage = machine().requireFluidStorage();
		FluidResource resource = storage.getResource(FLUID_TANK);
		return resource.isEmpty() ? FluidStack.EMPTY : resource.toStack(storage.getAmountAsInt(FLUID_TANK));
	}

	public int getFluidCapacity () {
		return machine().requireFluidStorage().getCapacityAsInt(FLUID_TANK, FluidResource.EMPTY);
	}

	@Override
	public @NonNull Component getDisplayName () {
		return !isMultiblockFormed() ? Component.translatable(getBlockState().getBlock().getDescriptionId()) : Component.translatableWithFallback(getMultiblockDefinition().toString(), getMultiblockDefinition().localizedName().getRawString());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int containerId, @NonNull Inventory inventory, @NonNull Player player) {
		return new FluidInfuserMenu(containerId, inventory, this);
	}

	@Override
	protected void saveMachineAdditional (ValueOutput output) {
		output.putInt(PROGRESS_TAG, progress);
	}

	@Override
	protected void loadMachineAdditional (ValueInput input) {
		progress = Math.max(0, input.getIntOr(PROGRESS_TAG, 0));
	}

	@Override
	protected void resetCustomProcessing () {
		setProgress(0);
	}

	private boolean hasCandidate () {
		var items = getItemHandler();
		var fluids = machine().requireFluidStorage();
		return !items.getResource(INPUT_SLOT).isEmpty() && items.getAmountAsLong(INPUT_SLOT) > 0 && !fluids.getResource(FLUID_TANK).isEmpty() && fluids.getAmountAsLong(FLUID_TANK) >= FluidTanks.BUCKET;
	}

	private boolean canComplete () {
		try (Transaction transaction = Transaction.openRoot()) {
			return stageContainerFill(transaction);
		}
	}

	private boolean stageContainerFill (TransactionContext transaction) {
		return FluidContainerTransfers.fillFromTank(getItemHandler(), INPUT_SLOT, OUTPUT_SLOT, machine().requireFluidStorage(), FLUID_TANK, FluidTanks.BUCKET, transaction);
	}

	private void setProgress (int progress) {
		if (this.progress == progress) return;
		this.progress = progress;
		setChanged();
	}

	private static MachineRecipeProcessor.TickResult result (MachineRecipeProcessor.Status status, boolean completed) {
		return new MachineRecipeProcessor.TickResult(status, completed);
	}
}
