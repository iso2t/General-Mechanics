package general.api.transfer.item;

import general.api.transfer.ResourceSlotDefinition;
import general.api.transfer.ResourceSlotKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.Predicate;

/**
 * Readable factories for physical item slot definitions.
 */
public final class ItemSlots {

	private ItemSlots () {
	}

	/**
	 * Slot whose effective capacity is also limited by each item's maximum stack size.
	 */
	public static ResourceSlotDefinition<ItemResource> slot (String name) {
		return slot(name, Item.ABSOLUTE_MAX_STACK_SIZE);
	}

	public static ResourceSlotDefinition<ItemResource> slot (ResourceSlotKey slot) {
		return slot(slot, Item.ABSOLUTE_MAX_STACK_SIZE);
	}

	public static ResourceSlotDefinition<ItemResource> slot (String name, int capacity) {
		return ResourceSlotDefinition.named(name, capacity);
	}

	public static ResourceSlotDefinition<ItemResource> slot (ResourceSlotKey slot, int capacity) {
		return ResourceSlotDefinition.named(slot, capacity);
	}

	public static ResourceSlotDefinition<ItemResource> slot (String name, int capacity, Predicate<? super ItemResource> validator) {
		return ResourceSlotDefinition.named(name, capacity, validator);
	}

	public static ResourceSlotDefinition<ItemResource> slot (ResourceSlotKey slot, int capacity, Predicate<? super ItemResource> validator) {
		return ResourceSlotDefinition.named(slot, capacity, validator);
	}

	/**
	 * Naming alias only; insertion permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> input (String name) {
		return slot(name);
	}

	public static ResourceSlotDefinition<ItemResource> input (ResourceSlotKey slot) {
		return slot(slot);
	}

	/**
	 * Naming alias only; insertion permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> input (String name, int capacity) {
		return slot(name, capacity);
	}

	public static ResourceSlotDefinition<ItemResource> input (ResourceSlotKey slot, int capacity) {
		return slot(slot, capacity);
	}

	/**
	 * Naming alias only; extraction permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> output (String name) {
		return slot(name);
	}

	public static ResourceSlotDefinition<ItemResource> output (ResourceSlotKey slot) {
		return slot(slot);
	}

	/**
	 * Naming alias only; extraction permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> output (String name, int capacity) {
		return slot(name, capacity);
	}

	public static ResourceSlotDefinition<ItemResource> output (ResourceSlotKey slot, int capacity) {
		return slot(slot, capacity);
	}
}
