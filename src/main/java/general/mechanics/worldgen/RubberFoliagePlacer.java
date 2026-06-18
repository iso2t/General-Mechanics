package general.mechanics.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import general.mechanics.registries.GenFoliagePlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class RubberFoliagePlacer extends FoliagePlacer {

	public static final MapCodec<RubberFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> foliagePlacerParts(instance).and(IntProviders.codec(0, 8).fieldOf("height").forGetter(p -> p.height)).apply(instance, RubberFoliagePlacer::new));

	private final IntProvider height;

	public RubberFoliagePlacer (IntProvider radius, IntProvider offset, IntProvider height) {
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> type () {
		return GenFoliagePlacers.RUBBER.get();
	}

	@Override
	protected void createFoliage (WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, int treeHeight, FoliageAttachment attachment, int foliageHeight, int leafRadius, int offset) {
		BlockPos origin = attachment.pos();
		boolean doubleTrunk = attachment.doubleTrunk();
		int body = leafRadius + attachment.radiusOffset();

		// Rows run top (yo = offset) -> bottom (yo = -foliageHeight); rowsFromBottom == 0 is the base. The
		// trunk runs up through the centre of every row. Shape is driven explicitly per row:
		//   - bottom 2 rows: full square (the solid "cube" bulb base)
		//   - the next row (first step-in to radius 1): a full 3x3
		//   - every row above that: a cross (+) — corners dropped, 4 arms still wrap the trunk
		for (int yo = offset; yo >= -foliageHeight; yo--) {
			int rowsFromBottom = foliageHeight + yo;
			if (rowsFromBottom <= 1) {
				placeSquareRow(level, foliageSetter, random, config, origin, body, yo, doubleTrunk);
			} else if (rowsFromBottom == 2) {
				placeSquareRow(level, foliageSetter, random, config, origin, 1, yo, doubleTrunk);
				// TODO: place 4 leaf blocks, one at the outer center edge to make the transition nicer.
			} else if (yo == offset) {
				// Topmost row: a single leaf only. With the apex leaf above, the tree is capped by a clean
				// 2-block point rather than a cross.
				tryPlaceLeaf(level, foliageSetter, random, config, origin.above(yo));
			} else {
				placeCrossRow(level, foliageSetter, random, config, origin, 1, yo, doubleTrunk);
			}
		}

		tryPlaceLeaf(level, foliageSetter, random, config, origin.above(offset + 1));
	}

	/**
	 * Places a square layer. The 4 hard corners of the wide (radius &ge; 2) layers are dropped so the base
	 * reads as a rounded octagon rather than a perfect cube; small layers (radius 1) stay full.
	 */
	private void placeSquareRow (WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, BlockPos origin, int radius, int yo, boolean doubleTrunk) {
		if (doubleTrunk) {
			this.placeLeavesRow(level, foliageSetter, random, config, origin, radius, yo, true);
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (radius >= 2 && Math.abs(dx) == radius && Math.abs(dz) == radius) continue;   // drop corners
				tryPlaceLeaf(level, foliageSetter, random, config, pos.setWithOffset(origin, dx, yo, dz));
			}
		}
	}

	/** Places a cross (+) layer — only the cells on the two axes through the trunk (corners dropped). */
	private void placeCrossRow (WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, BlockPos origin, int radius, int yo, boolean doubleTrunk) {
		// second to last yo should not have leaves generated
		if (doubleTrunk) {
			this.placeLeavesRow(level, foliageSetter, random, config, origin, radius, yo, true);
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (dx == 0 || dz == 0) {
					tryPlaceLeaf(level, foliageSetter, random, config, pos.setWithOffset(origin, dx, yo, dz));
				}
			}
		}
	}

	@Override
	public int foliageHeight (RandomSource random, int treeHeight, TreeConfiguration config) {
		return Math.min(treeHeight - 1, this.height.sample(random));
	}

	@Override
	protected boolean shouldSkipLocation (RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		// Unused: this placer positions every leaf explicitly (placeSquareRow / placeCrossRow) rather than
		// via placeLeavesRow, so nothing is skipped here.
		return false;
	}
}
