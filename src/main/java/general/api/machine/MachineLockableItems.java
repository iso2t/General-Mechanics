package general.api.machine;

import general.api.transfer.item.LockableItemResourceHandler;

/**
 * Item feature whose declared input set supports machine-wide locking.
 */
public interface MachineLockableItems extends MachineItems {

	@Override
	default LockableItemResourceHandler getItemHandler () {
		return machine().requireLockableItemHandler();
	}
}
