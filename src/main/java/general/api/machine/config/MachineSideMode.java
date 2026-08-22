package general.api.machine.config;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.Optional;

/**
 * The single external service exposed by a configurable machine side.
 *
 * <p>Machines opt into the subset of modes they support through
 * {@link MachineSideConfiguration.Builder}. A mode being defined here does not
 * automatically make it valid for every machine.</p>
 */
public enum MachineSideMode implements StringRepresentable {

	NONE(0),
	ITEM_INPUT(1),
	ITEM_OUTPUT(2),
	FLUID_INPUT(3),
	FLUID_OUTPUT(4),
	ENERGY_INPUT(5),
	NETWORK(6);

	private static final MachineSideMode[] VALUES = values();

	private final int id;

	MachineSideMode (int id) {
		this.id = id;
	}

	/**
	 * Stable identifier for menu synchronization and configuration actions.
	 */
	public int id () {
		return id;
	}

	public boolean isItem () {
		return this == ITEM_INPUT || this == ITEM_OUTPUT;
	}

	public boolean isFluid () {
		return this == FLUID_INPUT || this == FLUID_OUTPUT;
	}

	public boolean isInput () {
		return this == ITEM_INPUT || this == FLUID_INPUT || this == ENERGY_INPUT;
	}

	public boolean isOutput () {
		return this == ITEM_OUTPUT || this == FLUID_OUTPUT;
	}

	public static Optional<MachineSideMode> byId (int id) {
		for (MachineSideMode mode : VALUES) {
			if (mode.id == id) return Optional.of(mode);
		}
		return Optional.empty();
	}

	public static Optional<MachineSideMode> byName (String name) {
		if (name == null) return Optional.empty();
		String normalized = name.toLowerCase(Locale.ROOT);
		for (MachineSideMode mode : VALUES) {
			if (mode.getSerializedName().equals(normalized)) return Optional.of(mode);
		}
		return Optional.empty();
	}

	@Override
	public @NonNull String getSerializedName () {
		return name().toLowerCase(Locale.ROOT);
	}
}
