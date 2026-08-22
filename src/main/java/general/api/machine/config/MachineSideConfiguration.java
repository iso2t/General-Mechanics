package general.api.machine.config;

import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable, persistent per-face state created from a shared
 * {@link MachineSideConfigurationDefinition}.
 *
 * <p>Configuration is stored using {@link MachineFace relative faces}. Runtime
 * consumers may resolve it with a world direction, which keeps configuration
 * attached to the machine when the block is rotated. Runtime changes notify the
 * supplied listener; loading saved state deliberately does not.</p>
 */
public final class MachineSideConfiguration {

	private final MachineSideConfigurationDefinition    definition;
	private final EnumMap<MachineFace, MachineSideMode> modes;
	private final ChangeListener                        changeListener;

	MachineSideConfiguration (MachineSideConfigurationDefinition definition, ChangeListener changeListener) {
		this.definition = Objects.requireNonNull(definition, "definition");
		this.modes = definition.createDefaultModes();
		this.changeListener = Objects.requireNonNull(changeListener, "changeListener");
	}

	public MachineSideConfigurationDefinition getDefinition () {
		return definition;
	}

	public MachineSideMode getMode (MachineFace face) {
		return modes.get(Objects.requireNonNull(face, "face"));
	}

	/**
	 * Resolves the mode exposed on a world side for the current machine front.
	 */
	public MachineSideMode getMode (Direction front, Direction worldSide) {
		return getMode(MachineFace.fromWorldDirection(front, worldSide));
	}

	/**
	 * Returns an immutable snapshot suitable for menus and rendering.
	 */
	public Map<MachineFace, MachineSideMode> snapshot () {
		return Collections.unmodifiableMap(new EnumMap<>(modes));
	}

	/**
	 * Changes a relative face after validating both configurability and machine
	 * support.
	 *
	 * @return {@code true} when the mode changed
	 * @throws IllegalArgumentException when the face is locked or the mode is not
	 *                                  supported on that face
	 */
	public boolean setMode (MachineFace face, MachineSideMode mode) {
		Objects.requireNonNull(face, "face");
		Objects.requireNonNull(mode, "mode");
		if (!definition.isConfigurable(face)) throw new IllegalArgumentException("Machine face '" + face.getSerializedName() + "' is not configurable");
		if (!definition.supports(face, mode)) {
			throw new IllegalArgumentException("Machine side mode '" + mode.getSerializedName() + "' is not supported on face '" + face.getSerializedName() + "'");
		}

		MachineSideMode previous = modes.get(face);
		if (previous == mode) return false;
		modes.put(face, mode);
		changeListener.onSideChanged(face, previous, mode);
		return true;
	}

	/**
	 * World-direction counterpart to {@link #setMode(MachineFace, MachineSideMode)}.
	 */
	public boolean setMode (Direction front, Direction worldSide, MachineSideMode mode) {
		return setMode(MachineFace.fromWorldDirection(front, worldSide), mode);
	}

	/**
	 * Restores every face to its configured default and emits one callback for each
	 * face that changed.
	 *
	 * @return {@code true} when at least one face changed
	 */
	public boolean reset () {
		boolean changed = false;
		for (MachineFace face : MachineFace.values()) {
			MachineSideMode previous = modes.get(face);
			MachineSideMode next = definition.getDefaultMode(face);
			if (previous == next) continue;
			modes.put(face, next);
			changeListener.onSideChanged(face, previous, next);
			changed = true;
		}
		return changed;
	}

	/**
	 * Writes every face by serialized name. Callers control the parent tag by
	 * passing a child {@link ValueOutput} when desired.
	 */
	public void save (ValueOutput output) {
		Objects.requireNonNull(output, "output");
		for (MachineFace face : MachineFace.values()) {
			output.putString(face.getSerializedName(), modes.get(face).getSerializedName());
		}
	}

	/**
	 * Loads supported values without firing runtime change callbacks. Missing,
	 * invalid, disallowed, and locked-face values fall back to that face's default,
	 * keeping old or manually edited worlds loadable.
	 */
	public void load (ValueInput input) {
		Objects.requireNonNull(input, "input");
		for (MachineFace face : MachineFace.values()) {
			MachineSideMode fallback = definition.getDefaultMode(face);
			if (!definition.isConfigurable(face)) {
				modes.put(face, fallback);
				continue;
			}
			MachineSideMode loaded = input.getString(face.getSerializedName()).flatMap(MachineSideMode::byName).filter(mode -> definition.supports(face, mode)).orElse(fallback);
			modes.put(face, loaded);
		}
	}

	@FunctionalInterface
	public interface ChangeListener {

		void onSideChanged (MachineFace face, MachineSideMode previous, MachineSideMode current);
	}
}
