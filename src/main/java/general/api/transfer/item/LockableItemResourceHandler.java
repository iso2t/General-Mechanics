package general.api.transfer.item;

import general.api.transfer.ResourceChangeListener;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;

import java.util.BitSet;
import java.util.Objects;

/**
 * Item storage with a machine-wide lock and a persistent resource identity for
 * each configured input slot.
 *
 * <p>Locking snapshots the current identity in every configured slot. A slot
 * that was empty at that moment rejects every insertion until the machine is
 * unlocked. Extraction remains unrestricted, allowing recipes to consume a
 * stack while its remembered identity remains available to menus as a ghost
 * item.</p>
 */
public final class LockableItemResourceHandler extends ItemResourceHandler {

	private static final String LOCKED_TAG    = "locked";
	private static final String RESOURCES_TAG = "resources";

	private final BitSet                    lockableSlots;
	private final NonNullList<ItemResource> lockedResources;
	private final Runnable                  lockChangeCallback;

	private boolean locked;

	public LockableItemResourceHandler (ItemInventoryDefinition definition, Runnable contentChangeCallback, Runnable lockChangeCallback, int... lockableSlots) {
		this(definition, ResourceChangeListener.from(contentChangeCallback), lockChangeCallback, lockableSlots);
	}

	public LockableItemResourceHandler (ItemInventoryDefinition definition, ResourceChangeListener<ItemResource> contentChangeListener, Runnable lockChangeCallback, int... lockableSlots) {
		super(definition, contentChangeListener);
		this.lockChangeCallback = Objects.requireNonNull(lockChangeCallback, "lockChangeCallback");
		Objects.requireNonNull(lockableSlots, "lockableSlots");
		if (lockableSlots.length == 0) throw new IllegalArgumentException("At least one lockable item slot is required");

		this.lockableSlots = new BitSet(definition.size());
		for (int index : lockableSlots) {
			Objects.checkIndex(index, definition.size());
			if (this.lockableSlots.get(index)) throw new IllegalArgumentException("Duplicate lockable item slot index " + index);
			this.lockableSlots.set(index);
		}
		this.lockedResources = NonNullList.withSize(definition.size(), ItemResource.EMPTY);
	}

	public boolean isLocked () {
		return locked;
	}

	public boolean isLockableSlot (int index) {
		Objects.checkIndex(index, size());
		return lockableSlots.get(index);
	}

	public int getLockableSlotCount () {
		return lockableSlots.cardinality();
	}

	public ItemResource getLockedResource (int index) {
		Objects.checkIndex(index, size());
		return lockedResources.get(index);
	}

	public ItemStack getGhostStack (int index) {
		ItemResource resource = getLockedResource(index);
		return resource.isEmpty() ? ItemStack.EMPTY : resource.toStack();
	}

	public boolean toggleLocked () {
		return setLocked(!locked);
	}

	public boolean setLocked (boolean locked) {
		if (this.locked == locked) return false;
		this.locked = locked;
		if (locked) captureCurrentResources();
		else clearLockedResources();
		lockChangeCallback.run();
		return true;
	}

	public void serializeLockState (@NonNull ValueOutput output) {
		Objects.requireNonNull(output, "output");
		output.putBoolean(LOCKED_TAG, locked);
		if (!locked) return;

		var resources = output.list(RESOURCES_TAG, ItemResource.OPTIONAL_CODEC);
		for (ItemResource resource : lockedResources) resources.add(resource);
	}

	public void deserializeLockState (@NonNull ValueInput input) {
		Objects.requireNonNull(input, "input");
		clearLockedResources();
		locked = input.getBooleanOr(LOCKED_TAG, false);
		if (!locked) return;

		var serialized = input.list(RESOURCES_TAG, ItemResource.OPTIONAL_CODEC);
		if (serialized.isEmpty()) {
			// Worlds created before ghost identities were persisted can safely derive
			// their initial filters from the loaded inventory contents.
			captureCurrentResources();
			return;
		}

		int index = 0;
		for (ItemResource resource : serialized.orElseThrow()) {
			if (index >= lockedResources.size()) throw new IllegalArgumentException("Serialized item lock state contains too many slots");
			lockedResources.set(index++, Objects.requireNonNull(resource, "Serialized locked item resource"));
		}
		if (index != lockedResources.size()) {
			throw new IllegalArgumentException("Serialized item lock state contains " + index + " slots, expected " + lockedResources.size());
		}
		for (int slot = 0; slot < lockedResources.size(); slot++) {
			if (!lockableSlots.get(slot) && !lockedResources.get(slot).isEmpty()) {
				throw new IllegalArgumentException("Serialized item lock state contains a resource for non-lockable slot " + slot);
			}
		}
	}

	@Override
	protected boolean acceptsRuntimeInsertion (int index, ItemResource resource) {
		if (!locked || !lockableSlots.get(index)) return true;
		ItemResource expected = lockedResources.get(index);
		return !expected.isEmpty() && expected.equals(resource);
	}

	private void captureCurrentResources () {
		clearLockedResources();
		for (int index = lockableSlots.nextSetBit(0); index >= 0; index = lockableSlots.nextSetBit(index + 1)) {
			lockedResources.set(index, getResource(index));
		}
	}

	private void clearLockedResources () {
		for (int index = 0; index < lockedResources.size(); index++) lockedResources.set(index, ItemResource.EMPTY);
	}
}
