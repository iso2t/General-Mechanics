package general.api.network;

import general.api.network.service.NetworkService;
import general.api.network.service.NetworkServiceContainer;
import general.api.network.service.NetworkServiceType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class NetworkNode {

	@Getter
	private final UUID id;

	@Getter
	@Setter
	private String name;

	@Getter
	private final NetworkServiceContainer services;

	public NetworkNode (@NotNull UUID id, @NotNull String name) {
		this.id = id;
		this.name = name;
		this.services = new NetworkServiceContainer();
	}

	public NetworkNode (@NotNull String name) {
		this(UUID.randomUUID(), name);
	}

	public <T extends NetworkService> boolean hasService (NetworkServiceType<T> type) {
		return services.has(type);
	}

	public <T extends NetworkService> T getService (NetworkServiceType<T> type) {
		return services.get(type);
	}

	@Override
	public boolean equals (Object object) {
		return object instanceof NetworkNode node && id.equals(node.id);
	}

	@Override
	public int hashCode () {
		return id.hashCode();
	}

}
