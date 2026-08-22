package general.api.screens.slot;

import net.minecraft.world.item.ItemStack;

public interface ILockableSlot {

	SlotState getSlotState ();

	ItemStack getGhostStack ();

	default boolean isLocked () {
		return getSlotState() == SlotState.LOCKED;
	}

}
