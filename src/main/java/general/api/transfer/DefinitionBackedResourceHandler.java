package general.api.transfer;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Common transactional storage implementation backed by NeoForge's
 * {@link StacksResourceHandler} snapshot machinery.
 *
 * <p>Subclasses only provide conversion and copy operations for their stack
 * representation. Physical validity and capacities come from the immutable
 * definition. Insertions and extractions remain transactional; change listeners
 * are reached from NeoForge's root-commit callback and are therefore not called
 * for simulations, aborted transactions, failed operations, or net-zero changes.</p>
 *
 * <p>Like NeoForge's handlers, instances are intended for server-thread use and
 * are not thread-safe.</p>
 */
public abstract class DefinitionBackedResourceHandler<S, R extends Resource> extends StacksResourceHandler<S, R> implements VersionedResourceHandler<R> {

	@Getter
	private final ResourceInventoryDefinition<R> definition;
	private final ResourceChangeListener<R>      changeListener;
	private       long                           contentRevision;

	protected DefinitionBackedResourceHandler (ResourceInventoryDefinition<R> definition, S emptyStack, Codec<S> stackCodec, ResourceChangeListener<R> changeListener) {
		super(requireDefinition(definition).size(), Objects.requireNonNull(emptyStack, "emptyStack"), Objects.requireNonNull(stackCodec, "stackCodec"));
		this.definition = definition;
		this.changeListener = Objects.requireNonNull(changeListener, "changeListener");
	}

	public final ResourceSlotDefinition<R> slotDefinition (int index) {
		return definition.get(index);
	}

	/**
	 * Monotonic runtime revision incremented after each committed content change.
	 * Consumers can use this to invalidate derived state without rescanning or
	 * simulating an unchanged handler every tick.
	 */
	@Override
	public final long contentRevision () {
		return contentRevision;
	}

	public final int index (String name) {
		return definition.index(name);
	}

	public final int index (ResourceSlotKey slot) {
		return definition.index(slot);
	}

	@Override
	public final boolean isValid (int index, @NonNull R resource) {
		return definition.get(index).accepts(resource) && acceptsRuntimeInsertion(index, resource);
	}

	/**
	 * Additional live insertion rule layered on top of the immutable slot
	 * definition. Specializations may use this for persisted machine modes such as
	 * locking an input slot to one resource identity.
	 */
	protected boolean acceptsRuntimeInsertion (int index, R resource) {
		return true;
	}

	@Override
	protected int getCapacity (int index, @NonNull R resource) {
		return getEffectiveCapacity(definition.get(index), resource);
	}

	/**
	 * Allows a specialization to apply resource-level limits, such as item stack size.
	 */
	protected int getEffectiveCapacity (ResourceSlotDefinition<R> slot, R resource) {
		return slot.capacity();
	}

	/**
	 * Direct replacement with full physical validation.
	 *
	 * <p>This method is immediate, matching NeoForge's base handler. Normal
	 * machine operation should prefer transactional insert/extract calls.</p>
	 */
	@Override
	public final void set (int index, @NonNull R resource, int amount) {
		Objects.requireNonNull(resource, "resource");
		var slot = definition.get(index);
		if (amount < 0) {
			throw new IllegalArgumentException("Resource amount must be non-negative: " + amount);
		}
		if (resource.isEmpty() && amount > 0) {
			throw new IllegalArgumentException("Cannot store a positive amount of an empty resource in slot '" + displayName(index) + "'");
		}
		if (amount > 0 && !isValid(index, resource)) {
			throw new IllegalArgumentException("Resource " + resource + " is not valid for slot '" + displayName(index) + "'");
		}
		int capacity = getEffectiveCapacity(slot, resource);
		if (amount > capacity) {
			throw new IllegalArgumentException("Amount " + amount + " exceeds capacity " + capacity + " for slot '" + displayName(index) + "'");
		}
		super.set(index, resource, amount);
	}

	/**
	 * Loads NeoForge's standard {@code stacks} value while preserving this
	 * definition's fixed shape and invariants. A shorter legacy list is padded
	 * with empty slots, allowing definitions to append storage locations. A
	 * longer list or invalid contents fail contextually rather than losing data.
	 */
	@Override
	public final void deserialize (@NonNull ValueInput input) {
		Objects.requireNonNull(input, "input");
		input.read(VALUE_IO_KEY, codec).ifPresent(loaded -> {
			NonNullList<S> normalized = normalizeLoadedStacks(loaded);
			validateLoadedStacks(normalized);
			setStacks(normalized);
			contentRevision++;
		});
	}

	@Override
	protected final void onContentsChanged (int index, S previousContents) {
		R previousResource = getResourceFrom(previousContents);
		int previousAmount = getAmountFrom(previousContents);
		R currentResource = getResource(index);
		int currentAmount = getAmountAsInt(index);
		if (previousAmount == currentAmount && previousResource.equals(currentResource)) {
			return;
		}
		contentRevision++;
		changeListener.onResourceChanged(index, previousResource, previousAmount, currentResource, currentAmount);
	}

	private void validateLoadedStacks (NonNullList<S> loaded) {
		for (int index = 0; index < loaded.size(); index++) {
			S stack = Objects.requireNonNull(loaded.get(index), "Serialized stack at index " + index);
			R resource = Objects.requireNonNull(getResourceFrom(stack), "Serialized resource at index " + index);
			int amount = getAmountFrom(stack);
			if (amount < 0 || resource.isEmpty() != (amount == 0)) {
				throw new IllegalArgumentException("Malformed serialized contents in slot '" + displayName(index) + "': " + amount + "x " + resource);
			}
			if (amount > 0 && !definition.get(index).accepts(resource)) {
				throw new IllegalArgumentException("Serialized resource " + resource + " is not valid for slot '" + displayName(index) + "'");
			}
			int capacity = getEffectiveCapacity(definition.get(index), resource);
			if (amount > capacity) {
				throw new IllegalArgumentException("Serialized amount " + amount + " exceeds capacity " + capacity + " for slot '" + displayName(index) + "'");
			}
		}
	}

	private NonNullList<S> normalizeLoadedStacks (NonNullList<S> loaded) {
		if (loaded.size() > definition.size()) {
			throw new IllegalArgumentException("Serialized resource handler has " + loaded.size() + " slots, but definition only has " + definition.size() + "; refusing to discard stored resources");
		}
		if (loaded.size() == definition.size()) return loaded;
		NonNullList<S> normalized = NonNullList.withSize(definition.size(), emptyStack);
		for (int index = 0; index < loaded.size(); index++) normalized.set(index, loaded.get(index));
		return normalized;
	}

	private String displayName (int index) {
		return definition.get(index).name().orElse("#" + index);
	}

	private static <R extends Resource> ResourceInventoryDefinition<R> requireDefinition (ResourceInventoryDefinition<R> definition) {
		return Objects.requireNonNull(definition, "definition");
	}
}
