package general.mechanics.registries;

import general.api.crafting.IRecipeProvider;
import general.api.definitions.ItemDefinition;
import general.api.formula.core.Material;
import general.api.item.PartItem;
import general.api.item.materials.*;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.item.ItemRegistry;
import general.api.resources.Resource;
import general.api.tag.CoreTags;
import general.mechanics.materials.Materials;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GenParts extends ItemRegistry {

	public static final  ItemRegistry                                          INSTANCE         = new GenParts();
	public static final  DeferredRegister.Items                                REGISTRY         = DeferredRegister.createItems(GenAPI.getModId());
	private static final List<ItemDefinition<?>>                               ITEMS            = new ArrayList<>();
	private static final Map<ResourceKey<Material>, ItemDefinition<IngotItem>> ELEMENTS_BY_TYPE = new HashMap<>();

	public static final ItemDefinition<IngotItem> STEEL  = ingotBuilder("Steel", properties -> new IngotItem(properties, Materials.STEEL), Materials.STEEL);
	public static final ItemDefinition<IngotItem> COPPER = ingotBuilder("Copper", properties -> new IngotItem(properties, Materials.COPPER), Materials.COPPER);
	public static final ItemDefinition<IngotItem> GOLD   = ingotBuilder("Gold", properties -> new IngotItem(properties, Materials.GOLD), Materials.GOLD);
	public static final ItemDefinition<IngotItem> IRON   = ingotBuilder("Iron", properties -> new IngotItem(properties, Materials.IRON), Materials.IRON);

	public static ItemDefinition<IngotItem> ingotBuilder (String baseName, Function<Item.Properties, IngotItem> factory, ResourceKey<Material> material) {
		ItemDefinition<IngotItem> elementDef = ingot(baseName, factory);
		ELEMENTS_BY_TYPE.put(material, elementDef);
		return elementDef;
	}

	static <T extends IngotItem> ItemDefinition<T> ingot (String name, Function<Item.Properties, T> factory) {
		// Create the main element item
		ItemDefinition<T> ingot = registerItem(String.format("%s Ingot", name), factory);

		// Register the raw item
		String rawName = String.format("Raw %s", name);
		registerItem(rawName, properties -> new RawItem(ingot.get(), properties));

		// Register the nugget item
		String nuggetName = String.format("%s Nugget", name);
		registerItem(nuggetName, properties -> new NuggetItem(ingot.get(), properties));

		// Register the dust item
		String dustName = String.format("%s Dust", name);
		registerItem(dustName, properties -> new DustItem(ingot.get(), properties));

		// Register the plate item
		String plateName = String.format("%s Plate", name);
		registerItem(plateName, properties -> new PlateItem(ingot.get(), properties));

		// Register the pile item
		String pileName = String.format("%s Pile", name);
		registerItem(pileName, properties -> new PileItem(ingot.get(), properties));

		// Register the rod item
		String rodName = String.format("%s Rod", name);
		registerItem(rodName, properties -> new RodItem(ingot.get(), properties));

		String boltName = String.format("%s Bolt", name);
		registerItem(boltName, properties -> new BoltItem(ingot.get(), properties));

		String screwName = String.format("%s Screw", name);
		registerItem(screwName, properties -> new ScrewItem(ingot.get(), properties));

		return ingot;
	}

	public static <T extends Item> ItemDefinition<T> registerItem (final String localizedName, Function<Item.Properties, T> factory) {
		return ItemRegistry.registerItem(INSTANCE, localizedName, Resource.get(new RegistryString(localizedName).getRegistryName()), factory);
	}

	static <T extends Item> ItemDefinition<T> registerItem (final String localizedName, final String unlocalizedName, Function<Item.Properties, T> factory) {
		return ItemRegistry.registerItem(INSTANCE, localizedName, Resource.get(unlocalizedName), factory);
	}

	public static ItemDefinition<IngotItem> getIngot (ResourceKey<Material> material) {
		return ELEMENTS_BY_TYPE.get(material);
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
