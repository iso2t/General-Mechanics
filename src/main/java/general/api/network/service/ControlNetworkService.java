package general.api.network.service;

import general.api.network.NetworkServices;

import java.util.Set;

public interface ControlNetworkService extends NetworkService {

	MachineState getState ();

	boolean isEnabled ();

	void setEnabled (boolean enabled);

	Set<NetworkAction> getSupportedActions ();

	NetworkActionResult execute (NetworkAction action);

	@Override
	default NetworkServiceType<ControlNetworkService> getType () {
		return NetworkServices.CONTROL;
	}
}
