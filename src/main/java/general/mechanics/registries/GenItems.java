package general.mechanics.registries;

import general.api.definitions.ItemDefinition;
import general.api.formula.GenFormula;
import general.api.formula.tooltip.FormulaTooltip;
import general.api.item.plastic.PlasticItem;
import general.api.item.plastic.PlasticType;
import general.api.item.plastic.PlasticTypeItem;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.item.ItemRegistry;
import general.api.resources.Resource;
import general.mechanics.formula.GMMaterials;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GenItems extends ItemRegistry {

	public static final  ItemRegistry            INSTANCE = new GenItems();
	public static final  DeferredRegister.Items  REGISTRY = DeferredRegister.createItems(GenAPI.getModId());
	private static final List<ItemDefinition<?>> ITEMS    = new ArrayList<>();

	// Plastic Types
	public static final ItemDefinition<PlasticTypeItem> POLYETHYLENE  = plasticType("Polyethylene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYETHYLENE, GMMaterials.POLYETHYLENE));
	public static final ItemDefinition<PlasticTypeItem> POLYPROPYLENE = plasticType("Polypropylene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYPROPYLENE, GMMaterials.POLYPROPYLENE));
	public static final ItemDefinition<PlasticTypeItem> POLYSTYRENE = plasticType("Polystyrene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYSTYRENE, GMMaterials.POLYSTYRENE));
	public static final ItemDefinition<PlasticTypeItem> POLYVINYL_CHLORIDE = plasticType("Polyvinyl Chloride", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYVINYL_CHLORIDE, GMMaterials.POLYVINYL_CHLORIDE));
	public static final ItemDefinition<PlasticTypeItem> POLYETHYLENE_TEREPHTHALATE = plasticType("Polyethylene Terephthalate", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYETHYLENE_TEREPHTHALATE, GMMaterials.POLYETHYLENE_TEREPHTHALATE));
	public static final ItemDefinition<PlasticTypeItem> ACRYLONITRILE_BUTADIENE_STYRENE = plasticType("Acrylonitrile Butadiene Styrene", (properties) -> new PlasticTypeItem(properties, PlasticType.ACRYLONITRILE_BUTADIENE_STYRENE, GMMaterials.ACRYLONITRILE_BUTADIENE_STYRENE));
	public static final ItemDefinition<PlasticTypeItem> POLYCARBONATE = plasticType("Polycarbonate", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYCARBONATE, GMMaterials.POLYCARBONATE));
	public static final ItemDefinition<PlasticTypeItem> NYLON = plasticType("Nylon", (properties) -> new PlasticTypeItem(properties, PlasticType.NYLON, GMMaterials.NYLON));
	public static final ItemDefinition<PlasticTypeItem> POLYURETHANE = plasticType("Polyurethane", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYURETHANE, GMMaterials.POLYURETHANE));
	public static final ItemDefinition<PlasticTypeItem> POLYTETRAFLUOROETHYLENE = plasticType("Polytetrafluoroethylene", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYTETRAFLUOROETHYLENE, GMMaterials.POLYTETRAFLUOROETHYLENE));
	public static final ItemDefinition<PlasticTypeItem> POLYETHERETHERKETONE = plasticType("Polyetheretherketone", (properties) -> new PlasticTypeItem(properties, PlasticType.POLYETHERETHERKETONE, GMMaterials.POLYETHERETHERKETONE));

	public static <T extends Item> ItemDefinition<T> registerItem (final String localizedName, Function<Item.Properties, T> factory) {
		return ItemRegistry.registerItem(INSTANCE, localizedName, Resource.get(new RegistryString(localizedName).getRegistryName()), factory);
	}

	static <T extends PlasticTypeItem> ItemDefinition<T> plasticType (final String localizedName, Function<Item.Properties, T> factory) {
		var definition = registerItem(localizedName, factory);

		for (DyeColor color : PlasticType.getAllColors()) {
			var coloredName = formatColorName(color.getName()) + " " + localizedName;
			var coloredResourceName = color.getName().toLowerCase() + "_" + localizedName.toLowerCase().replace(" ", "_");
			ItemRegistry.registerItem(INSTANCE, coloredName, Resource.get(coloredResourceName),
					properties -> new PlasticItem(definition.get(), color, properties));
		}
		return definition;
	}

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