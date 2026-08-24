package general.mechanics.worldgen;

import general.api.resources.Resource;
import general.mechanics.registries.GenBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

public final class GenFeatures {

	private GenFeatures () {
	}

	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Resource.get("rubber_tree"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> LIMESTONE   = ResourceKey.create(Registries.CONFIGURED_FEATURE, Resource.get("limestone"));

	public static final TreeGrower RUBBER = new TreeGrower("rubber", Optional.empty(), Optional.of(RUBBER_TREE), Optional.empty());

	public static final ResourceKey<PlacedFeature> RUBBER_TREE_PLACED     = ResourceKey.create(Registries.PLACED_FEATURE, Resource.get("rubber_tree"));
	public static final ResourceKey<PlacedFeature> LIMESTONE_UPPER_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Resource.get("limestone_upper"));
	public static final ResourceKey<PlacedFeature> LIMESTONE_LOWER_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Resource.get("limestone_lower"));

	public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREES = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Resource.get("add_rubber_trees"));
	public static final ResourceKey<BiomeModifier> ADD_LIMESTONE    = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Resource.get("add_limestone"));

	public static void bootstrap (BootstrapContext<ConfiguredFeature<?, ?>> context) {
		FeatureUtils.register(context, RUBBER_TREE, Feature.TREE, rubberTree().build());
		FeatureUtils.register(context, LIMESTONE, Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), GenBlocks.LIMESTONE.get().defaultBlockState(), 64));
	}

	public static void placedFeatures (BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
		PlacementUtils.register(context, RUBBER_TREE_PLACED, features.getOrThrow(RUBBER_TREE), VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(24), GenBlocks.RUBBER_SAPLING.get()));
		PlacementUtils.register(context, LIMESTONE_UPPER_PLACED, features.getOrThrow(LIMESTONE), RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128)), BiomeFilter.biome());
		PlacementUtils.register(context, LIMESTONE_LOWER_PLACED, features.getOrThrow(LIMESTONE), CountPlacement.of(2), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60)), BiomeFilter.biome());
	}

	public static void biomeModifiers (BootstrapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
		context.register(ADD_RUBBER_TREES, new BiomeModifiers.AddFeaturesBiomeModifier(HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS), biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST), biomes.getOrThrow(Biomes.DARK_FOREST), biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.MEADOW), biomes.getOrThrow(Biomes.WINDSWEPT_FOREST), biomes.getOrThrow(Biomes.WINDSWEPT_HILLS), biomes.getOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS)), HolderSet.direct(features.getOrThrow(RUBBER_TREE_PLACED)), GenerationStep.Decoration.VEGETAL_DECORATION));
		context.register(ADD_LIMESTONE, new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(features.getOrThrow(LIMESTONE_UPPER_PLACED), features.getOrThrow(LIMESTONE_LOWER_PLACED)), GenerationStep.Decoration.UNDERGROUND_ORES));
	}

	private static TreeConfiguration.TreeConfigurationBuilder rubberTree () {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(GenBlocks.RUBBER_LOG.get()), new StraightTrunkPlacer(7, 1, 0), BlockStateProvider.simple(GenBlocks.RUBBER_LEAVES.get()), new RubberFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), ConstantInt.of(5)), new TwoLayersFeatureSize(2, 0, 2)).ignoreVines();
	}
}
