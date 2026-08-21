package general.api.transfer;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.*;
import java.util.function.Predicate;

/**
 * Immutable ordered layout of physical resource slots.
 *
 * <p>The order is the backing handler's stable index order. Named lookups are
 * resolved once when the definition is built, so machine code can avoid magic
 * numbers without adding lookup work to transfer operations.</p>
 */
public final class ResourceInventoryDefinition<R extends Resource> {

	private final List<ResourceSlotDefinition<R>> slots;
	private final Map<String, Integer>            namedSlots;

	private ResourceInventoryDefinition (List<ResourceSlotDefinition<R>> slots) {
		Objects.requireNonNull(slots, "slots");
		var checkedSlots = new ArrayList<ResourceSlotDefinition<R>>(slots.size());
		for (int index = 0; index < slots.size(); index++) {
			checkedSlots.add(Objects.requireNonNull(slots.get(index), "Resource slot definition at index " + index));
		}
		this.slots = List.copyOf(checkedSlots);
		var names = new LinkedHashMap<String, Integer>();
		for (int index = 0; index < this.slots.size(); index++) {
			var slot = this.slots.get(index);
			if (slot.name().isPresent()) {
				String name = slot.name().orElseThrow();
				Integer previous = names.putIfAbsent(name, index);
				if (previous != null) {
					throw new IllegalArgumentException("Duplicate resource slot name '" + name + "' at indices " + previous + " and " + index);
				}
			}
		}
		this.namedSlots = Map.copyOf(names);
	}

	public static <R extends Resource> Builder<R> builder () {
		return new Builder<>();
	}

	public static <R extends Resource> ResourceInventoryDefinition<R> of (List<ResourceSlotDefinition<R>> slots) {
		return new ResourceInventoryDefinition<>(Objects.requireNonNull(slots, "slots"));
	}

	/**
	 * Immutable definitions in handler index order.
	 */
	public List<ResourceSlotDefinition<R>> slots () {
		return slots;
	}

	public int size () {
		return slots.size();
	}

	public ResourceSlotDefinition<R> get (int index) {
		if (index < 0 || index >= slots.size()) {
			throw new IndexOutOfBoundsException("Resource slot index " + index + " is outside [0, " + slots.size() + ")");
		}
		return slots.get(index);
	}

	/**
	 * Resolves a stable name or throws an error containing that name.
	 */
	public int index (String name) {
		Objects.requireNonNull(name, "name");
		Integer index = namedSlots.get(name);
		if (index == null) {
			throw new IllegalArgumentException("Unknown resource slot '" + name + "'");
		}
		return index;
	}

	public int index (ResourceSlotKey slot) {
		return index(Objects.requireNonNull(slot, "slot").name());
	}

	public boolean has (String name) {
		return namedSlots.containsKey(Objects.requireNonNull(name, "name"));
	}

	public boolean has (ResourceSlotKey slot) {
		return has(Objects.requireNonNull(slot, "slot").name());
	}

	/**
	 * Builder for generic and custom resource layouts.
	 */
	public static final class Builder<R extends Resource> {

		private final List<ResourceSlotDefinition<R>> slots = new ArrayList<>();

		public Builder<R> slot (ResourceSlotDefinition<R> definition) {
			slots.add(Objects.requireNonNull(definition, "definition"));
			return this;
		}

		public Builder<R> slot (int capacity) {
			return slot(ResourceSlotDefinition.of(capacity));
		}

		public Builder<R> slot (int capacity, Predicate<? super R> validator) {
			return slot(ResourceSlotDefinition.filtered(capacity, validator));
		}

		public Builder<R> slot (String name, int capacity) {
			return slot(ResourceSlotDefinition.named(name, capacity));
		}

		public Builder<R> slot (ResourceSlotKey slot, int capacity) {
			return slot(Objects.requireNonNull(slot, "slot").name(), capacity);
		}

		public Builder<R> slot (String name, int capacity, Predicate<? super R> validator) {
			return slot(ResourceSlotDefinition.named(name, capacity, validator));
		}

		public Builder<R> slot (ResourceSlotKey slot, int capacity, Predicate<? super R> validator) {
			return slot(Objects.requireNonNull(slot, "slot").name(), capacity, validator);
		}

		public ResourceInventoryDefinition<R> build () {
			return new ResourceInventoryDefinition<>(slots);
		}
	}
}
