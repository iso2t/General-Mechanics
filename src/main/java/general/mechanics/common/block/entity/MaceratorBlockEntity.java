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
import general.mechanics.common.block.machine.MaceratorBlock;
import general.mechanics.common.menus.MaceratorMenu;
import general.mechanics.common.network.NetworkConnectorServices;
import general.mechanics.registries.GenMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Standalone maceration yields one guaranteed result. Forming the factory doubles
 * only the primary output while preserving the recipe's independent chance roll.
 */
public class MaceratorBlockEntity extends MachineBlockEntity implements MachineMultiblock, MachineLockableItems, MachineEnergy, MachineNetwork, MenuProvider {

	private static final MachinePowerProfile BASE_POWER_PROFILE = MachinePowerProfile.base(100_000, 10_000, 30);

	public static final ItemInventoryDefinition ITEMS = ItemInventoryDefinition.builder().input(MaceratorBlock.RecipeSlots.INPUT).output(MaceratorBlock.RecipeSlots.OUTPUT).output(MaceratorBlock.RecipeSlots.CHANCE_OUTPUT).build();

	public static final int INPUT_SLOT         = ITEMS.index(MaceratorBlock.RecipeSlots.INPUT);
	public static final int OUTPUT_SLOT        = ITEMS.index(MaceratorBlock.RecipeSlots.OUTPUT);
	public static final int CHANCE_OUTPUT_SLOT = ITEMS.index(MaceratorBlock.RecipeSlots.CHANCE_OUTPUT);

	public static final MachineSideConfigurationDefinition SIDES = MachineSideConfigurationDefinition.builder().allow(MachineSideMode.ITEM_INPUT, MachineSideMode.ITEM_OUTPUT, MachineSideMode.ENERGY_INPUT, MachineSideMode.NETWORK).lock(MachineFace.FRONT).defaultMode(MachineFace.LEFT, MachineSideMode.ITEM_INPUT).defaultMode(MachineFace.RIGHT, MachineSideMode.ITEM_OUTPUT).defaultMode(MachineFace.BACK, MachineSideMode.ENERGY_INPUT).defaultMode(MachineFace.TOP, MachineSideMode.NETWORK).build();

	public static final MachineDefinition MACHINE = MachineDefinition.builder().items(ITEMS, items -> items.lockable(MaceratorBlock.RecipeSlots.INPUT).input(MaceratorBlock.RecipeSlots.INPUT).output(MaceratorBlock.RecipeSlots.OUTPUT, MaceratorBlock.RecipeSlots.CHANCE_OUTPUT)).energy(BASE_POWER_PROFILE, energy -> energy.network(ResourceIoMode.INSERT)).sideConfiguration(SIDES).poweredRecipes(MaceratorBlock::getRecipeDefinition).recipeItemOutputMultiplier(MaceratorBlock.RecipeSlots.OUTPUT, runtime -> runtime.isFormed() ? 2 : 1).network("Macerator", (runtime, services) -> {
		services.register(NetworkServices.ITEM, NetworkConnectorServices.itemService(runtime::getNetworkItemHandler));
		services.register(NetworkServices.ENERGY, NetworkConnectorServices.energyService(runtime::getNetworkEnergyHandler));
	}).multiblock(() -> GenMultiblocks.MACERATOR, true, MachineUpgradeResolvers.firstProviderAt('U')).build();

	public MaceratorBlockEntity (BlockEntityType<MaceratorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, MACHINE);
	}

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<MaceratorBlockEntity> type) {
		MachineCapabilities.register(event, type);
	}

	@Override
	public @NonNull Component getDisplayName () {
		return !isMultiblockFormed() ? Component.translatable(getBlockState().getBlock().getDescriptionId()) : Component.translatableWithFallback(getMultiblockDefinition().toString(), getMultiblockDefinition().localizedName().getRawString());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int containerId, @NonNull Inventory inventory, @NonNull Player player) {
		return new MaceratorMenu(containerId, inventory, this);
	}
}
