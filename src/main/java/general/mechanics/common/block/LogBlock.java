package general.mechanics.common.block;

import general.api.block.IBlockTagsProvider;
import general.mechanics.registries.GenBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class LogBlock extends RotatedPillarBlock implements IBlockTagsProvider {

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
		return List.of(BlockTags.MINEABLE_WITH_AXE, BlockTags.NEEDS_STONE_TOOL, BlockTags.LOGS_THAT_BURN);
	}
}
