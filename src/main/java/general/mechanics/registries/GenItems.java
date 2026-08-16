package general.mechanics.registries;

import general.api.definitions.ItemDefinition;
import general.api.item.PartItem;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.item.ItemRegistry;
import general.api.resources.Resource;
import general.mechanics.item.WireSpoolItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// Order dictates list in creative tab.
public class GenItems extends ItemRegistry {

	public static final  ItemRegistry            INSTANCE = new GenItems();
	public static final  DeferredRegister.Items  REGISTRY = DeferredRegister.createItems(GenAPI.getModId());
	private static final List<ItemDefinition<?>> ITEMS    = new ArrayList<>();

	// Wire
	public static final ItemDefinition<Item>          WIRE_SPOOL          = registerItem("Wire Spool", "spool", Item::new);
	public static final ItemDefinition<WireSpoolItem> COPPER_WIRE_SPOOL   = registerItem("Copper Wire Spool", (properties) -> new WireSpoolItem(properties, Items.COPPER_INGOT));
	public static final ItemDefinition<WireSpoolItem> REDSTONE_WIRE_SPOOL = registerItem("Redstone Wire Spool", (properties) -> new WireSpoolItem(properties, Items.REDSTONE));

	// Misc
	public static final ItemDefinition<Item>     TREE_SAP         = registerItem("Tree Sap", Item::new);
	public static final ItemDefinition<Item>     UNTREATED_RUBBER = registerItem("Untreated Rubber", Item::new);
	public static final ItemDefinition<Item>     RUBBER           = registerItem("Rubber", Item::new);
	public static final ItemDefinition<Item>     SAWDUST          = registerItem("Sawdust", Item::new);
	public static final ItemDefinition<PartItem> WET_PAPER        = registerItem("Wet Paper", properties -> new PartItem(properties) {
		@Override
		public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
			ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1).pattern("WS").pattern("SS").define('W', Tags.Items.BUCKETS_WATER).define('S', SAWDUST.get()).unlockedBy("has_any", criterion).save(consumer);
		}

		@Override
		public ItemLike getCriterionItem () {
			return SAWDUST.get();
		}
	});
	public static final ItemDefinition<PartItem> CARDBOARD        = registerItem("Cardboard", properties -> new PartItem(properties) {
		@Override
		public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
			ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1).pattern("WW").pattern("WW").define('W', WET_PAPER.get()).unlockedBy("has_any", criterion).save(consumer);
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