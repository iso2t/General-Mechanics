package general.api.multiblock;

import general.api.network.service.NetworkServiceType;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.ResourceSlotKey;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable controller resources and network services exposed through a hatch
 * route. Resource locations are stored by stable slot name and resolved against
 * the bound controller's inventory definitions.
 */
public final class HatchAccess {

	private final Set<String>                itemInsertion;
	private final Set<String>                itemExtraction;
	private final Set<String>                fluidInsertion;
	private final Set<String>                fluidExtraction;
	private final ResourceIoMode             energyMode;
	private final Set<NetworkServiceType<?>> networkServices;

	private HatchAccess (Builder builder) {
		this.itemInsertion = Set.copyOf(builder.itemInsertion);
		this.itemExtraction = Set.copyOf(builder.itemExtraction);
		this.fluidInsertion = Set.copyOf(builder.fluidInsertion);
		this.fluidExtraction = Set.copyOf(builder.fluidExtraction);
		this.energyMode = builder.energyMode;
		this.networkServices = Set.copyOf(builder.networkServices);
	}

	public static Builder builder () {
		return new Builder();
	}

	public Set<String> itemInsertion () {
		return itemInsertion;
	}

	public Set<String> itemExtraction () {
		return itemExtraction;
	}

	public Set<String> fluidInsertion () {
		return fluidInsertion;
	}

	public Set<String> fluidExtraction () {
		return fluidExtraction;
	}

	public ResourceIoMode energyMode () {
		return energyMode;
	}

	public Set<NetworkServiceType<?>> networkServices () {
		return networkServices;
	}

	public boolean hasItemAccess () {
		return !itemInsertion.isEmpty() || !itemExtraction.isEmpty();
	}

	public boolean hasFluidAccess () {
		return !fluidInsertion.isEmpty() || !fluidExtraction.isEmpty();
	}

	public static final class Builder {

		private final Set<String>                itemInsertion   = new LinkedHashSet<>();
		private final Set<String>                itemExtraction  = new LinkedHashSet<>();
		private final Set<String>                fluidInsertion  = new LinkedHashSet<>();
		private final Set<String>                fluidExtraction = new LinkedHashSet<>();
		private final Set<NetworkServiceType<?>> networkServices = new LinkedHashSet<>();
		private       ResourceIoMode             energyMode      = ResourceIoMode.NONE;

		public Builder itemInsert (ResourceSlotKey... slots) {
			addSlots(itemInsertion, slots);
			return this;
		}

		public Builder itemExtract (ResourceSlotKey... slots) {
			addSlots(itemExtraction, slots);
			return this;
		}

		public Builder fluidInsert (ResourceSlotKey... slots) {
			addSlots(fluidInsertion, slots);
			return this;
		}

		public Builder fluidExtract (ResourceSlotKey... slots) {
			addSlots(fluidExtraction, slots);
			return this;
		}

		public Builder energy (ResourceIoMode mode) {
			this.energyMode = Objects.requireNonNull(mode, "mode");
			return this;
		}

		public Builder network (NetworkServiceType<?>... services) {
			Objects.requireNonNull(services, "services");
			for (int index = 0; index < services.length; index++) {
				networkServices.add(Objects.requireNonNull(services[index], "service at index " + index));
			}
			return this;
		}

		public HatchAccess build () {
			return new HatchAccess(this);
		}

		private static void addSlots (Set<String> target, ResourceSlotKey[] slots) {
			Objects.requireNonNull(slots, "slots");
			for (int index = 0; index < slots.length; index++) {
				ResourceSlotKey slot = Objects.requireNonNull(slots[index], "slot at index " + index);
				target.add(Objects.requireNonNull(slot.name(), "slot name"));
			}
		}
	}
}
