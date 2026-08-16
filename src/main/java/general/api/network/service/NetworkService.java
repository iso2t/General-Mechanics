package general.api.network.service;

public interface NetworkService {

	NetworkServiceType<? extends NetworkService> getType ();

}
