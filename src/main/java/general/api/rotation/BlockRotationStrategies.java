package general.api.rotation;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

/**
 * Standard orientation strategies for blocks with vanilla direction properties.
 */
public enum BlockRotationStrategies implements BlockRotationStrategy {

	/**
	 * A block with one horizontal front face.
	 */
	HORIZONTAL_FACING {
		@Override
		public void addProperties (StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(BlockStateProperties.HORIZONTAL_FACING);
		}

		@Override
		public BlockState getStateForPlacement (BlockState state, BlockPlaceContext context) {
			return state.setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
		}

		@Override
		public BlockState rotate (BlockState state, boolean reverse) {
			Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			return state.setValue(BlockStateProperties.HORIZONTAL_FACING, reverse ? facing.getCounterClockWise() : facing.getClockWise());
		}

		@Override
		public Optional<Direction> getFacing (BlockState state) {
			return Optional.of(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
		}
	},

	/**
	 * A block whose front may point in any of the six cardinal directions.
	 */
	FACING {
		@Override
		public void addProperties (StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(BlockStateProperties.FACING);
		}

		@Override
		public BlockState getStateForPlacement (BlockState state, BlockPlaceContext context) {
			return state.setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
		}

		@Override
		public BlockState rotate (BlockState state, boolean reverse) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if (facing.getAxis().isVertical()) return state;
			return state.setValue(BlockStateProperties.FACING, reverse ? facing.getCounterClockWise() : facing.getClockWise());
		}

		@Override
		public Optional<Direction> getFacing (BlockState state) {
			return Optional.of(state.getValue(BlockStateProperties.FACING));
		}
	},

	/**
	 * A pillar-style block aligned to the axis of the clicked face.
	 */
	AXIS {
		@Override
		public void addProperties (StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(BlockStateProperties.AXIS);
		}

		@Override
		public BlockState getStateForPlacement (BlockState state, BlockPlaceContext context) {
			return state.setValue(BlockStateProperties.AXIS, context.getClickedFace().getAxis());
		}

		@Override
		public BlockState rotate (BlockState state, boolean reverse) {
			Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
			Direction.Axis next = switch (axis) {
				case X -> reverse ? Direction.Axis.Y : Direction.Axis.Z;
				case Y -> reverse ? Direction.Axis.Z : Direction.Axis.X;
				case Z -> reverse ? Direction.Axis.X : Direction.Axis.Y;
			};
			return state.setValue(BlockStateProperties.AXIS, next);
		}
	};
}
