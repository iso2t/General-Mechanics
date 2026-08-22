package general.mechanics.common.block.machine;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.block.util.ILitProvider;
import general.api.block.util.IPickaxe;
import general.api.crafting.*;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.api.transfer.fluid.FluidTanks;
import general.mechanics.common.block.entity.CokeOvenControllerBlockEntity;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenFluids;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CokeOvenController extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<CokeOvenControllerBlockEntity>, IWrenchable, IMachineModel, IRotatableBlock, RecipeDataProvider, ILitProvider, IPickaxe {

	private BlockEntityType<CokeOvenControllerBlockEntity> blockEntityType;

	public CokeOvenController (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE));
		registerDefaultState(getStateDefinition().any().setValue(LIT, false));
	}

	@Override
	public void setBlockEntity (Class<CokeOvenControllerBlockEntity> blockEntityClass, BlockEntityType<CokeOvenControllerBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return new CokeOvenControllerBlockEntity(blockEntityType, blockPos, blockState);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker (Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
		if (level.isClientSide() || type != blockEntityType) return null;
		return (tickLevel, pos, tickState, blockEntity) -> {
			if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof CokeOvenControllerBlockEntity controller) {
				controller.serverTick(serverLevel);
			}
		};
	}

	@Override
	protected @NonNull InteractionResult useWithoutItem (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof CokeOvenControllerBlockEntity controller) {
			return controller.useMultiblock(player, hitResult);
		}
		return InteractionResult.PASS;
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/coke_oven/coke_oven_controller");
	}

	@Override
	public Identifier getBottomTexture () {
		return Resource.getMainMod("block/machine/coke_oven/coke_oven_controller_bottom");
	}

	@Override
	public Identifier getTopTexture () {
		return getBottomTexture();
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/coke_oven/coke_oven_controller_side");
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/coke_oven/coke_oven_controller_lit");
	}

	@Override
	public BlockRotationStrategy getRotationStrategy () {
		return BlockRotationStrategies.HORIZONTAL_FACING;
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return GenBlocks.COKE_OVEN_BRICKS;
	}

	@Override
	public void animateTick (@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
		if (!(level.getBlockEntity(pos) instanceof CokeOvenControllerBlockEntity entity) || !state.getValue(LIT)) return;

		double xPos = pos.getX() + 0.5D;
		double yPos = pos.getY();
		double zPos = pos.getZ() + 0.5D;

		if (random.nextDouble() < 0.1D) level.playLocalSound(pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f, 1f, true);

		var direction = getFacing(state);
		if (direction.isEmpty()) return;
		var axis = direction.get().getAxis();

		double defaultOffset = random.nextDouble() * 0.6 - 0.3;
		double xOffsets = axis == Direction.Axis.X ? (double) direction.get().getStepX() * 0.52 : defaultOffset;
		double yOffsets = random.nextDouble() * 6.0 / 8.0;
		double zOffsets = axis == Direction.Axis.Z ? (double) direction.get().getStepZ() * 0.52 : defaultOffset;

		level.addParticle(ParticleTypes.SMOKE, xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
		level.addParticle(ParticleTypes.FLAME, xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);

		var stack = entity.getInputStack();
		if (!stack.isEmpty()) {
			level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack.getItem()), xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
		}
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		var path = context.ownerId().getPath();
		context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.MISC, this, 1).pattern("BBB").pattern("BSB").pattern("BBB").define('B', GenBlocks.COKE_OVEN_BRICKS).define('S', Blocks.BLAST_FURNACE), path + "_from_bricks");

		// Register specific recipes for the Coke Oven
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, Items.COAL, 1).itemOutput(RecipeSlots.OUTPUT, GenItems.COAL_COKE.get(), 1).fluidOutput(RecipeSlots.CREOSOTE, GenFluids.CREOSOTE.get(), FluidTanks.BUCKET / 4).duration(1_200), "coke_oven/coal_coke");
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, Tags.Items.STORAGE_BLOCKS_COAL, context.items(), 1).itemOutput(RecipeSlots.OUTPUT, GenItems.COAL_COKE.get(), 9).fluidOutput(RecipeSlots.CREOSOTE, GenFluids.CREOSOTE.get(), 2_250).duration(1_200 * 9), "coke_oven/coal_coke_from_coal_block");
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, Items.CHARCOAL, 1).itemOutput(RecipeSlots.OUTPUT, GenItems.COAL_COKE.get(), 1).fluidOutput(RecipeSlots.CREOSOTE, GenFluids.CREOSOTE.get(), FluidTanks.BUCKET / 2).duration(1_200), "coke_oven/coal_coke_from_charcoal");
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, ItemTags.LOGS_THAT_BURN, context.items(), 1).itemOutput(RecipeSlots.OUTPUT, GenItems.COAL_COKE.get(), 1).fluidOutput(RecipeSlots.CREOSOTE, GenFluids.CREOSOTE.get(), FluidTanks.BUCKET / 4).duration(1_500), "coke_oven/coal_coke_from_logs");
	}

	public static MachineRecipeDefinition<NoRecipeData> getRecipeDefinition () {
		return GenRecipes.COKE_OVEN;
	}

	/**
	 * Logical recipe fields shared by the Coke Oven schema, storage, and datagen.
	 */
	public static final class RecipeSlots {

		public static final MachineRecipeSlot.ItemInput   INPUT    = MachineRecipeSlots.ITEM_INPUT;
		public static final MachineRecipeSlot.ItemOutput  OUTPUT   = MachineRecipeSlots.ITEM_OUTPUT;
		public static final MachineRecipeSlot.FluidOutput CREOSOTE = MachineRecipeSlots.FLUID_OUTPUT;

		private RecipeSlots () {
		}
	}

}
