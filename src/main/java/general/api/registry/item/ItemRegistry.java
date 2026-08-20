package general.api.registry.item;

import general.api.definitions.ItemDefinition;
import general.api.tab.ICreativeModeTab;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Function;

public abstract class ItemRegistry implements ICreativeModeTab {

	public abstract DeferredRegister.Items getRegistry ();

	public abstract List<ItemDefinition<?>> getItems ();

	public static <T extends Item> ItemDefinition<T> registerItem (ItemRegistry registry, final String localizedName, Identifier identifier, Function<Item.Properties, T> factory) {
		var definition = new ItemDefinition<>(localizedName, registry.getRegistry().registerItem(identifier.getPath(), factory));
		registry.getItems().add(definition);
		return definition;
	}

}
