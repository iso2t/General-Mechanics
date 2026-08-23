package general.mechanics.common.block.entity;

import general.api.machine.*;
import general.api.multiblock.MultiblockInstance;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.FluidTanks;
import general.api.transfer.item.ItemInventoryDefinition;
import general.mechanics.common.block.machine.CokeOvenController;
import general.mechanics.common.menus.CokeOvenMenu;
import general.mechanics.registries.GenMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CokeOvenControllerBlockEntity extends MachineBlockEntity implements MachineMultiblock, MachineLockableItems, MachineFluids, MenuProvider {

	public static final ItemInventoryDefinition ITEMS = ItemInventoryDefinition.builder().input(CokeOvenController.RecipeSlots.INPUT).output(CokeOvenController.RecipeSlots.OUTPUT).build();

	public static final FluidInventoryDefinition FLUIDS = FluidInventoryDefinition.builder().tank(CokeOvenController.RecipeSlots.CREOSOTE, FluidTanks.buckets(32)).build();

	public static final int INPUT_SLOT    = ITEMS.index(CokeOvenController.RecipeSlots.INPUT);
	public static final int OUTPUT_SLOT   = ITEMS.index(CokeOvenController.RecipeSlots.OUTPUT);
	public static final int CREOSOTE_TANK = FLUIDS.index(CokeOvenController.RecipeSlots.CREOSOTE);

	public static final MachineDefinition MACHINE = MachineDefinition.builder().items(ITEMS, items -> items.lockable(CokeOvenController.RecipeSlots.INPUT).input(CokeOvenController.RecipeSlots.INPUT).output(CokeOvenController.RecipeSlots.OUTPUT)).fluids(FLUIDS, FluidTanks.buckets(32), fluids -> fluids.output(CokeOvenController.RecipeSlots.CREOSOTE)).recipes(CokeOvenController::getRecipeDefinition).multiblock(() -> GenMultiblocks.COKE_OVEN, false).litState().build();

	public CokeOvenControllerBlockEntity (BlockEntityType<CokeOvenControllerBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, MACHINE);
	}

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<CokeOvenControllerBlockEntity> type) {
		MachineCapabilities.register(event, type);
	}

	public ItemStack getInputStack () {
		return machine().getItemStack(INPUT_SLOT);
	}

	public FluidStack getFluidStack () {
		return getFluidHandler().getResource(CREOSOTE_TANK).toStack(getFluidHandler().getAmountAsInt(CREOSOTE_TANK));
	}

	@Override
	public InteractionResult onFormedMultiblockUse (Player player, BlockHitResult hitResult, MultiblockInstance instance) {
		if (player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(this, getBlockPos());
		return InteractionResult.SUCCESS;
	}

	@Override
	public @NonNull Component getDisplayName () {
		return Component.translatable(getMultiblockDefinition().toString());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int containerId, @NonNull Inventory inventory, @NonNull Player player) {
		return isMultiblockOperational() ? new CokeOvenMenu(containerId, inventory, this) : null;
	}
}
