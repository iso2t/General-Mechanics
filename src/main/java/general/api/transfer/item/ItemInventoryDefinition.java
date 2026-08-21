package general.api.transfer.item;

import general.api.transfer.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Item convenience layer over the generic physical inventory definition.
 */
public final class ItemInventoryDefinition {

	private final ResourceInventoryDefinition<ItemResource> definition;

	private ItemInventoryDefinition (ResourceInventoryDefinition<ItemResource> definition) {
		this.definition = definition;
	}

	public static Builder builder () {
		return new Builder();
	}

	public ResourceInventoryDefinition<ItemResource> genericDefinition () {
		return definition;
	}

	public List<ResourceSlotDefinition<ItemResource>> slots () {
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

	public ResourceSlotDefinition<ItemResource> get (int index) {
		return definition.get(index);
	}

	public ResourceAccess.Builder<ItemResource> access () {
		return ResourceAccess.builder(definition);
	}

	public ItemResourceHandler createHandler (Runnable changeCallback) {
		return new ItemResourceHandler(this, changeCallback);
	}

	public ItemResourceHandler createHandler (ResourceChangeListener<ItemResource> changeListener) {
		return new ItemResourceHandler(this, changeListener);
	}

	/**
	 * Creates storage whose selected input slots can be locked to their current item
	 * identities. Lock state remains machine-wide while each listed slot retains its
	 * own item filter.
	 */
	public LockableItemResourceHandler createLockableHandler (Runnable changeCallback, int... lockableSlots) {
		return new LockableItemResourceHandler(this, changeCallback, changeCallback, lockableSlots);
	}

	public LockableItemResourceHandler createLockableHandler (Runnable changeCallback, ResourceSlotKey... lockableSlots) {
		Objects.requireNonNull(lockableSlots, "lockableSlots");
		int[] indices = new int[lockableSlots.length];
		for (int index = 0; index < lockableSlots.length; index++) {
			indices[index] = this.index(Objects.requireNonNull(lockableSlots[index], "lockable slot at index " + index));
		}
		return createLockableHandler(changeCallback, indices);
	}

	public static final class Builder {

		private final ResourceInventoryDefinition.Builder<ItemResource> delegate = ResourceInventoryDefinition.builder();

		public Builder slot (ResourceSlotDefinition<ItemResource> slot) {
			delegate.slot(slot);
			return this;
		}

		public Builder slot (String name) {
			return slot(name, Item.ABSOLUTE_MAX_STACK_SIZE);
		}

		public Builder slot (ResourceSlotKey slot) {
			return slot(slot, Item.ABSOLUTE_MAX_STACK_SIZE);
		}

		public Builder slot (String name, int capacity) {
			delegate.slot(name, capacity);
			return this;
		}

		public Builder slot (ResourceSlotKey slot, int capacity) {
			delegate.slot(slot, capacity);
			return this;
		}

		public Builder slot (String name, int capacity, Predicate<? super ItemResource> validator) {
			delegate.slot(name, capacity, validator);
			return this;
		}

		public Builder slot (ResourceSlotKey slot, int capacity, Predicate<? super ItemResource> validator) {
			delegate.slot(slot, capacity, validator);
			return this;
		}

		/**
		 * Physical slot alias; this does not grant external insertion access.
		 */
		public Builder input (String name) {
			return slot(name);
		}

		public Builder input (ResourceSlotKey slot) {
			return slot(slot);
		}

		public Builder input (String name, int capacity) {
			return slot(name, capacity);
		}

		public Builder input (ResourceSlotKey slot, int capacity) {
			return slot(slot, capacity);
		}

		public Builder input (String name, int capacity, Predicate<? super ItemResource> validator) {
			return slot(name, capacity, validator);
		}

		public Builder input (ResourceSlotKey slot, int capacity, Predicate<? super ItemResource> validator) {
			return slot(slot, capacity, validator);
		}

		/**
		 * Physical slot alias; this does not deny internal or external insertion.
		 */
		public Builder output (String name) {
			return slot(name);
		}

		public Builder output (ResourceSlotKey slot) {
			return slot(slot);
		}

		public Builder output (String name, int capacity) {
			return slot(name, capacity);
		}

		public Builder output (ResourceSlotKey slot, int capacity) {
			return slot(slot, capacity);
		}

		public Builder output (String name, int capacity, Predicate<? super ItemResource> validator) {
			return slot(name, capacity, validator);
		}

		public Builder output (ResourceSlotKey slot, int capacity, Predicate<? super ItemResource> validator) {
			return slot(slot, capacity, validator);
		}

		public ItemInventoryDefinition build () {
			return new ItemInventoryDefinition(delegate.build());
		}
	}
}
