package general.api.transfer;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.List;

/**
 * Generic provider contract for owners of one resource storage system.
 *
 * <p>Owners containing multiple resource types should implement the distinctly
 * named item/fluid provider contracts instead; Java cannot inherit this generic
 * interface twice with different type arguments.</p>
 */
public interface ResourceHandlerProvider<R extends Resource> {

	ResourceHandler<R> getResourceHandler ();

	ResourceInventoryDefinition<R> getResourceDefinition ();

	default List<ResourceSlotDefinition<R>> getResourceSlots () {
		return getResourceDefinition().slots();
	}

	default int getResourceSlotCount () {
		return getResourceDefinition().size();
	}

	default ResourceSlotDefinition<R> getResourceSlotDefinition (int index) {
		return getResourceDefinition().get(index);
	}

	/**
	 * Lifecycle hook suitable for passing to a definition-backed handler.
	 */
	default void onResourceChanged (int index, R previousResource, int previousAmount, R resource, int amount) {
	}
}
