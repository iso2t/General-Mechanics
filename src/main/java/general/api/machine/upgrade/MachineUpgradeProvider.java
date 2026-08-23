package general.api.machine.upgrade;

/**
 * Supplies the upgrade profile represented by a block or another machine
 * component.
 *
 * <p>Multiblocks should resolve a uniform component tier once and apply the
 * returned profile once, regardless of how many blocks make up that component.</p>
 */
public interface MachineUpgradeProvider {

	MachineUpgradeProfile getUpgradeProfile ();
}
