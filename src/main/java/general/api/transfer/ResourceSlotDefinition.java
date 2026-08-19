package general.api.transfer;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Immutable physical rules for one resource storage location.
 *
 * <p>A definition controls what may physically be stored and how much. It does
 * not describe automation permissions or block sides; those belong in a
 * {@link ResourceAccessPolicy}. Validators are only invoked for non-empty
 * resources.</p>
 *
 * @param <R> resource type stored by the slot
 */
public final class ResourceSlotDefinition<R extends Resource> {

	private static final Predicate<Resource> ACCEPT_ALL = resource -> true;

	private final String               name;
	private final int                  capacity;
	private final Predicate<? super R> validator;

	private ResourceSlotDefinition (String name, int capacity, Predicate<? super R> validator) {
		if (capacity < 0) {
			throw new IllegalArgumentException("Resource slot capacity must be non-negative: " + capacity);
		}
		if (name != null && name.isBlank()) {
			throw new IllegalArgumentException("Resource slot name must not be blank");
		}
		this.name = name;
		this.capacity = capacity;
		this.validator = Objects.requireNonNull(validator, "validator");
	}

	/**
	 * Creates an unnamed slot that accepts every non-empty resource.
	 */
	public static <R extends Resource> ResourceSlotDefinition<R> of (int capacity) {
		return new ResourceSlotDefinition<>(null, capacity, acceptAll());
	}

	/**
	 * Creates an unnamed slot with a physical resource filter.
	 */
	public static <R extends Resource> ResourceSlotDefinition<R> filtered (int capacity, Predicate<? super R> validator) {
		return new ResourceSlotDefinition<>(null, capacity, validator);
	}

	/**
	 * Creates a named slot that accepts every non-empty resource.
	 */
	public static <R extends Resource> ResourceSlotDefinition<R> named (String name, int capacity) {
		return new ResourceSlotDefinition<>(Objects.requireNonNull(name, "name"), capacity, acceptAll());
	}

	/**
	 * Creates a named slot with a physical resource filter.
	 */
	public static <R extends Resource> ResourceSlotDefinition<R> named (String name, int capacity, Predicate<? super R> validator) {
		return new ResourceSlotDefinition<>(Objects.requireNonNull(name, "name"), capacity, validator);
	}

	/**
	 * Stable name, when the definition was given one.
	 */
	public Optional<String> name () {
		return Optional.ofNullable(name);
	}

	/**
	 * Maximum physical amount before resource-specific limits are applied.
	 */
	public int capacity () {
		return capacity;
	}

	/**
	 * Tests a resource against the physical filter.
	 *
	 * @return {@code false} for an empty resource; otherwise the validator result
	 */
	public boolean accepts (R resource) {
		Objects.requireNonNull(resource, "resource");
		return !resource.isEmpty() && validator.test(resource);
	}

	@SuppressWarnings("unchecked")
	private static <R extends Resource> Predicate<R> acceptAll () {
		return (Predicate<R>) ACCEPT_ALL;
	}
}
