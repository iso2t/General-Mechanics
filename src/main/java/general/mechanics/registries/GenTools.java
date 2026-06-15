package general.mechanics.registries;

import general.api.definitions.ItemDefinition;
import general.api.mod.GenAPI;
import general.api.registry.RegistryString;
import general.api.registry.item.ItemRegistry;
import general.api.resources.Resource;
import general.mechanics.item.tools.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GenTools extends ItemRegistry {

	public static final  ItemRegistry            INSTANCE = new GenTools();
	public static final  DeferredRegister.Items  REGISTRY = DeferredRegister.createItems(GenAPI.getModId());
	private static final List<ItemDefinition<?>> ITEMS    = new ArrayList<>();

	// Tools
	public static final ItemDefinition<WrenchItem>              WRENCH               = registerItem("Wrench", WrenchItem::new);
	public static final ItemDefinition<FlatheadScrewdriverItem> FLATHEAD_SCREWDRIVER = registerItem("Flathead Screwdriver", FlatheadScrewdriverItem::new);
	public static final ItemDefinition<PhillipsScrewdriverItem> PHILLIPS_SCREWDRIVER = registerItem("Phillips Screwdriver", PhillipsScrewdriverItem::new);
	public static final ItemDefinition<HammerItem>              HAMMER               = registerItem("Hammer", HammerItem::new);
	public static final ItemDefinition<SocketDriverItem>        SOCKET_DRIVER        = registerItem("Socket Driver", SocketDriverItem::new);
	public static final ItemDefinition<WireCuttersItem>         WIRE_CUTTERS         = registerItem("Wire Cutters", WireCuttersItem::new);
	public static final ItemDefinition<SawItem>                 SAW                  = registerItem("Saw", SawItem::new);
	public static final ItemDefinition<FileItem>                FILE                 = registerItem("File", FileItem::new);

	public static <T extends Item> ItemDefinition<T> registerItem (final String localizedName, Function<Item.Properties, T> factory) {
		return ItemRegistry.registerItem(INSTANCE, localizedName, Resource.get(new RegistryString(localizedName).getRegistryName()), factory);
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
