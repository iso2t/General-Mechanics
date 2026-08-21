package general.api.multiblock;

import java.util.Objects;

/**
 * Stable route name within one multiblock definition.
 */
public record HatchKey(String name) {

	public HatchKey {
		Objects.requireNonNull(name, "name");
		if (name.isBlank()) throw new IllegalArgumentException("Hatch key cannot be blank");
	}

	public static HatchKey of (String name) {
		return new HatchKey(name);
	}

	@Override
	public String toString () {
		return name;
	}
}
