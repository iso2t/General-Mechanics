package general.api.transfer.fluid;

import general.api.machine.upgrade.MachineUpgradeProfile;
import general.api.transfer.*;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

	public ProfiledFluidResourceHandler createProfiledHandler (Supplier<MachineUpgradeProfile> profileSupplier, Runnable changeCallback) {
		return new ProfiledFluidResourceHandler(this, profileSupplier, changeCallback);
	}

	public ProfiledFluidResourceHandler createProfiledHandler (Supplier<MachineUpgradeProfile> profileSupplier, ResourceChangeListener<FluidResource> changeListener) {
		return new ProfiledFluidResourceHandler(this, profileSupplier, changeListener);
	}

	public ProfiledFluidResourceHandler createProfiledHandler (MachineUpgradeProfile profile, Runnable changeCallback) {
		return new ProfiledFluidResourceHandler(this, profile, changeCallback);
	}

	public ProfiledFluidResourceHandler createProfiledHandler (MachineUpgradeProfile profile, ResourceChangeListener<FluidResource> changeListener) {
		return new ProfiledFluidResourceHandler(this, profile, changeListener);
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
