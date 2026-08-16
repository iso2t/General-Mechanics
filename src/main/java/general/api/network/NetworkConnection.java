package general.api.network;

import java.util.UUID;

public record NetworkConnection(UUID first, UUID second) {

	public NetworkConnection {
		if (first.equals(second)) {
			throw new IllegalArgumentException("A node cannot connect to itself");
		}
	}

	public boolean contains (UUID node) {
		return first.equals(node) || second.equals(node);
	}

	public UUID other (UUID node) {
		if (first.equals(node)) {
			return second;
		}

		if (second.equals(node)) {
			return first;
		}

		throw new IllegalArgumentException("Node is not part of this connection");
	}
}