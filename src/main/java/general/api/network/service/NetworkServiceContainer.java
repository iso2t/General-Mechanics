package general.api.network.service;

import java.util.*;

public final class NetworkServiceContainer {

	private final Map<NetworkServiceType<?>, NetworkService> services = new HashMap<>();

	public <T extends NetworkService> void register (NetworkServiceType<T> type, T service) {
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(service, "service");
		if (!type.equals(service.getType())) {
			throw new IllegalArgumentException("Cannot register service " + service.getClass().getName() + " as " + type + "; it declares type " + service.getType());
		}
		services.put(type, service);
	}

	public void unregister (NetworkServiceType<?> type) {
		services.remove(type);
	}

	public boolean has (NetworkServiceType<?> type) {
		return services.containsKey(type);
	}

	public <T extends NetworkService> T get (NetworkServiceType<T> type) {
		NetworkService service = services.get(type);

		if (service == null) {
			return null;
		}

		return type.cast(service);
	}

	public Collection<NetworkService> getAll () {
		return Collections.unmodifiableCollection(services.values());
	}

	public void clear () {
		services.clear();
	}

}
