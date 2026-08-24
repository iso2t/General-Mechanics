package general.mechanics.registries;

import general.api.block.DecorativeBlock;
import general.api.block.IOBlock;
import general.api.block.RecipeProviderBlock;
import general.api.block.materials.MetalBlock;
import general.api.crafting.RecipeGenerationContext;
import general.api.definitions.BlockDefinition;
import general.api.item.materials.IngotItem;
import general.api.machine.upgrade.MachineUpgradeProfile;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.block.BlockRegistry;
import general.api.resources.Resource;
import general.mechanics.common.block.cable.CableBlock;
import general.mechanics.common.block.hatch.IOFluidHatch;
import general.mechanics.common.block.hatch.IOItemHatch;
import general.mechanics.common.block.hatch.IONetworkHatch;
import general.mechanics.common.block.hatch.IOPowerHatch;
import general.mechanics.common.block.machine.*;
import general.mechanics.common.block.misc.CoreMatrixBlock;
import general.mechanics.common.block.misc.EncasedFluidBlock;
import general.mechanics.common.block.misc.HeatingElementBlock;
import general.mechanics.common.block.misc.RubberWood;
import general.mechanics.common.block.network.NetworkConnectorBlock;
import general.mechanics.common.block.network.PowerInjectorBlock;
import general.mechanics.worldgen.GenFeatures;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenBlocks extends BlockRegistry {

	public static final BlockRegistry            INSTANCE = new GenBlocks();
	public static final DeferredRegister.Blocks  REGISTRY = DeferredRegister.createBlocks(GenAPI.getModId());
	public static final List<BlockDefinition<?>> BLOCKS   = new ArrayList<>();

	public static final BlockDefinition<CableBlock>                CABLE                 = registerBlock("Cable", CableBlock::new);
	public static final BlockDefinition<NetworkConnectorBlock>     NETWORK_CONNECTOR     = registerBlock("Network Connector", NetworkConnectorBlock::new);
	public static final BlockDefinition<PowerInjectorBlock>        POWER_INJECTOR        = registerBlock("Power Injector", PowerInjectorBlock::new);
	public static final BlockDefinition<RubberWood.RubberLogBlock> RUBBER_LOG            = registerBlock("Rubber Log", RubberWood.RubberLogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
	public static final BlockDefinition<RubberWood.RubberLogBlock> RUBBER_WOOD           = registerBlock("Rubber Wood", RubberWood.RubberLogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));
	public static final BlockDefinition<RubberWood.LogBlock>       STRIPPED_RUBBER_LOG   = registerBlock("Stripped Rubber Log", RubberWood.LogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG));
	public static final BlockDefinition<RubberWood.LogBlock>       STRIPPED_RUBBER_WOOD  = registerBlock("Stripped Rubber Wood", RubberWood.LogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD));
	public static final BlockDefinition<RubberWood.Planks>         RUBBER_PLANKS         = registerBlock("Rubber Planks", RubberWood.Planks::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
	public static final BlockDefinition<TintedParticleLeavesBlock> RUBBER_LEAVES         = registerBlock("Rubber Leaves", props -> new TintedParticleLeavesBlock(0.01F, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));
	public static final BlockDefinition<SaplingBlock>              RUBBER_SAPLING        = registerBlock("Rubber Sapling", props -> new SaplingBlock(GenFeatures.RUBBER, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
	public static final BlockDefinition<FlowerPotBlock>            POTTED_RUBBER_SAPLING = registerBlock("Potted Rubber Sapling", props -> new FlowerPotBlock(RUBBER_SAPLING.get(), props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING));
	public static final BlockDefinition<IOItemHatch>               ITEM_INPUT_HATCH      = registerBlock("Item Input Hatch", properties -> new IOItemHatch(properties, IOBlock.IOMode.INPUT));
	public static final BlockDefinition<IOItemHatch>               ITEM_OUTPUT_HATCH     = registerBlock("Item Output Hatch", properties -> new IOItemHatch(properties, IOBlock.IOMode.OUTPUT));
	public static final BlockDefinition<IOFluidHatch>              FLUID_INPUT_HATCH     = registerBlock("Fluid Input Hatch", properties -> new IOFluidHatch(properties, IOBlock.IOMode.INPUT));
	public static final BlockDefinition<IOFluidHatch>              FLUID_OUTPUT_HATCH    = registerBlock("Fluid Output Hatch", properties -> new IOFluidHatch(properties, IOBlock.IOMode.OUTPUT));
	public static final BlockDefinition<IOPowerHatch>              POWER_HATCH           = registerBlock("Power Hatch", IOPowerHatch::new);
	public static final BlockDefinition<IONetworkHatch>            NETWORK_HATCH         = registerBlock("Network Hatch", IONetworkHatch::new);
	public static final BlockDefinition<EncasedFluidBlock>         ENCASED_WATER         = registerBlock("Encased Water", properties -> new EncasedFluidBlock(properties, Fluids.WATER));
	public static final BlockDefinition<EncasedFluidBlock>         ENCASED_LAVA          = registerBlock("Encased Lava", properties -> new EncasedFluidBlock(properties, Fluids.LAVA));

	// Misc
	public static final BlockDefinition<DecorativeBlock> LIMESTONE                 = registerBlock("Limestone", properties -> new DecorativeBlock(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<SlabBlock>       LIMESTONE_SLAB            = registerBlock("Limestone Slab", properties -> new SlabBlock(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<StairBlock>      LIMESTONE_STAIRS          = registerBlock("Limestone Stairs", properties -> new StairBlock(LIMESTONE.get().defaultBlockState(), properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<DecorativeBlock> LIMESTONE_BRICKS          = registerBlock("Limestone Bricks", properties -> new DecorativeBlock(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<SlabBlock>       LIMESTONE_BRICK_SLAB      = registerBlock("Limestone Bricks Slab", properties -> new SlabBlock(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<StairBlock>      LIMESTONE_BRICK_STAIRS    = registerBlock("Limestone Brick Stairs", properties -> new StairBlock(LIMESTONE_BRICKS.get().defaultBlockState(), properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<DecorativeBlock> LIMESTONE_POLISHED        = registerBlock("Polished Limestone", "limestone_polished", properties -> new DecorativeBlock(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<SlabBlock>       LIMESTONE_POLISHED_SLAB   = registerBlock("Polished Limestone Slab", "limestone_polished_slab", properties -> new SlabBlock(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
	public static final BlockDefinition<StairBlock>      LIMESTONE_POLISHED_STAIRS = registerBlock("Polished Limestone Stairs", "limestone_polished_stairs", properties -> new StairBlock(LIMESTONE_POLISHED.get().defaultBlockState(), properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));

	public static final List<BlockFamilyDefinition> LIMESTONE_FAMILIES = List.of(new BlockFamilyDefinition(LIMESTONE, LIMESTONE_SLAB, LIMESTONE_STAIRS), new BlockFamilyDefinition(LIMESTONE_BRICKS, LIMESTONE_BRICK_SLAB, LIMESTONE_BRICK_STAIRS), new BlockFamilyDefinition(LIMESTONE_POLISHED, LIMESTONE_POLISHED_SLAB, LIMESTONE_POLISHED_STAIRS));

	// Upgrade blocks
	public static final BlockDefinition<CoreMatrixBlock> IRON_CORE_MATRIX     = registerBlock("Iron Core Matrix", properties -> new CoreMatrixBlock(coreMatrixProfile(1.25D), properties));
	public static final BlockDefinition<CoreMatrixBlock> STEEL_CORE_MATRIX    = registerBlock("Steel Core Matrix", properties -> new CoreMatrixBlock(coreMatrixProfile(2.0D), properties));
	public static final BlockDefinition<CoreMatrixBlock> TITANIUM_CORE_MATRIX = registerBlock("Titanium Core Matrix", properties -> new CoreMatrixBlock(coreMatrixProfile(4.0D), properties));
	public static final BlockDefinition<CoreMatrixBlock> TUNGSTEN_CORE_MATRIX = registerBlock("Tungsten Core Matrix", properties -> new CoreMatrixBlock(coreMatrixProfile(32.0D), properties));
	public static final BlockDefinition<CoreMatrixBlock> QUANTUM_CORE_MATRIX  = registerBlock("Quantum Core Matrix", properties -> new CoreMatrixBlock(coreMatrixProfile(64.0D), properties));

	// Misc Multiblock
	public static final BlockDefinition<MachineCasingBlock> MACHINE_CASING = registerBlock("Machine Casing", MachineCasingBlock::new);
	public static final BlockDefinition<MachineFrameBlock>  MACHINE_FRAME  = registerBlock("Machine Frame", MachineFrameBlock::new);

	// Coke Oven
	public static final BlockDefinition<RecipeProviderBlock> COKE_OVEN_BRICKS     = registerBlock("Coke Oven Bricks", props -> new RecipeProviderBlock(props) {
		@Override
		public void generateRecipes (RecipeGenerationContext context) {
			context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.MISC, this, 4).pattern("CSC").pattern("GWG").pattern("CSC").define('C', Blocks.CLAY).define('S', Tags.Items.SANDS).define('G', Tags.Items.GRAVELS).define('W', Tags.Items.BUCKETS_WATER), "coke_oven_bricks");
		}

		@Override
		public ItemLike getRecipeUnlockItem () {
			return Blocks.SAND;
		}
	}, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS));
	public static final BlockDefinition<CokeOvenController>  COKE_OVEN_CONTROLLER = registerBlock("Coke Oven Controller", CokeOvenController::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS));

	// Machines
	public static final BlockDefinition<ElectricFurnaceBlock> ELECTRIC_FURNACE = registerBlock("Electric Furnace", ElectricFurnaceBlock::new);
	public static final BlockDefinition<StampingPressBlock>   STAMPING_PRESS   = registerBlock("Stamping Press", StampingPressBlock::new);
	public static final BlockDefinition<MaceratorBlock>       MACERATOR        = registerBlock("Macerator", MaceratorBlock::new);
	public static final BlockDefinition<HeatingElementBlock>  HEATING_ELEMENT  = registerBlock("Heating Element", HeatingElementBlock::new);

	// Metal Blocks
	public static final BlockDefinition<MetalBlock> STEEL_BLOCK    = registerMetalBlock("Steel", GenItems.STEEL);
	public static final BlockDefinition<MetalBlock> TITANIUM_BLOCK = registerMetalBlock("Titanium", GenItems.TITANIUM);
	public static final BlockDefinition<MetalBlock> TUNGSTEN_BLOCK = registerMetalBlock("Tungsten", GenItems.TUNGSTEN);

	private static BlockDefinition<MetalBlock> registerMetalBlock (String materialName, Supplier<? extends IngotItem> ingot) {
		return registerBlock("Block of " + materialName, properties -> new MetalBlock(properties, ingot));
	}

	public record BlockFamilyDefinition(BlockDefinition<? extends Block> base, BlockDefinition<? extends SlabBlock> slab, BlockDefinition<? extends StairBlock> stairs) {
	}

	private static MachineUpgradeProfile coreMatrixProfile (double multiplier) {
		return MachineUpgradeProfile.builder().processingSpeed(multiplier).energy(multiplier, multiplier, multiplier).fluid(multiplier, multiplier).build();
	}

	private static String formatColorName (String colorName) {
		String[] words = colorName.split("_");
		StringBuilder formatted = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (i > 0) {
				formatted.append(" ");
			}
			formatted.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1));
		}
		return formatted.toString();
	}

	public static <T extends Block> BlockDefinition<T> registerBlock (final String localizedName, final String unlocalizedName, final Function<BlockBehaviour.Properties, T> factory) {
		return BlockRegistry.registerBlock(INSTANCE, GenItems.INSTANCE, localizedName, Resource.get(unlocalizedName), factory);
	}

	public static <T extends Block> BlockDefinition<T> registerBlock (final String localizedName, final String unlocalizedName, final Function<BlockBehaviour.Properties, T> factory, final Supplier<BlockBehaviour.Properties> baseProperties) {
		return BlockRegistry.registerBlock(INSTANCE, GenItems.INSTANCE, localizedName, Resource.get(unlocalizedName), factory, baseProperties);
	}

	public static <T extends Block> BlockDefinition<T> registerBlock (final String localizedName, final Function<BlockBehaviour.Properties, T> factory) {
		return BlockRegistry.registerBlock(INSTANCE, GenItems.INSTANCE, localizedName, Resource.get(new RegistryString(localizedName).getRegistryName()), factory);
	}

	public static <T extends Block> BlockDefinition<T> registerBlock (final String localizedName, final Function<BlockBehaviour.Properties, T> factory, final Supplier<BlockBehaviour.Properties> baseProperties) {
		return BlockRegistry.registerBlock(INSTANCE, GenItems.INSTANCE, localizedName, Resource.get(new RegistryString(localizedName).getRegistryName()), factory, baseProperties);
	}

	@Override
	public DeferredRegister.Blocks getRegistry () {
		return REGISTRY;
	}

	@Override
	public List<BlockDefinition<?>> getBlocks () {
		return BLOCKS;
	}

	@Override
	public void buildDisplayItems (CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		for (var block : getBlocks()) {
			if (!dnaTab().contains(block)) output.accept(block);
		}
	}

	@Override
	protected List<BlockDefinition<?>> dnaTab () {
		return List.of(POTTED_RUBBER_SAPLING);
	}
}
