package general.mechanics.block;

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

/**
 * A rubber log/wood that slowly fills with sap (like a crop) on random tick. At {@link #MAX_SAP} the
 * trunk shows a resin spot on one horizontal side ({@link #RESIN_FACING}); a sappy log yields
 * {@code TREE_SAP} when sawed (see {@code SawItem}) and will be tappable by a future machine.
 *
 * <p>Only upright (Y-axis) logs grow/show resin; horizontal logs stay plain. Stripping drops these extra
 * states (the stripped block is a plain {@link LogBlock} — see {@link LogBlock#getToolModifiedState}).
 */
public class RubberLogBlock extends LogBlock {

	public static final int MAX_SAP = 3;
	/** Sap fill stage, 0..{@link #MAX_SAP}; only meaningful (and only grows) on upright logs. */
	public static final IntegerProperty SAP = IntegerProperty.create("sap", 0, MAX_SAP);
	/** Which horizontal side carries the resin spot once the log is full. */
	public static final EnumProperty<Direction> RESIN_FACING = BlockStateProperties.HORIZONTAL_FACING;

	public RubberLogBlock (Properties properties) {
		super(properties.randomTicks());
	}

	@Override
	protected void createBlockStateDefinition (StateDefinition.@NonNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder); // AXIS
		builder.add(SAP, RESIN_FACING);
	}

	@Override
	protected void randomTick (@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
		// Sap only fills upright trunk logs, and only until full.
		if (state.getValue(AXIS) != Direction.Axis.Y) return;
		int sap = state.getValue(SAP);
		if (sap >= MAX_SAP || random.nextInt(8) != 0) return;

		BlockState next = state.setValue(SAP, sap + 1);
		if (sap + 1 == MAX_SAP) {
			// On filling, pick the side the resin spot shows on.
			next = next.setValue(RESIN_FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
		}
		level.setBlock(pos, next, Block.UPDATE_CLIENTS);
	}

	/** True when {@code state} is a full rubber log/wood (resin ready to harvest). */
	public static boolean isSappy (BlockState state) {
		return state.getBlock() instanceof RubberLogBlock && state.getValue(SAP) == MAX_SAP;
	}
}
