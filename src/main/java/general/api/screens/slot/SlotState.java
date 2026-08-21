package general.api.screens.slot;

public enum SlotState {

	LOCKED,
	UNLOCKED;

	public SlotState toggle () {
		return this == LOCKED ? UNLOCKED : LOCKED;
	}

}
