package general.api.machine;

/**
 * Owner of one reusable machine runtime.
 */
public interface MachineHost {

	MachineRuntime machine ();
}
