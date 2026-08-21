package general.mechanics.common.block.entity;

import general.api.block.util.ILitProvider;
import general.api.crafting.MachineRecipeProcessor;
import general.api.definitions.MultiblockDefinition;
import general.api.multiblock.MultiblockController;
import general.api.multiblock.MultiblockHandler;
import general.api.multiblock.MultiblockInstance;
import general.api.transfer.ResourceAccessPolicy;
import general.api.transfer.SidedResourceHandlers;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.FluidResourceHandler;
import general.api.transfer.fluid.FluidTanks;
import general.api.transfer.fluid.SidedFluidResourceProvider;
import general.api.transfer.item.ItemInventoryDefinition;
import general.api.transfer.item.ItemResourceHandler;
import general.api.transfer.item.SidedItemResourceProvider;
import general.mechanics.common.block.CokeOvenController;
import general.mechanics.common.menus.CokeOvenMenu;
import general.mechanics.registries.GenMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Authoritative multiblock controller state for a coke oven.
 */
public class CokeOvenControllerBlockEntity extends BlockEntity implements MultiblockController, SidedItemResourceProvider, SidedFluidResourceProvider, MenuProvider {

	private static final String RECIPE_PROCESSOR_TAG = "recipe_processor";

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<CokeOvenControllerBlockEntity> type) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, type, (block, side) -> block.isMultiblockFormed() ? block.getItemHandler(side) : null);
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, (block, side) -> block.isMultiblockFormed() ? block.getFluidHandler(side) : null);
	}

	public static final ItemInventoryDefinition ITEMS = ItemInventoryDefinition.builder().input(CokeOvenController.RecipeSlots.INPUT).output(CokeOvenController.RecipeSlots.OUTPUT).build();

	public static final FluidInventoryDefinition FLUIDS = FluidInventoryDefinition.builder().tank(CokeOvenController.RecipeSlots.CREOSOTE, FluidTanks.buckets(32)).build();

	public static final int INPUT_SLOT    = ITEMS.index(CokeOvenController.RecipeSlots.INPUT);
	public static final int OUTPUT_SLOT   = ITEMS.index(CokeOvenController.RecipeSlots.OUTPUT);
	public static final int CREOSOTE_TANK = FLUIDS.index(CokeOvenController.RecipeSlots.CREOSOTE);

	private static final ResourceAccessPolicy<ItemResource> ITEM_AUTOMATION = ITEMS.access().insert(CokeOvenController.RecipeSlots.INPUT).extract(CokeOvenController.RecipeSlots.OUTPUT).build();

	private static final ResourceAccessPolicy<FluidResource> FLUID_AUTOMATION = FLUIDS.access().extract(CokeOvenController.RecipeSlots.CREOSOTE).build();

	private       boolean                              formed;
	private final ItemResourceHandler                  items           = ITEMS.createHandler(this::setChanged);
	private final FluidResourceHandler                 fluids          = FLUIDS.createHandler(this::setChanged);
	private final MachineRecipeProcessor               recipeProcessor = CokeOvenController.getRecipeDefinition().processor(items, fluids, this::setChanged);
	private final SidedResourceHandlers<ItemResource>  sidedItems;
	private final SidedResourceHandlers<FluidResource> sidedFluids;

	public CokeOvenControllerBlockEntity (BlockEntityType<CokeOvenControllerBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		var itemViews = SidedResourceHandlers.builder(items);
		var fluidViews = SidedResourceHandlers.builder(fluids);
		for (Direction side : Direction.values()) {
			itemViews.side(side, ITEM_AUTOMATION);
			fluidViews.side(side, FLUID_AUTOMATION);
		}
		this.sidedItems = itemViews.build();
		this.sidedFluids = fluidViews.build();
	}

	@Override
	public MultiblockDefinition getMultiblockDefinition () {
		return GenMultiblocks.COKE_OVEN;
	}

	@Override
	public BlockPos getMultiblockPosition () {
		return getBlockPos();
	}

	@Override
	public Direction getMultiblockFacing () {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
	}

	@Override
	public boolean isMultiblockFormed () {
		return formed;
	}

	@Override
	public void setMultiblockFormed (boolean formed) {
		if (this.formed == formed) return;
		this.formed = formed;
		setChanged();
		if (level != null) {
			if (!formed && level instanceof ServerLevel serverLevel) {
				recipeProcessor.reset();
				setLit(serverLevel, false);
			}
			level.invalidateCapabilities(worldPosition);
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
	}

	@Override
	public void onMultiblockFormed (MultiblockInstance instance) {
		if (getLevel() instanceof ServerLevel serverLevel) {
			MultiblockHandler.spawnFormationParticles(serverLevel, instance);
		}
	}

	@Override
	public InteractionResult onFormedMultiblockUse (Player player, BlockHitResult hitResult, MultiblockInstance instance) {
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(this, worldPosition);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void saveAdditional (@NonNull ValueOutput output) {
		super.saveAdditional(output);
		output.putBoolean(FORMED_TAG, formed);
		recipeProcessor.save(output.child(RECIPE_PROCESSOR_TAG));
		items.serialize(output.child("items"));
		fluids.serialize(output.child("fluids"));
	}

	@Override
	protected void loadAdditional (@NonNull ValueInput input) {
		super.loadAdditional(input);
		formed = input.getBooleanOr(FORMED_TAG, false);
		recipeProcessor.load(input.childOrEmpty(RECIPE_PROCESSOR_TAG));
		items.deserialize(input.childOrEmpty("items"));
		fluids.deserialize(input.childOrEmpty("fluids"));
	}

	@Override
	public FluidResourceHandler getFluidHandler () {
		return fluids;
	}

	@Override
	public FluidInventoryDefinition getFluidDefinition () {
		return FLUIDS;
	}

	@Override
	public SidedResourceHandlers<FluidResource> getSidedFluidHandlers () {
		return sidedFluids;
	}

	@Override
	public ItemResourceHandler getItemHandler () {
		return items;
	}

	@Override
	public ItemInventoryDefinition getItemDefinition () {
		return ITEMS;
	}

	@Override
	public SidedResourceHandlers<ItemResource> getSidedItemHandlers () {
		return sidedItems;
	}

	/**
	 * Copy of the current input for client-side particles and display code.
	 */
	public ItemStack getInputStack () {
		return items.getResource(INPUT_SLOT).toStack(items.getAmountAsInt(INPUT_SLOT));
	}

	public FluidStack getFluidStack () {
		return fluids.getResource(CREOSOTE_TANK).toStack(fluids.getAmountAsInt(CREOSOTE_TANK));
	}

	public int getProgress () {
		return recipeProcessor.progress();
	}

	public int getMaxProgress () {
		return recipeProcessor.maxProgress();
	}

	public void serverTick (ServerLevel level) {
		if (!formed) {
			recipeProcessor.reset();
			setLit(level, false);
			return;
		}
		MachineRecipeProcessor.TickResult result = recipeProcessor.tick(level);
		setLit(level, result.status() == MachineRecipeProcessor.Status.RUNNING);
	}

	private void setLit (ServerLevel level, boolean lit) {
		BlockState state = getBlockState();
		if (!state.hasProperty(ILitProvider.LIT) || state.getValue(ILitProvider.LIT) == lit) return;
		level.setBlock(worldPosition, state.setValue(ILitProvider.LIT, lit), Block.UPDATE_CLIENTS);
	}

	@Override
	public @NonNull Component getDisplayName () {
		return Component.translatable(getBlockState().getBlock().getDescriptionId());
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int containerId, @NonNull Inventory inventory, @NonNull Player player) {
		return formed ? new CokeOvenMenu(containerId, inventory, this) : null;
	}

}
