package general.api.network;

import java.util.UUID;

public interface INetworkInterface {

	NetworkNode getNetworkNode();

	default boolean isNetworkEnabled() {
		return true;
	}

}
