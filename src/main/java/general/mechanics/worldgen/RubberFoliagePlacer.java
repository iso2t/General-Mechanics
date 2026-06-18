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
			int depthFromTop = offset - yo;
			int rowsFromBottom = foliageHeight + yo;
			int rowRadius;
			if (rowsFromBottom <= 2) {
				rowRadius = body;                                  // wide dense bulb + flat shelf
			} else {
				rowRadius = Math.min(body - 1, depthFromTop);      // narrow ragged taper -> 0 at the apex
			}
			this.placeLeavesRow(level, foliageSetter, random, config, origin, rowRadius, yo, doubleTrunk);
		}

		tryPlaceLeaf(level, foliageSetter, random, config, origin.above(offset + 1));
	}

	@Override
	public int foliageHeight (RandomSource random, int treeHeight, TreeConfiguration config) {
		return Math.min(treeHeight - 1, this.height.sample(random));
	}

	@Override
	protected boolean shouldSkipLocation (RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		if (currentRadius <= 0) return false;
		if (dx == currentRadius && dz == currentRadius) return true;
		return currentRadius == 1 && random.nextInt(3) == 0;
	}
}
