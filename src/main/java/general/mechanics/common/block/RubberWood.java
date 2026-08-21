package general.mechanics.common.block;

import general.api.block.DecorativeBlock;
import general.api.block.IBlockTagsProvider;
import general.api.block.util.IAxe;
import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.mechanics.registries.GenBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class RubberWood {

	public static class LogBlock extends RotatedPillarBlock implements IBlockTagsProvider, IAxe {

		public LogBlock (Properties properties) {
			super(properties);
		}

		@Override
		public boolean isFlammable (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
			return true;
		}

		@Override
		public int getFlammability (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
			return 5;
		}

		@Override
		public int getFireSpreadSpeed (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
			return 5;
		}

		@Override
		public @Nullable BlockState getToolModifiedState (@NonNull BlockState state, @NonNull UseOnContext context, @NonNull ItemAbility itemAbility, boolean simulate) {
			if (context.getItemInHand().is(ItemTags.AXES)) {
				if (state.is(GenBlocks.RUBBER_LOG.get())) return GenBlocks.STRIPPED_RUBBER_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
				if (state.is(GenBlocks.RUBBER_WOOD.get())) return GenBlocks.STRIPPED_RUBBER_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
			}
			return super.getToolModifiedState(state, context, itemAbility, simulate);
		}

		@Override
		public List<TagKey<Block>> getBlockTags () {
			return List.of(BlockTags.LOGS_THAT_BURN);
		}
	}

	public static class RubberLogBlock extends LogBlock {

		public static final int                     MAX_SAP      = 3;
		public static final IntegerProperty         SAP          = IntegerProperty.create("sap", 0, MAX_SAP);
		public static final EnumProperty<Direction> RESIN_FACING = BlockStateProperties.HORIZONTAL_FACING;

		public RubberLogBlock (Properties properties) {
			super(properties.randomTicks());
		}

		@Override
		protected void createBlockStateDefinition (StateDefinition.@NonNull Builder<Block, BlockState> builder) {
			super.createBlockStateDefinition(builder);
			builder.add(SAP, RESIN_FACING);
		}

		@Override
		protected void randomTick (@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
			if (state.getValue(AXIS) != Direction.Axis.Y) return;
			int sap = state.getValue(SAP);
			if (sap >= MAX_SAP || random.nextInt(8) != 0) return;

			BlockState next = state.setValue(SAP, sap + 1);
			if (sap + 1 == MAX_SAP) {
				next = next.setValue(RESIN_FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
			}
			level.setBlock(pos, next, Block.UPDATE_CLIENTS);
		}

		public static boolean isSappy (BlockState state) {
			return state.getBlock() instanceof RubberWood.RubberLogBlock && state.getValue(SAP) == MAX_SAP;
		}
	}

	public static class Planks extends DecorativeBlock implements RecipeDataProvider {

		public Planks (Properties properties) {
			super(properties);
		}

		@Override
		public boolean isFlammable (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
			return true;
		}

		@Override
		public int getFlammability (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
			return 20;
		}

		@Override
		public int getFireSpreadSpeed (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
			return 5;
		}

		@Override
		public List<TagKey<Block>> getBlockTags () {
			return List.of(BlockTags.PLANKS, BlockTags.MINEABLE_WITH_AXE);
		}

		@Override
		public void generateRecipes (RecipeGenerationContext context) {
			context.save(ShapelessRecipeBuilder.shapeless(context.items(), RecipeCategory.MISC, this, 4).requires(GenBlocks.RUBBER_LOG.get()));
		}

		@Override
		public ItemLike getRecipeUnlockItem () {
			return GenBlocks.RUBBER_LOG;
		}
	}

}
