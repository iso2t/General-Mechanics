package general.mechanics.registries;

import general.api.definitions.ItemDefinition;
import general.api.item.PartItem;
import general.api.item.plastic.PlasticItem;
import general.api.item.plastic.PlasticType;
import general.api.item.plastic.PlasticTypeItem;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.item.ItemRegistry;
import general.api.resources.Resource;
import general.mechanics.item.RubberColoredItem;
import general.mechanics.item.RubberItem;
import general.mechanics.item.WireSpoolItem;
import general.mechanics.materials.Materials;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GenItems extends ItemRegistry {

	public static final  ItemRegistry            INSTANCE = new GenItems();
	public static final  DeferredRegister.Items  REGISTRY = DeferredRegister.createItems(GenAPI.getModId());
	private static final List<ItemDefinition<?>> ITEMS    = new ArrayList<>();

	// Wire
	public static final ItemDefinition<Item>          WIRE_SPOOL          = registerItem("Wire Spool", "spool", Item::new);
	public static final ItemDefinition<WireSpoolItem> COPPER_WIRE_SPOOL   = registerItem("Copper Wire Spool", (properties) -> new WireSpoolItem(properties, Items.COPPER_INGOT));
	public static final ItemDefinition<WireSpoolItem> REDSTONE_WIRE_SPOOL = registerItem("Redstone Wire Spool", (properties) -> new WireSpoolItem(properties, Items.REDSTONE));

	// Plastic Types
	public static final ItemDefinition<PlasticTypeItem> POLYETHYLENE                    = plasticType("Polyethylene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYETHYLENE, Materials.POLYETHYLENE));
	public static final ItemDefinition<PlasticTypeItem> POLYPROPYLENE                   = plasticType("Polypropylene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYPROPYLENE, Materials.POLYPROPYLENE));
	public static final ItemDefinition<PlasticTypeItem> POLYSTYRENE                     = plasticType("Polystyrene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYSTYRENE, Materials.POLYSTYRENE));
	public static final ItemDefinition<PlasticTypeItem> POLYVINYL_CHLORIDE              = plasticType("Polyvinyl Chloride", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYVINYL_CHLORIDE, Materials.POLYVINYL_CHLORIDE));
	public static final ItemDefinition<PlasticTypeItem> POLYETHYLENE_TEREPHTHALATE      = plasticType("Polyethylene Terephthalate", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYETHYLENE_TEREPHTHALATE, Materials.POLYETHYLENE_TEREPHTHALATE));
	public static final ItemDefinition<PlasticTypeItem> ACRYLONITRILE_BUTADIENE_STYRENE = plasticType("Acrylonitrile Butadiene Styrene", (properties) -> new PlasticTypeItem(properties, PlasticType.ACRYLONITRILE_BUTADIENE_STYRENE, Materials.ACRYLONITRILE_BUTADIENE_STYRENE));
	public static final ItemDefinition<PlasticTypeItem> POLYCARBONATE                   = plasticType("Polycarbonate", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYCARBONATE, Materials.POLYCARBONATE));
	public static final ItemDefinition<PlasticTypeItem> NYLON                           = plasticType("Nylon", (properties) -> new PlasticTypeItem(properties, PlasticType.NYLON, Materials.NYLON));
	public static final ItemDefinition<PlasticTypeItem> POLYURETHANE                    = plasticType("Polyurethane", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYURETHANE, Materials.POLYURETHANE));
	public static final ItemDefinition<PlasticTypeItem> POLYTETRAFLUOROETHYLENE         = plasticType("Polytetrafluoroethylene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYTETRAFLUOROETHYLENE, Materials.POLYTETRAFLUOROETHYLENE));
	public static final ItemDefinition<PlasticTypeItem> POLYETHERETHERKETONE            = plasticType("Polyetheretherketone", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYETHERETHERKETONE, Materials.POLYETHERETHERKETONE));
	public static final ItemDefinition<Item> TREE_SAP = registerItem("Tree Sap", Item::new);

	// Rubber
	public static final ItemDefinition<RubberItem>      ISOPRENE                        = registerRubber("Isoprene", properties -> new RubberItem(properties, Materials.ISOPRENE));

	// Misc
	public static final ItemDefinition<Item> SAWDUST = registerItem("Sawdust", Item::new);
	public static final ItemDefinition<PartItem> WET_PAPER = registerItem("Wet Paper", properties -> new PartItem(properties) {
		@Override
		public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
			ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
					.pattern("WS")
					.pattern("SS")
					.define('W', Tags.Items.BUCKETS_WATER)
					.define('S', SAWDUST.get())
					.unlockedBy("has_any", criterion)
					.save(consumer);
		}

		@Override
		public ItemLike getCriterionItem () {
			return SAWDUST.get();
		}
	});
	public static final ItemDefinition<PartItem> CARDBOARD = registerItem("Cardboard", properties -> new PartItem(properties) {
		@Override
		public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
			ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
					.pattern("WW")
					.pattern("WW")
					.define('W', WET_PAPER.get())
					.unlockedBy("has_any", criterion)
					.save(consumer);
		}

		@Override
		public ItemLike getCriterionItem () {
			return WET_PAPER.get();
		}
	});

	public static <T extends Item> ItemDefinition<T> registerItem (final String localizedName, Function<Item.Properties, T> factory) {
		return ItemRegistry.registerItem(INSTANCE, localizedName, Resource.get(new RegistryString(localizedName).getRegistryName()), factory);
	}

	static <T extends Item> ItemDefinition<T> registerItem (final String localizedName, final String unlocalizedName, Function<Item.Properties, T> factory) {
		return ItemRegistry.registerItem(INSTANCE, localizedName, Resource.get(unlocalizedName), factory);
	}

	static <T extends RubberItem> ItemDefinition<T> registerRubber (final String localizedName, Function<Item.Properties, T> factory) {
		var definition = registerItem(localizedName, factory);
		for (DyeColor color : PlasticType.getAllColors()) {
			ItemRegistry.registerItem(INSTANCE, String.format("%s %s", formatColorName(color.getName()), localizedName), Resource.get(new RegistryString(String.format("%s %s", formatColorName(color.getName()), localizedName)).getRegistryName()),
					properties -> new RubberColoredItem(definition.get(), color, properties));
		}
		return definition;
	}

	static <T extends PlasticTypeItem> ItemDefinition<T> plasticType (final String localizedName, Function<Item.Properties, T> factory) {
		var definition = registerItem(localizedName, factory);

		for (DyeColor color : PlasticType.getAllColors()) {
			ItemRegistry.registerItem(INSTANCE, String.format("%s %s", formatColorName(color.getName()), localizedName), Resource.get(new RegistryString(String.format("%s %s", formatColorName(color.getName()), localizedName)).getRegistryName()), properties -> new PlasticItem(definition.get(), color, properties));
		}
		return definition;
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

	public static List<PlasticItem> getAllColoredPlastics () {
		List<PlasticItem> allColored = new ArrayList<>();
		for (var item : ITEMS) {
			if (item.get() instanceof PlasticItem colored) {
				allColored.add(colored);
			}
		}
		return allColored;
	}

	/**
	 * Get all colored variants for a specific plastic type
	 */
	public static List<Item> getColoredPlasticsForType (PlasticType plasticType) {
		List<Item> coloredVariants = new ArrayList<>();
		for (var item : ITEMS) {
			if (item.get() instanceof PlasticItem colored) {
				if (colored.getParent().getPlasticType() == plasticType) {
					coloredVariants.add(colored);
					if (!coloredVariants.contains(colored.getParent())) coloredVariants.add(colored.getParent());
				}
			}
		}
		return coloredVariants;
	}

	public static List<Item> getAllPlasticsForColor (DyeColor color) {
		List<Item> coloredVariants = new ArrayList<>();
		for (var item : ITEMS) {
			if (item.get() instanceof PlasticItem colored) {
				if (colored.getColor() == color) {
					coloredVariants.add(colored);
				}
			}
		}
		return coloredVariants;
	}

	@Override
	public DeferredRegister.Items getRegistry () {
		return REGISTRY;
	}

	@Override
	public List<ItemDefinition<?>> getItems () {
		return ITEMS;
	}

	@Override
	public void buildDisplayItems (CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		for (var item : getItems()) output.accept(item);
	}
}