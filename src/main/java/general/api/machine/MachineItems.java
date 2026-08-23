package general.api.machine;

import general.api.transfer.SidedResourceHandlers;
import general.api.transfer.item.ItemInventoryDefinition;
import general.api.transfer.item.ItemResourceHandler;
import general.api.transfer.item.SidedItemResourceProvider;
import net.neoforged.neoforge.transfer.item.ItemResource;

/**
 * Opt-in item-storage feature for a machine block entity.
 */
public interface MachineItems extends MachineHost, SidedItemResourceProvider {

	@Override
	default ItemResourceHandler getItemHandler () {
		return machine().requireItemHandler();
	}

	@Override
	default ItemInventoryDefinition getItemDefinition () {
		return machine().requireItemDefinition();
	}

	@Override
	default SidedResourceHandlers<ItemResource> getSidedItemHandlers () {
		return machine().requireSidedItemHandlers();
	}
}
