package general.api.network.service;

import general.api.network.NetworkServices;

public interface EnergyNetworkService extends NetworkService {

	long insert (long amount, boolean simulate);

	long extract (long amount, boolean simulate);

	long getStored ();

	long getCapacity ();

	@Override
	default NetworkServiceType<EnergyNetworkService> getType () {
		return NetworkServices.ENERGY;
	}
}