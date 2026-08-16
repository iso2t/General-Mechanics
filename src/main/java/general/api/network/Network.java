package general.api.network;

import general.api.network.service.NetworkService;
import general.api.network.service.NetworkServiceType;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public final class Network {

	@Getter
	private final UUID                   id;
	private final Map<UUID, NetworkNode> nodes       = new HashMap<>();
	private final Set<NetworkConnection> connections = new HashSet<>();

	public Network (UUID id) {
		this.id = Objects.requireNonNull(id);
	}

	public Network () {
		this(UUID.randomUUID());
	}

	public Collection<NetworkNode> getNodes () {
		return Collections.unmodifiableCollection(nodes.values());
	}

	public boolean addNode (NetworkNode node) {
		if (nodes.containsKey(node.getId())) {
			return false;
		}

		nodes.put(node.getId(), node);
		return true;
	}

	public boolean removeNode (UUID nodeId) {
		return nodes.remove(nodeId) != null;
	}

	public boolean containsNode (UUID nodeId) {
		return nodes.containsKey(nodeId);
	}

	public NetworkNode getNode (UUID nodeId) {
		return nodes.get(nodeId);
	}

	public int size () {
		return nodes.size();
	}

	public <T extends NetworkService> List<T> getServices (NetworkServiceType<T> type) {
		return nodes.values().stream().map(node -> node.getService(type)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public <T extends NetworkService> Map<NetworkNode, T> getNodesWithService (NetworkServiceType<T> type) {
		Map<NetworkNode, T> result = new HashMap<>();

		for (NetworkNode node : nodes.values()) {
			T service = node.getService(type);

			if (service != null) {
				result.put(node, service);
			}
		}

		return Collections.unmodifiableMap(result);
	}

	public boolean connect (UUID first, UUID second) {
		if (!nodes.containsKey(first) || !nodes.containsKey(second)) {
			return false;
		}

		return connections.add(normalizeConnection(first, second));
	}

	public boolean disconnect (UUID first, UUID second) {
		return connections.remove(normalizeConnection(first, second));
	}

	public boolean areConnected (UUID first, UUID second) {
		return connections.contains(normalizeConnection(first, second));
	}

	public Set<NetworkConnection> getConnections () {
		return Collections.unmodifiableSet(connections);
	}

	public Set<NetworkNode> getNeighbors (UUID nodeId) {
		Set<NetworkNode> result = new HashSet<>();

		for (NetworkConnection connection : connections) {
			if (!connection.contains(nodeId)) {
				continue;
			}

			NetworkNode node = nodes.get(connection.other(nodeId));

			if (node != null) {
				result.add(node);
			}
		}

		return Collections.unmodifiableSet(result);
	}

	private NetworkConnection normalizeConnection (UUID first, UUID second) {
		return first.compareTo(second) < 0 ? new NetworkConnection(first, second) : new NetworkConnection(second, first);
	}

	public boolean hasPath (UUID source, UUID destination) {

		if (source.equals(destination)) {
			return nodes.containsKey(source);
		}

		if (!nodes.containsKey(source) || !nodes.containsKey(destination)) {
			return false;
		}

		Set<UUID> visited = new HashSet<>();
		ArrayDeque<UUID> queue = new ArrayDeque<>();

		queue.add(source);
		visited.add(source);

		while (!queue.isEmpty()) {

			UUID current = queue.removeFirst();

			for (NetworkNode neighbor : getNeighbors(current)) {

				UUID id = neighbor.getId();

				if (id.equals(destination)) {
					return true;
				}

				if (visited.add(id)) {
					queue.addLast(id);
				}
			}
		}

		return false;
	}

}
