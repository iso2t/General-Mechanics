package general.api.transfer.item;

import general.api.transfer.ResourceSlotDefinition;
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

	public static ResourceSlotDefinition<ItemResource> slot (String name, int capacity) {
		return ResourceSlotDefinition.named(name, capacity);
	}

	public static ResourceSlotDefinition<ItemResource> slot (String name, int capacity, Predicate<? super ItemResource> validator) {
		return ResourceSlotDefinition.named(name, capacity, validator);
	}

	/**
	 * Naming alias only; insertion permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> input (String name) {
		return slot(name);
	}

	/**
	 * Naming alias only; insertion permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> input (String name, int capacity) {
		return slot(name, capacity);
	}

	/**
	 * Naming alias only; extraction permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> output (String name) {
		return slot(name);
	}

	/**
	 * Naming alias only; extraction permission is configured separately.
	 */
	public static ResourceSlotDefinition<ItemResource> output (String name, int capacity) {
		return slot(name, capacity);
	}
}
