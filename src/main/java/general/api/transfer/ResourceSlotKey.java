package general.api.transfer;

/**
 * Stable name shared by logical recipe slots and physical resource storage.
 *
 * <p>APIs that only need to resolve a named storage location accept this base
 * type. More specialized systems can layer type-safe roles on top of it.</p>
 */
@FunctionalInterface
public interface ResourceSlotKey {

	String name ();
}
