package general.mechanics.block;

import general.api.block.IceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ice7Block extends IceBlock {

	private static final int SLOPE_RADIUS = 6;             // influence radius (XZ)
	private static final int SLOPE_MAX_TARGET_LAYERS = 6;  // 1..8
	private static final double SLOPE_BASE_RATE = 0.30;    // chance scale at center
	private static final int SLOPE_MAX_UPDATES_PER_TICK = 3; // safety: cap edits per tick

	private static final int FREEZE_CHECKS_PER_TICK = 4;
	private static final int FREEZE_RADIUS = 5;
	private static final double FREEZE_CHANCE = 1;

	public Ice7Block (Properties properties) {
		super(properties.randomTicks());
	}

	@Override
	public boolean canMelt() {
		return false;
	}

	@Override
	public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		super.randomTick(state, level, pos, random);
		this.buildSnowSlope(level, pos, random);
		this.freezeNeighbors(state, level, pos, random);
	}

	@Override
	public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
	}

	/**
	 * Freezes neighboring blocks into ice if they are water.
	 *
	 * @param state  The state of the {@link Ice7Block}.
	 * @param level  The level the block is currently in.
	 * @param center The position of the {@link Ice7Block}.
	 * @param random The random source to use.
	 */
	protected void freezeNeighbors(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos center, @NotNull RandomSource random) {
		for (int i = 0; i < FREEZE_CHECKS_PER_TICK; i++) {
			int dx = random.nextInt(FREEZE_RADIUS * 2 + 1) - FREEZE_RADIUS;
			int dz = random.nextInt(FREEZE_RADIUS * 2 + 1) - FREEZE_RADIUS;
			int dy = 0;
			if (dx == 0 && dz == 0 && dy == 0) continue;

			BlockPos pos = center.offset(dx, dy, dz);
			BlockState bs = level.getBlockState(pos);

			if (bs.hasProperty(BlockStateProperties.WATERLOGGED) && bs.getValue(BlockStateProperties.WATERLOGGED)) {
				continue;
			}

			if (bs.getBlock() == Blocks.WATER) {
				int lvl = bs.hasProperty(FlowingFluid.LEVEL) ? bs.getValue(FlowingFluid.LEVEL) : 0;
				double chance = (lvl == 0) ? FREEZE_CHANCE : (FREEZE_CHANCE * 0.6);

				if (random.nextDouble() < chance) {
					level.setBlock(pos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);

					BlockPos above = pos.above();
					if (level.isEmptyBlock(above) && random.nextFloat() < 0.25F) {
						BlockState snow = Blocks.SNOW.defaultBlockState();
						if (snow.canSurvive(level, above)) {
							level.setBlock(above, snow.setValue(SnowLayerBlock.LAYERS, 1), Block.UPDATE_ALL);
						}
					}
				}
			}
		}
	}

	/**
	 * Builds a snow slope at the given position around the {@link Ice7Block}.
	 *
	 * @param level  The level the block is currently in.
	 * @param center The position of the parent {@link Ice7Block}.
	 * @param random The random source to use.
	 */
	protected void buildSnowSlope(@NotNull ServerLevel level, @NotNull BlockPos center, @NotNull RandomSource random) {
		final int r = SLOPE_RADIUS;
		final int r2 = r * r;

		ArrayList<BlockPos> candidates = new ArrayList<>((2 * r + 1) * (2 * r + 1) - 1);

		for (int dx = -r; dx <= r; dx++) {
			for (int dz = -r; dz <= r; dz++) {
				if (dx == 0 && dz == 0) continue;
				int d2 = dx * dx + dz * dz;
				if (d2 > r2) continue;
				BlockPos p = center.offset(dx, 0, dz);
				if (Blocks.SNOW.defaultBlockState().canSurvive(level, p)) {
					candidates.add(p);
				}
			}
		}

		// Sort by distance squared (no sqrt) → closest first
		candidates.sort(Comparator.comparingInt(p -> {
			int dx = p.getX() - center.getX();
			int dz = p.getZ() - center.getZ();
			return dx * dx + dz * dz;
		}));

		int updates = 0;

		for (BlockPos p : candidates) {
			if (updates >= SLOPE_MAX_UPDATES_PER_TICK) break;

			BlockState current = level.getBlockState(p);

			int currentLayers = 0;
			if (current.is(Blocks.SNOW)) {
				currentLayers = current.getValue(SnowLayerBlock.LAYERS);
			} else if (!current.isAir()) {
				continue;
			}

			int dx = p.getX() - center.getX();
			int dz = p.getZ() - center.getZ();
			double dist = Math.sqrt(dx * dx + dz * dz);
			double closeness = 1.0 - (dist / (double) r);
			if (closeness <= 0.0) continue;

			closeness = Math.pow(closeness, 1.2);

			int targetLayers = Math.max(1, (int) Math.round(1 + closeness * (SLOPE_MAX_TARGET_LAYERS - 1)));
			targetLayers = Math.min(targetLayers, 8);

			if (currentLayers >= targetLayers) continue;

			// build inner rings faster
			double chance = SLOPE_BASE_RATE * closeness;
			if (random.nextDouble() >= chance) continue;

			if (currentLayers == 0) {
				level.setBlock(p, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1), Block.UPDATE_ALL);
			} else {
				int next = Math.min(currentLayers + 1, targetLayers);
				level.setBlock(p, current.setValue(SnowLayerBlock.LAYERS, next), Block.UPDATE_ALL);
			}

			updates++;
		}
	}

	@Override
	public List<TagKey<Block>> getBlockTags () {
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.NEEDS_NETHERITE_TOOL);
	}

}
