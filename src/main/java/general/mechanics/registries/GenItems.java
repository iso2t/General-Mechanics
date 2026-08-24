package general.mechanics.registries;

import com.google.common.base.Preconditions;
import general.api.crafting.RecipeGenerationContext;
import general.api.definitions.ItemDefinition;
import general.api.item.PartItem;
import general.api.item.RecipeProviderItem;
import general.api.item.materials.*;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.item.ItemRegistry;
import general.api.resources.Resource;
import general.mechanics.Mechanics;
import general.mechanics.common.item.NetworkDebuggerItem;
import general.mechanics.common.item.StampingDieItem;
import general.mechanics.item.WireSpoolItem;
import general.mechanics.item.food.HardBoiledEggItem;
import general.mechanics.item.food.HoneyBunItem;
import general.mechanics.item.tools.SawItem;
import general.mechanics.item.tools.WireCuttersItem;
import general.mechanics.item.tools.WrenchItem;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.FuelValues;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

	// Tools
	public static final ItemDefinition<WrenchItem>          WRENCH       = registerItem("Wrench", WrenchItem::new);
	public static final ItemDefinition<WireCuttersItem>     WIRE_CUTTERS = registerItem("Wire Cutters", WireCuttersItem::new);
	public static final ItemDefinition<SawItem>             SAW          = registerItem("Saw", SawItem::new);
	public static final ItemDefinition<NetworkDebuggerItem> GUIDE        = registerItem("Service Terminal", "guide", NetworkDebuggerItem::new);

	// Ingots
	public static final ItemDefinition<IngotItem> STEEL    = Ingot.registerIngot("Steel", 0xFF71797E);
	public static final ItemDefinition<IngotItem> TITANIUM = Ingot.registerIngot("Titanium", 0xFF5B798E);
	public static final ItemDefinition<IngotItem> TUNGSTEN = Ingot.registerIngot("Tungsten", 0xFFB5AC9F);

	// Material forms backed by vanilla ingots. Iron and gold reuse Minecraft's existing nuggets.
	public static final ItemDefinition<DustItem>   COPPER_DUST    = Ingot.itemDust("Copper Dust", "copper_dust", properties -> new DustItem(0xFFC47C52, properties));
	public static final ItemDefinition<NuggetItem> COPPER_NUGGET  = Ingot.itemElementNugget("Copper Nugget", "copper_nugget", properties -> new NuggetItem(0xFFC47C52, properties));
	public static final ItemDefinition<PlateItem>  COPPER_PLATE   = Ingot.itemPlate("Copper Plate", "copper_plate", properties -> new PlateItem(0xFFC47C52, properties));
	public static final ItemDefinition<GearItem>   COPPER_GEAR    = Ingot.itemGear("Copper Gear", "copper_gear", properties -> new GearItem(0xFFC47C52, properties));
	public static final ItemDefinition<DustItem>   IRON_DUST      = Ingot.itemDust("Iron Dust", "iron_dust", properties -> new DustItem(0xFFD8D8D8, properties));
	public static final ItemDefinition<PlateItem>  IRON_PLATE     = Ingot.itemPlate("Iron Plate", "iron_plate", properties -> new PlateItem(0xFFD8D8D8, properties));
	public static final ItemDefinition<GearItem>   IRON_GEAR      = Ingot.itemGear("Iron Gear", "iron_gear", properties -> new GearItem(0xFFD8D8D8, properties));
	public static final ItemDefinition<DustItem>   GOLD_DUST      = Ingot.itemDust("Gold Dust", "gold_dust", properties -> new DustItem(0xFFFFD34E, properties));
	public static final ItemDefinition<PlateItem>  GOLD_PLATE     = Ingot.itemPlate("Gold Plate", "gold_plate", properties -> new PlateItem(0xFFFFD34E, properties));
	public static final ItemDefinition<GearItem>   GOLD_GEAR      = Ingot.itemGear("Gold Gear", "gold_gear", properties -> new GearItem(0xFFFFD34E, properties));
	public static final ItemDefinition<DustItem>   NETHERITE_DUST = Ingot.itemDust("Netherite Dust", "netherite_dust", properties -> new DustItem(0xFF4A3F42, properties));
	public static final ItemDefinition<PlateItem>  NETHERITE_PLATE = Ingot.itemPlate("Netherite Plate", "netherite_plate", properties -> new PlateItem(0xFF4A3F42, properties));
	public static final ItemDefinition<GearItem>   NETHERITE_GEAR = Ingot.itemGear("Netherite Gear", "netherite_gear", properties -> new GearItem(0xFF4A3F42, properties));

	// Reusable Stamping Press tools
	public static final ItemDefinition<StampingDieItem> GEAR_DIE   = registerItem("Gear Die", properties -> new StampingDieItem(properties, StampingDieItem.Shape.GEAR));
	public static final ItemDefinition<StampingDieItem> NUGGET_DIE = registerItem("Nugget Die", properties -> new StampingDieItem(properties, StampingDieItem.Shape.NUGGET));

	// Misc
	public static final ItemDefinition<HoneyBunItem>       HONEY_BUN        = registerItem("Honey Bun", HoneyBunItem::new);
	public static final ItemDefinition<HardBoiledEggItem>  HARD_BOILED_EGG  = registerItem("Hard Boiled Egg", HardBoiledEggItem::new);
	public static final ItemDefinition<Item>               TREE_SAP         = registerItem("Tree Sap", Item::new);
	public static final ItemDefinition<Item>               UNTREATED_RUBBER = registerItem("Untreated Rubber", Item::new);
	public static final ItemDefinition<Item>               RUBBER           = registerItem("Rubber", Item::new);
	public static final ItemDefinition<Item>               SAWDUST          = registerItem("Sawdust", Item::new);
	public static final ItemDefinition<RecipeProviderItem> COAL_COKE        = registerItem("Coal Coke", properties -> new RecipeProviderItem(properties) {
		@Override
		public void generateRecipes (RecipeGenerationContext context) {
		}

		@Override
		public ItemLike getRecipeUnlockItem () {
			return Items.COAL;
		}

		@Override
		public int getBurnTime (@NonNull ItemStack itemStack, @Nullable RecipeType<?> recipeType, @NonNull FuelValues fuelValues) {
			return 3_200;
		}


	});
	public static final ItemDefinition<PartItem>           WET_PAPER        = registerItem("Wet Paper", properties -> new PartItem(properties) {
		@Override
		public void generateRecipes (RecipeGenerationContext context) {
			context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.MISC, this, 1).pattern("WS").pattern("SS").define('W', Tags.Items.BUCKETS_WATER).define('S', SAWDUST.get()));
		}

		@Override
		public ItemLike getRecipeUnlockItem () {
			return SAWDUST.get();
		}
	});
	public static final ItemDefinition<PartItem>           CARDBOARD        = registerItem("Cardboard", properties -> new PartItem(properties) {
		@Override
		public void generateRecipes (RecipeGenerationContext context) {
			context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.MISC, this, 1).pattern("WW").pattern("WW").define('W', WET_PAPER.get()));
		}

		@Override
		public ItemLike getRecipeUnlockItem () {
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

	private static class Ingot {

		private static ItemDefinition<IngotItem> registerIngot (final String name, int color) {
			return ingot(name + " Ingot", properties -> new IngotItem(properties, color));
		}

		static <T extends IngotItem> ItemDefinition<T> ingot (String name, Function<Item.Properties, T> factory) {
			// Create the main element item
			ItemDefinition<T> elementDef = registerItem(name, factory);

			// Register the raw item
			String rawName = "Raw " + name.replace(" Ingot", "");
			String rawResourceName = name.toLowerCase().replace(' ', '_').replace("_ingot", "_raw");
			itemElementRaw(rawName, rawResourceName, properties -> new RawItem(elementDef.get(), properties));

			// Register the nugget item
			String nuggetName = name.replace(" Ingot", " Nugget");
			String nuggetResourceName = name.toLowerCase().replace(' ', '_').replace("_ingot", "_nugget");
			itemElementNugget(nuggetName, nuggetResourceName, properties -> new NuggetItem(elementDef.get(), properties));

			// Register the dust item
			String dustName = name.replace(" Ingot", " Dust");
			String dustResourceName = name.toLowerCase().replace(' ', '_').replace("_ingot", "_dust");
			itemDust(dustName, dustResourceName, properties -> new DustItem(elementDef.get(), properties));

			// Register the plate item
			String plateName = name.replace(" Ingot", " Plate");
			String plateResourceName = name.toLowerCase().replace(' ', '_').replace("_ingot", "_plate");
			itemPlate(plateName, plateResourceName, properties -> new PlateItem(elementDef.get(), properties));

			// Register the gear item
			String gearName = name.replace(" Ingot", " Gear");
			String gearResourceName = name.toLowerCase().replace(' ', '_').replace("_ingot", "_gear");
			itemGear(gearName, gearResourceName, properties -> new GearItem(elementDef.get(), properties));

			return elementDef;
		}

		static ItemDefinition<DustItem> itemDust (String name, String resourceName, Function<Item.Properties, DustItem> factory) {
			return itemDust(name, Resource.getMainMod(resourceName), factory);
		}

		static ItemDefinition<DustItem> itemDust (String name, Identifier id, Function<Item.Properties, DustItem> factory) {
			Preconditions.checkArgument(id.getNamespace().equals(Mechanics.MOD_ID), "Can only register items in " + Mechanics.MOD_ID);
			var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));

			ITEMS.add(definition);
			return definition;
		}

		static ItemDefinition<PlateItem> itemPlate (String name, String resourceName, Function<Item.Properties, PlateItem> factory) {
			return itemPlate(name, Resource.getMainMod(resourceName), factory);
		}

		static ItemDefinition<PlateItem> itemPlate (String name, Identifier id, Function<Item.Properties, PlateItem> factory) {
			Preconditions.checkArgument(id.getNamespace().equals(Mechanics.MOD_ID), "Can only register items in " + Mechanics.MOD_ID);
			var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));

			ITEMS.add(definition);
			return definition;
		}

		static ItemDefinition<GearItem> itemGear (String name, String resourceName, Function<Item.Properties, GearItem> factory) {
			return itemGear(name, Resource.getMainMod(resourceName), factory);
		}

		static ItemDefinition<GearItem> itemGear (String name, Identifier id, Function<Item.Properties, GearItem> factory) {
			Preconditions.checkArgument(id.getNamespace().equals(Mechanics.MOD_ID), "Can only register items in " + Mechanics.MOD_ID);
			var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));

			ITEMS.add(definition);
			return definition;
		}

		static ItemDefinition<RawItem> itemElementRaw (String name, String resourceName, Function<Item.Properties, RawItem> factory) {
			return itemElementRaw(name, Resource.getMainMod(resourceName), factory);
		}

		static ItemDefinition<RawItem> itemElementRaw (String name, Identifier id, Function<Item.Properties, RawItem> factory) {
			Preconditions.checkArgument(id.getNamespace().equals(Mechanics.MOD_ID), "Can only register items in " + Mechanics.MOD_ID);
			var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));

			ITEMS.add(definition);
			return definition;
		}

		static ItemDefinition<NuggetItem> itemElementNugget (String name, String resourceName, Function<Item.Properties, NuggetItem> factory) {
			return itemElementNugget(name, Resource.getMainMod(resourceName), factory);
		}

		static ItemDefinition<NuggetItem> itemElementNugget (String name, Identifier id, Function<Item.Properties, NuggetItem> factory) {
			Preconditions.checkArgument(id.getNamespace().equals(Mechanics.MOD_ID), "Can only register items in " + Mechanics.MOD_ID);
			var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));

			ITEMS.add(definition);
			return definition;
		}

	}

}
