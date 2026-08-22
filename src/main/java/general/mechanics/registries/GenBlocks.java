package general.mechanics.registries;

import general.api.block.DecorativeBlock;
import general.api.block.IOBlock;
import general.api.block.RecipeProviderBlock;
import general.api.crafting.RecipeGenerationContext;
import general.api.definitions.BlockDefinition;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.block.BlockRegistry;
import general.api.resources.Resource;
import general.mechanics.common.block.cable.CableBlock;
import general.mechanics.common.block.hatch.IOFluidHatch;
import general.mechanics.common.block.hatch.IOItemHatch;
import general.mechanics.common.block.hatch.IONetworkHatch;
import general.mechanics.common.block.hatch.IOPowerHatch;
import general.mechanics.common.block.machine.CokeOvenController;
import general.mechanics.common.block.machine.ElectricFurnaceBlock;
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
	public static final BlockDefinition<PowerInjectorBlock>        POWER_INJECTOR = registerBlock("Power Injector", PowerInjectorBlock::new);
	public static final BlockDefinition<RubberWood.RubberLogBlock> RUBBER_LOG     = registerBlock("Rubber Log", RubberWood.RubberLogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
	public static final BlockDefinition<RubberWood.RubberLogBlock> RUBBER_WOOD    = registerBlock("Rubber Wood", RubberWood.RubberLogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD));
	public static final BlockDefinition<RubberWood.LogBlock>       STRIPPED_RUBBER_LOG   = registerBlock("Stripped Rubber Log", RubberWood.LogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG));
	public static final BlockDefinition<RubberWood.LogBlock>       STRIPPED_RUBBER_WOOD  = registerBlock("Stripped Rubber Wood", RubberWood.LogBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD));
	public static final BlockDefinition<RubberWood.Planks>         RUBBER_PLANKS         = registerBlock("Rubber Planks", RubberWood.Planks::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
	public static final BlockDefinition<TintedParticleLeavesBlock> RUBBER_LEAVES         = registerBlock("Rubber Leaves", props -> new TintedParticleLeavesBlock(0.01F, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));
	public static final BlockDefinition<SaplingBlock>              RUBBER_SAPLING        = registerBlock("Rubber Sapling", props -> new SaplingBlock(GenFeatures.RUBBER, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
	public static final BlockDefinition<FlowerPotBlock> POTTED_RUBBER_SAPLING = registerBlock("Potted Rubber Sapling", props -> new FlowerPotBlock(RUBBER_SAPLING.get(), props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING));
	public static final BlockDefinition<IOItemHatch>    ITEM_INPUT_HATCH      = registerBlock("Item Input Hatch", properties -> new IOItemHatch(properties, IOBlock.IOMode.INPUT));
	public static final BlockDefinition<IOItemHatch>    ITEM_OUTPUT_HATCH     = registerBlock("Item Output Hatch", properties -> new IOItemHatch(properties, IOBlock.IOMode.OUTPUT));
	public static final BlockDefinition<IOFluidHatch>   FLUID_INPUT_HATCH     = registerBlock("Fluid Input Hatch", properties -> new IOFluidHatch(properties, IOBlock.IOMode.INPUT));
	public static final BlockDefinition<IOFluidHatch>   FLUID_OUTPUT_HATCH    = registerBlock("Fluid Output Hatch", properties -> new IOFluidHatch(properties, IOBlock.IOMode.OUTPUT));
	public static final BlockDefinition<IOPowerHatch>   POWER_HATCH           = registerBlock("Power Hatch", IOPowerHatch::new);
	public static final BlockDefinition<IONetworkHatch> NETWORK_HATCH         = registerBlock("Network Hatch", IONetworkHatch::new);

	// Upgrade blocks
	public static final BlockDefinition<DecorativeBlock> IRON_CORE_MATRIX     = registerBlock("Iron Core Matrix", DecorativeBlock::new);
	public static final BlockDefinition<DecorativeBlock> STEEL_CORE_MATRIX    = registerBlock("Steel Core Matrix", DecorativeBlock::new);
	public static final BlockDefinition<DecorativeBlock> TITANIUM_CORE_MATRIX = registerBlock("Titanium Core Matrix", DecorativeBlock::new);
	public static final BlockDefinition<DecorativeBlock> TUNGSTEN_CORE_MATRIX = registerBlock("Tungsten Core Matrix", DecorativeBlock::new);
	public static final BlockDefinition<DecorativeBlock> QUANTUM_CORE_MATRIX  = registerBlock("Quantum Core Matrix", DecorativeBlock::new);

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
