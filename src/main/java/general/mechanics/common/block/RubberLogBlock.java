package general.mechanics.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.NonNull;

public class RubberLogBlock extends LogBlock {

	public static final int MAX_SAP = 3;
	public static final IntegerProperty SAP = IntegerProperty.create("sap", 0, MAX_SAP);
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
		return state.getBlock() instanceof RubberLogBlock && state.getValue(SAP) == MAX_SAP;
	}
}
