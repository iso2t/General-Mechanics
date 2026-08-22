package general.api.machine.config;

import java.util.*;

/**
 * Immutable type-level rules used to create machine side configurations.
 *
 * <p>A machine class should normally keep one static definition and create a
 * lightweight {@link MachineSideConfiguration} from it for each block entity.</p>
 */
public final class MachineSideConfigurationDefinition {

	private static final MachineSideConfiguration.ChangeListener NO_CHANGE_LISTENER = (face, previous, current) -> {
	};

	private final EnumMap<MachineFace, MachineSideMode>      defaults;
	private final EnumMap<MachineFace, Set<MachineSideMode>> allowedModes;
	private final EnumSet<MachineFace>                       lockedFaces;

	private MachineSideConfigurationDefinition (Builder builder) {
		this.defaults = new EnumMap<>(builder.defaults);
		this.allowedModes = new EnumMap<>(MachineFace.class);
		for (MachineFace face : MachineFace.values()) {
			this.allowedModes.put(face, Collections.unmodifiableSet(EnumSet.copyOf(builder.allowedModes.get(face))));
		}
		this.lockedFaces = builder.lockedFaces.clone();
	}

	public static Builder builder () {
		return new Builder();
	}

	public MachineSideConfiguration createConfiguration () {
		return createConfiguration(NO_CHANGE_LISTENER);
	}

	public MachineSideConfiguration createConfiguration (MachineSideConfiguration.ChangeListener changeListener) {
		return new MachineSideConfiguration(this, changeListener);
	}

	public MachineSideMode getDefaultMode (MachineFace face) {
		return defaults.get(Objects.requireNonNull(face, "face"));
	}

	public boolean isConfigurable (MachineFace face) {
		return !lockedFaces.contains(Objects.requireNonNull(face, "face"));
	}

	public boolean supports (MachineFace face, MachineSideMode mode) {
		Objects.requireNonNull(face, "face");
		Objects.requireNonNull(mode, "mode");
		return allowedModes.get(face).contains(mode);
	}

	/**
	 * Returns the supported modes in enum declaration order.
	 */
	public Set<MachineSideMode> getAllowedModes (MachineFace face) {
		return allowedModes.get(Objects.requireNonNull(face, "face"));
	}

	EnumMap<MachineFace, MachineSideMode> createDefaultModes () {
		return new EnumMap<>(defaults);
	}

	/**
	 * Defines the supported modes, defaults, and immutable faces for one machine
	 * type.
	 */
	public static final class Builder {

		private final EnumMap<MachineFace, MachineSideMode>      defaults     = new EnumMap<>(MachineFace.class);
		private final EnumMap<MachineFace, Set<MachineSideMode>> allowedModes = new EnumMap<>(MachineFace.class);
		private final EnumSet<MachineFace>                       lockedFaces  = EnumSet.noneOf(MachineFace.class);

		private Builder () {
			for (MachineFace face : MachineFace.values()) {
				defaults.put(face, MachineSideMode.NONE);
				allowedModes.put(face, EnumSet.of(MachineSideMode.NONE));
			}
		}

		/**
		 * Adds modes to every machine face.
		 */
		public Builder allow (MachineSideMode... modes) {
			requireModes(modes);
			for (MachineFace face : MachineFace.values()) allow(face, modes);
			return this;
		}

		/**
		 * Adds modes to one machine face.
		 */
		public Builder allow (MachineFace face, MachineSideMode... modes) {
			Objects.requireNonNull(face, "face");
			requireModes(modes);
			for (MachineSideMode mode : modes) allowedModes.get(face).add(mode);
			return this;
		}

		/**
		 * Replaces the complete set of modes supported by one face.
		 */
		public Builder modes (MachineFace face, MachineSideMode first, MachineSideMode... remaining) {
			Objects.requireNonNull(face, "face");
			Objects.requireNonNull(first, "first");
			requireModeElements(remaining);
			allowedModes.put(face, EnumSet.of(first, remaining));
			return this;
		}

		public Builder defaultMode (MachineFace face, MachineSideMode mode) {
			defaults.put(Objects.requireNonNull(face, "face"), Objects.requireNonNull(mode, "mode"));
			return this;
		}

		public Builder lock (MachineFace face) {
			lockedFaces.add(Objects.requireNonNull(face, "face"));
			return this;
		}

		public Builder lock (MachineFace face, MachineSideMode mode) {
			return defaultMode(face, mode).lock(face);
		}

		public MachineSideConfigurationDefinition build () {
			for (MachineFace face : MachineFace.values()) {
				MachineSideMode defaultMode = defaults.get(face);
				if (!allowedModes.get(face).contains(defaultMode)) {
					throw new IllegalStateException("Default machine side mode '" + defaultMode.getSerializedName() + "' is not supported on face '" + face.getSerializedName() + "'");
				}
			}
			return new MachineSideConfigurationDefinition(this);
		}

		private static void requireModes (MachineSideMode[] modes) {
			Objects.requireNonNull(modes, "modes");
			if (modes.length == 0) throw new IllegalArgumentException("At least one machine side mode is required");
			requireModeElements(modes);
		}

		private static void requireModeElements (MachineSideMode[] modes) {
			for (int index = 0; index < modes.length; index++) Objects.requireNonNull(modes[index], "mode at index " + index);
		}
	}
}
