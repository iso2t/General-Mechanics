package general.api.screens.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Resource-handler-backed machine input slot whose machine-wide lock state and
 * synchronized ghost item are supplied by its owning menu.
 */
public class MachineItemSlot extends ResourceHandlerSlot implements ILockableSlot {

	private BooleanSupplier     lockedSupplier = () -> false;
	private Supplier<ItemStack> ghostSupplier  = () -> ItemStack.EMPTY;
	private boolean             lockStateBound;

	public MachineItemSlot (ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int handlerSlot, int x, int y) {
		super(handler, slotModifier, handlerSlot, x, y);
	}

	/**
	 * Binds this slot to the shared menu state. A slot may only be bound once.
	 */
	public final void bindLockState (BooleanSupplier lockedSupplier, Supplier<ItemStack> ghostSupplier) {
		if (lockStateBound) throw new IllegalStateException("Machine item slot is already bound to a lock state");
		this.lockedSupplier = Objects.requireNonNull(lockedSupplier, "lockedSupplier");
		this.ghostSupplier = Objects.requireNonNull(ghostSupplier, "ghostSupplier");
		this.lockStateBound = true;
	}

	@Override
	public SlotState getSlotState () {
		return lockedSupplier.getAsBoolean() ? SlotState.LOCKED : SlotState.UNLOCKED;
	}

	@Override
	public ItemStack getGhostStack () {
		ItemStack ghost = ghostSupplier.get();
		return ghost == null ? ItemStack.EMPTY : ghost.copy();
	}

	@Override
	public boolean mayPlace (@NonNull ItemStack stack) {
		if (!super.mayPlace(stack)) return false;
		if (!isLocked()) return true;
		ItemStack ghost = getGhostStack();
		return !ghost.isEmpty() && ItemStack.isSameItemSameComponents(ghost, stack);
	}
}
