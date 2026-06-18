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

		for (int yo = offset; yo >= -foliageHeight; yo--) {
			int rowsFromBottom = foliageHeight + yo;
			if (rowsFromBottom <= 1) {
				placeSquareRow(level, foliageSetter, random, config, origin, body, yo, doubleTrunk);
			} else if (rowsFromBottom == 2) {
				placeSquareRow(level, foliageSetter, random, config, origin, 1, yo, doubleTrunk);
				placeFaceCentres(level, foliageSetter, random, config, origin, body, yo);
			} else if (yo == offset) {
				tryPlaceLeaf(level, foliageSetter, random, config, origin.above(yo));
			} else {
				placeCrossRow(level, foliageSetter, random, config, origin, 1, yo, doubleTrunk);
			}
		}

		tryPlaceLeaf(level, foliageSetter, random, config, origin.above(offset + 1));
	}

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

	private void placeFaceCentres (WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, BlockPos origin, int radius, int yo) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		tryPlaceLeaf(level, foliageSetter, random, config, pos.setWithOffset(origin, radius, yo, 0));
		tryPlaceLeaf(level, foliageSetter, random, config, pos.setWithOffset(origin, -radius, yo, 0));
		tryPlaceLeaf(level, foliageSetter, random, config, pos.setWithOffset(origin, 0, yo, radius));
		tryPlaceLeaf(level, foliageSetter, random, config, pos.setWithOffset(origin, 0, yo, -radius));
	}

	private void placeCrossRow (WorldGenLevel level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, BlockPos origin, int radius, int yo, boolean doubleTrunk) {
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
		return false;
	}
}
