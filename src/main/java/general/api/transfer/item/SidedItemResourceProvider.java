package general.api.transfer.item;

import general.api.transfer.SidedResourceHandlers;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

/**
 * Item capability provider backed by prebuilt, cached side views.
 */
public interface SidedItemResourceProvider extends ItemResourceProvider {

	SidedResourceHandlers<ItemResource> getSidedItemHandlers ();

	default ResourceHandler<ItemResource> getItemHandler (@Nullable Direction side) {
		return getSidedItemHandlers().forSide(side);
	}
}
