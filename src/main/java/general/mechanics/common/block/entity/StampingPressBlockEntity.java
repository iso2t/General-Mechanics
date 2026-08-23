package general.mechanics.common.block.entity;

import general.api.machine.*;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import general.api.machine.power.MachinePowerProfile;
import general.api.machine.upgrade.MachineUpgradeResolvers;
import general.api.network.NetworkServices;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.item.ItemInventoryDefinition;
import general.mechanics.common.block.machine.StampingPressBlock;
import general.mechanics.common.menus.StampingPressMenu;
import general.mechanics.common.network.NetworkConnectorServices;
import general.mechanics.registries.GenMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Powered stamping machine with a reusable die slot and an optional 3x3x3
 * factory structure. Standalone operation uses the base profile; a formed
 * factory resolves its speed and transfer upgrades from the uniform matrix ring.
 */
public class StampingPressBlockEntity extends MachineBlockEntity implements MachineMultiblock, MachineLockableItems, MachineEnergy, MachineNetwork, MenuProvider {

	private static final MachinePowerProfile BASE_POWER_PROFILE = MachinePowerProfile.base(100_000, 10_000, 40);

	public static final ItemInventoryDefinition ITEMS = ItemInventoryDefinition.builder().input(StampingPressBlock.RecipeSlots.INPUT).input(StampingPressBlock.RecipeSlots.DIE).output(StampingPressBlock.RecipeSlots.OUTPUT).build();

	public static final int INPUT_SLOT  = ITEMS.index(StampingPressBlock.RecipeSlots.INPUT);
	public static final int DIE_SLOT    = ITEMS.index(StampingPressBlock.RecipeSlots.DIE);
	public static final int OUTPUT_SLOT = ITEMS.index(StampingPressBlock.RecipeSlots.OUTPUT);

	public static final MachineSideConfigurationDefinition SIDES = MachineSideConfigurationDefinition.builder().allow(MachineSideMode.ITEM_INPUT, MachineSideMode.ITEM_OUTPUT, MachineSideMode.ENERGY_INPUT, MachineSideMode.NETWORK).lock(MachineFace.FRONT).defaultMode(MachineFace.LEFT, MachineSideMode.ITEM_INPUT).defaultMode(MachineFace.RIGHT, MachineSideMode.ITEM_OUTPUT).defaultMode(MachineFace.BACK, MachineSideMode.ENERGY_INPUT).defaultMode(MachineFace.TOP, MachineSideMode.NETWORK).build();

	public static final MachineDefinition MACHINE = MachineDefinition.builder().items(ITEMS, items -> items.lockable(StampingPressBlock.RecipeSlots.INPUT, StampingPressBlock.RecipeSlots.DIE).input(StampingPressBlock.RecipeSlots.INPUT, StampingPressBlock.RecipeSlots.DIE).output(StampingPressBlock.RecipeSlots.OUTPUT)).energy(BASE_POWER_PROFILE, energy -> energy.network(ResourceIoMode.INSERT)).sideConfiguration(SIDES).poweredRecipes(StampingPressBlock::getRecipeDefinition).network("StampingPress", (runtime, services) -> {
		services.register(NetworkServices.ITEM, NetworkConnectorServices.itemService(runtime::getNetworkItemHandler));
		services.register(NetworkServices.ENERGY, NetworkConnectorServices.energyService(runtime::getNetworkEnergyHandler));
	}).multiblock(() -> GenMultiblocks.STAMPING_PRESS, true, MachineUpgradeResolvers.firstProviderAt('U')).build();

	public StampingPressBlockEntity (BlockEntityType<StampingPressBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, MACHINE);
	}

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<StampingPressBlockEntity> type) {
		MachineCapabilities.register(event, type);
	}

	public ItemStack getInputStack () {
		return machine().getItemStack(INPUT_SLOT);
	}

	public ItemStack getDieStack () {
		return machine().getItemStack(DIE_SLOT);
	}

	@Override
	public @NonNull Component getDisplayName () {
		return !isMultiblockFormed() ? Component.translatable(getBlockState().getBlock().getDescriptionId()) : Component.translatable(getMultiblockDefinition().toString());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int containerId, @NonNull Inventory inventory, @NonNull Player player) {
		return new StampingPressMenu(containerId, inventory, this);
	}
}
