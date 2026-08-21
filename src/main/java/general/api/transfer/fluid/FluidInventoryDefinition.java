package general.api.transfer.fluid;

import general.api.transfer.ResourceAccess;
import general.api.transfer.ResourceChangeListener;
import general.api.transfer.ResourceInventoryDefinition;
import general.api.transfer.ResourceSlotKey;
import general.api.transfer.ResourceSlotDefinition;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;
import java.util.function.Predicate;

/**
 * Fluid convenience layer over the generic physical inventory definition.
 */
public final class FluidInventoryDefinition {

	private final ResourceInventoryDefinition<FluidResource> definition;

	private FluidInventoryDefinition (ResourceInventoryDefinition<FluidResource> definition) {
		this.definition = definition;
	}

	public static Builder builder () {
		return new Builder();
	}

	public ResourceInventoryDefinition<FluidResource> genericDefinition () {
		return definition;
	}

	public List<ResourceSlotDefinition<FluidResource>> slots () {
		return definition.slots();
	}

	public int size () {
		return definition.size();
	}

	public int index (String name) {
		return definition.index(name);
	}

	public int index (ResourceSlotKey slot) {
		return definition.index(slot);
	}

	public ResourceSlotDefinition<FluidResource> get (int index) {
		return definition.get(index);
	}

	public ResourceAccess.Builder<FluidResource> access () {
		return ResourceAccess.builder(definition);
	}

	public FluidResourceHandler createHandler (Runnable changeCallback) {
		return new FluidResourceHandler(this, changeCallback);
	}

	public FluidResourceHandler createHandler (ResourceChangeListener<FluidResource> changeListener) {
		return new FluidResourceHandler(this, changeListener);
	}

	public static final class Builder {

		private final ResourceInventoryDefinition.Builder<FluidResource> delegate = ResourceInventoryDefinition.builder();

		public Builder tank (ResourceSlotDefinition<FluidResource> tank) {
			delegate.slot(tank);
			return this;
		}

		public Builder tank (String name, int capacity) {
			delegate.slot(name, capacity);
			return this;
		}

		public Builder tank (ResourceSlotKey slot, int capacity) {
			delegate.slot(slot, capacity);
			return this;
		}

		public Builder tank (String name, int capacity, Predicate<? super FluidResource> validator) {
			delegate.slot(name, capacity, validator);
			return this;
		}

		public Builder tank (ResourceSlotKey slot, int capacity, Predicate<? super FluidResource> validator) {
			delegate.slot(slot, capacity, validator);
			return this;
		}

		public FluidInventoryDefinition build () {
			return new FluidInventoryDefinition(delegate.build());
		}
	}
}
