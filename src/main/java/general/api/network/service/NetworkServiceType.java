package general.api.network.service;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class NetworkServiceType<T extends NetworkService> {

	@Getter
	private final Identifier id;
	private final Class<T>   type;

	public NetworkServiceType (@NotNull Identifier id, @NotNull Class<T> type) {
		this.id = id;
		this.type = type;
	}

	public Class<T> getServiceClass () {
		return type;
	}

	public T cast (NetworkService service) {
		return type.cast(service);
	}

	@Override
	public boolean equals (Object object) {
		return object instanceof NetworkServiceType<?> other && id.equals(other.id);
	}

	@Override
	public int hashCode () {
		return id.hashCode();
	}

	@Override
	public String toString () {
		return id.toString();
	}

}
