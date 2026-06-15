package general.mechanics.registries;

import general.api.block.IceBlock;
import general.api.block.plastic.ColoredPlasticBlock;
import general.api.block.plastic.PlasticTypeBlock;
import general.api.definitions.BlockDefinition;
import general.api.item.plastic.PlasticType;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.block.BlockRegistry;
import general.api.resources.Resource;
import general.mechanics.client.block.Ice7Block;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenBlocks extends BlockRegistry {

	public static final BlockRegistry            INSTANCE = new GenBlocks();
	public static final DeferredRegister.Blocks  REGISTRY = DeferredRegister.createBlocks(GenAPI.getModId());
	public static final List<BlockDefinition<?>> BLOCKS   = new ArrayList<>();

	public static final BlockDefinition<PlasticTypeBlock> POLYETHYLENE_BLOCK                    = plasticTypeBlock("Polyethylene Block", PlasticType.POLYETHYLENE);
	public static final BlockDefinition<PlasticTypeBlock> POLYPROPYLENE_BLOCK                   = plasticTypeBlock("Polypropylene Block", PlasticType.POLYPROPYLENE);
	public static final BlockDefinition<PlasticTypeBlock> POLYSTYRENE_BLOCK                     = plasticTypeBlock("Polystyrene Block", PlasticType.POLYSTYRENE);
	public static final BlockDefinition<PlasticTypeBlock> POLYVINYL_CHLORIDE_BLOCK              = plasticTypeBlock("Polyvinyl Chloride Block", PlasticType.POLYVINYL_CHLORIDE);
	public static final BlockDefinition<PlasticTypeBlock> POLYETHYLENE_TEREPHTHALATE_BLOCK      = plasticTypeBlock("Polyethylene Terephthalate Block", PlasticType.POLYETHYLENE_TEREPHTHALATE);
	public static final BlockDefinition<PlasticTypeBlock> ACRYLONITRILE_BUTADIENE_STYRENE_BLOCK = plasticTypeBlock("Acrylonitrile Butadiene Styrene Block", PlasticType.ACRYLONITRILE_BUTADIENE_STYRENE);
	public static final BlockDefinition<PlasticTypeBlock> POLYCARBONATE_BLOCK                   = plasticTypeBlock("Polycarbonate Block", PlasticType.POLYCARBONATE);
	public static final BlockDefinition<PlasticTypeBlock> NYLON_BLOCK                           = plasticTypeBlock("Nylon Block", PlasticType.NYLON);
	public static final BlockDefinition<PlasticTypeBlock> POLYURETHANE_BLOCK                    = plasticTypeBlock("Polyurethane Block", PlasticType.POLYURETHANE);
	public static final BlockDefinition<PlasticTypeBlock> POLYTETRAFLUOROETHYLENE_BLOCK         = plasticTypeBlock("Polytetrafluoroethylene Block", PlasticType.POLYTETRAFLUOROETHYLENE);
	public static final BlockDefinition<PlasticTypeBlock> POLYETHERETHERKETONE_BLOCK            = plasticTypeBlock("Polyetheretherketone Block", PlasticType.POLYETHERETHERKETONE);


	public static final BlockDefinition<IceBlock> ICE2 = registerBlock("Ice II", "ice_2", IceBlock::new);
	public static final BlockDefinition<IceBlock> ICE3 = registerBlock("Ice III", "ice_3", IceBlock::new);
	public static final BlockDefinition<IceBlock> ICE4 = registerBlock("Ice IV", "ice_4", IceBlock::new);
	public static final BlockDefinition<IceBlock> ICE5 = registerBlock("Ice V", "ice_5", IceBlock::new);
	public static final BlockDefinition<IceBlock>  ICE6 = registerBlock("Ice VI", "ice_6", IceBlock::new);
	public static final BlockDefinition<Ice7Block> ICE7 = registerBlock("Ice VII", "ice_7", Ice7Block::new);

	private static String formatColorName(String colorName) {
		String[] words = colorName.split("_");
		StringBuilder formatted = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (i > 0) {
				formatted.append(" ");
			}
			formatted.append(words[i].substring(0, 1).toUpperCase())
					.append(words[i].substring(1));
		}
		return formatted.toString();
	}

	static BlockDefinition<PlasticTypeBlock> plasticTypeBlock (String name, PlasticType plasticType) {
		String resource = name.toLowerCase().replace(' ', '_');

		// Create the main plastic type block.
		var plasticTypeDef = registerBlock(name, resource,
				props -> new PlasticTypeBlock(props, plasticType),
				() -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

		// Register all colored plastic block variants.
		for (DyeColor color : PlasticType.getAllColors()) {
			String coloredName = formatColorName(color.getName()) + " " + name;
			String coloredResource = color.getName().toLowerCase() + "_" + resource;
			registerBlock(coloredName, coloredResource,
					props -> new ColoredPlasticBlock(plasticTypeDef.get(), color, props),
					() -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
		}

		return plasticTypeDef;
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

	/**
	 * Get all colored plastic blocks for a specific plastic type
	 */
	public static List<ColoredPlasticBlock> getColoredPlasticBlocksForType(PlasticType plasticType) {
		List<ColoredPlasticBlock> coloredVariants = new ArrayList<>();
		for (var block : BLOCKS) {
			if (block.get() instanceof ColoredPlasticBlock colored) {
				if (colored.getPlasticType() == plasticType) {
					coloredVariants.add(colored);
				}
			}
		}
		return coloredVariants;
	}

	/**
	 * Get all colored plastic blocks
	 */
	public static List<ColoredPlasticBlock> getAllColoredPlasticBlocks() {
		List<ColoredPlasticBlock> allColored = new ArrayList<>();
		for (var block : BLOCKS) {
			if (block.get() instanceof ColoredPlasticBlock colored) {
				allColored.add(colored);
			}
		}
		return allColored;
	}

	public static List<PlasticTypeBlock> getAllPlasticTypeBlocks() {
		List<PlasticTypeBlock> allPlastic = new ArrayList<>();
		for (var block : BLOCKS) {
			if (block.get() instanceof PlasticTypeBlock plastic) {
				allPlastic.add(plastic);
			}
		}
		return allPlastic;
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
		for (var block : getBlocks()) output.accept(block);
	}
}
