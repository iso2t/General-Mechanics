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
import general.mechanics.common.block.machine.ElectricFurnaceBlock;
import general.mechanics.common.menus.ElectricFurnaceMenu;
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

public class ElectricFurnaceBlockEntity extends MachineBlockEntity implements MachineMultiblock, MachineLockableItems, MachineEnergy, MachineNetwork, MenuProvider {

	private static final MachinePowerProfile BASE_POWER_PROFILE = MachinePowerProfile.base(100_000, 10_000, 20);

	public static final MachinePowerProfile STANDALONE_POWER_PROFILE = BASE_POWER_PROFILE;

	public static final ItemInventoryDefinition ITEMS = ItemInventoryDefinition.builder().input(ElectricFurnaceBlock.RecipeSlots.INPUT).input(ElectricFurnaceBlock.RecipeSlots.CATALYST).output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1).output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_2).output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_3).output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_4).build();

	public static final int INPUT_SLOT    = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.INPUT);
	public static final int CATALYST_SLOT = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.CATALYST);
	public static final int OUTPUT_SLOT_1 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1);
	public static final int OUTPUT_SLOT_2 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_2);
	public static final int OUTPUT_SLOT_3 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_3);
	public static final int OUTPUT_SLOT_4 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_4);

	/**
	 * Shared side rules; the visual front is permanently disabled.
	 */
	public static final MachineSideConfigurationDefinition SIDES = MachineSideConfigurationDefinition.builder().allow(MachineSideMode.ITEM_INPUT, MachineSideMode.ITEM_OUTPUT, MachineSideMode.ENERGY_INPUT, MachineSideMode.NETWORK).lock(MachineFace.FRONT).defaultMode(MachineFace.LEFT, MachineSideMode.ITEM_INPUT).defaultMode(MachineFace.RIGHT, MachineSideMode.ITEM_OUTPUT).defaultMode(MachineFace.BACK, MachineSideMode.ENERGY_INPUT).defaultMode(MachineFace.TOP, MachineSideMode.NETWORK).build();

	public static final MachineDefinition MACHINE = MachineDefinition.builder().items(ITEMS, items -> items.lockable(ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.CATALYST).input(ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.CATALYST).output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, ElectricFurnaceBlock.RecipeSlots.OUTPUT_2, ElectricFurnaceBlock.RecipeSlots.OUTPUT_3, ElectricFurnaceBlock.RecipeSlots.OUTPUT_4)).energy(BASE_POWER_PROFILE, energy -> energy.network(ResourceIoMode.INSERT)).sideConfiguration(SIDES).poweredRecipes(ElectricFurnaceBlock::getRecipeDefinition).network("ElectricFurnace", (runtime, services) -> {
		services.register(NetworkServices.ITEM, NetworkConnectorServices.itemService(runtime::getNetworkItemHandler));
		services.register(NetworkServices.ENERGY, NetworkConnectorServices.energyService(runtime::getNetworkEnergyHandler));
	}).multiblock(() -> GenMultiblocks.ELECTRIC_FURNACE, true, MachineUpgradeResolvers.firstProviderAt('U')).build();

	public ElectricFurnaceBlockEntity (BlockEntityType<ElectricFurnaceBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, MACHINE);
	}

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<ElectricFurnaceBlockEntity> type) {
		MachineCapabilities.register(event, type);
	}

	public ItemStack getInputStack () {
		return machine().getItemStack(INPUT_SLOT);
	}

	public ItemStack getCatalystStack () {
		return machine().getItemStack(CATALYST_SLOT);
	}

	@Override
	public @NonNull Component getDisplayName () {
		return !isMultiblockFormed() ? Component.translatable(getBlockState().getBlock().getDescriptionId()) : Component.translatableWithFallback(getMultiblockDefinition().toString(), getMultiblockDefinition().localizedName().getRawString());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int containerId, @NonNull Inventory inventory, @NonNull Player player) {
		return new ElectricFurnaceMenu(containerId, inventory, this);
	}

}
