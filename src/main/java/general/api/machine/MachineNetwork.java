package general.api.machine;

import general.api.network.INetworkInterface;
import general.api.network.NetworkNode;

/**
 * Opt-in General Network feature for a machine block entity.
 */
public interface MachineNetwork extends MachineHost, INetworkInterface {

	@Override
	default NetworkNode getNetworkNode () {
		return machine().getNetworkNode();
	}

	@Override
	default boolean isNetworkEnabled () {
		return machine().isNetworkEnabled();
	}
}
