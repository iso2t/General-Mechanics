package general.mechanics.worldgen;

import general.api.resources.Resource;
import general.mechanics.registries.GenBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.Optional;

public final class GenFeatures {

	private GenFeatures () {
	}

	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Resource.get("rubber_tree"));

	public static final TreeGrower RUBBER = new TreeGrower("rubber", Optional.empty(), Optional.of(RUBBER_TREE), Optional.empty());

	public static void bootstrap (BootstrapContext<ConfiguredFeature<?, ?>> context) {
		FeatureUtils.register(context, RUBBER_TREE, Feature.TREE, rubberTree().build());
	}

	private static TreeConfiguration.TreeConfigurationBuilder rubberTree () {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(GenBlocks.RUBBER_LOG.get()), new StraightTrunkPlacer(7, 1, 0), BlockStateProvider.simple(GenBlocks.RUBBER_LEAVES.get()),
				// body radius 2 (sticks out 2), no offset, a 5-row crown (bulb + short narrow taper).
				new RubberFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), ConstantInt.of(5)), new TwoLayersFeatureSize(2, 0, 2)).ignoreVines();
	}
}
