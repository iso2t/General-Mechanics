package general.api.transfer.item;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

/**
 * Distinct item provider contract that can coexist with {@code FluidResourceProvider}.
 */
public interface ItemResourceProvider {

	ResourceHandler<ItemResource> getItemHandler ();

	ItemInventoryDefinition getItemDefinition ();

	default void onItemsChanged (int index, ItemResource previousResource, int previousAmount, ItemResource resource, int amount) {
	}
}
