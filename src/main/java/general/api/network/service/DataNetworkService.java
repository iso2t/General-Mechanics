package general.api.network.service;

import general.api.network.NetworkServices;

import java.util.Map;

public interface DataNetworkService extends NetworkService {

	Map<String, NetworkValue> getValues ();

	default NetworkValue getValue (String key) {
		return getValues().get(key);
	}

	@Override
	default NetworkServiceType<DataNetworkService> getType () {
		return NetworkServices.DATA;
	}
}
