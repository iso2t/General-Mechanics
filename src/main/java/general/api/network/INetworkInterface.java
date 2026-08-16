package general.api.network;

import java.util.List;

public interface INetworkInterface {

	NetworkNode getNetworkNode();

	default boolean isNetworkEnabled() {
		return true;
	}

	/** External blocks this interface currently uses, for diagnostics and tooling. */
	default List<NetworkEndpoint> getNetworkEndpoints () {
		return List.of();
	}

}
