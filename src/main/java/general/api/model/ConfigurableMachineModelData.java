package general.api.model;

import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfiguration;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

import java.util.Objects;

/**
 * Immutable render-thread snapshot of a configurable machine's relative side
 * modes. Keeping this data relative makes it remain correct when the host block
 * is rotated.
 */
public final class ConfigurableMachineModelData {

	public static final ModelProperty<Modes> MODES = new ModelProperty<>(Objects::nonNull);

	private ConfigurableMachineModelData () {
	}

	public static ModelData create (MachineSideConfiguration configuration) {
		return ModelData.of(MODES, Modes.from(configuration));
	}

	public static ModelData create (Modes modes) {
		return ModelData.of(MODES, Objects.requireNonNull(modes, "modes"));
	}

	public static Modes defaults (MachineSideConfigurationDefinition definition) {
		Objects.requireNonNull(definition, "definition");
		return Modes.from(definition.createConfiguration());
	}

	public record Modes(MachineSideMode front, MachineSideMode back, MachineSideMode left, MachineSideMode right, MachineSideMode top, MachineSideMode bottom) {

		public Modes {
			Objects.requireNonNull(front, "front");
			Objects.requireNonNull(back, "back");
			Objects.requireNonNull(left, "left");
			Objects.requireNonNull(right, "right");
			Objects.requireNonNull(top, "top");
			Objects.requireNonNull(bottom, "bottom");
		}

		public static Modes from (MachineSideConfiguration configuration) {
			Objects.requireNonNull(configuration, "configuration");
			return new Modes(
					configuration.getMode(MachineFace.FRONT),
					configuration.getMode(MachineFace.BACK),
					configuration.getMode(MachineFace.LEFT),
					configuration.getMode(MachineFace.RIGHT),
					configuration.getMode(MachineFace.TOP),
					configuration.getMode(MachineFace.BOTTOM)
			);
		}

		public MachineSideMode get (MachineFace face) {
			return switch (Objects.requireNonNull(face, "face")) {
				case FRONT -> front;
				case BACK -> back;
				case LEFT -> left;
				case RIGHT -> right;
				case TOP -> top;
				case BOTTOM -> bottom;
			};
		}
	}
}
