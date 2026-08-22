package general.api.model;

import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.Objects;

/**
 * Machine model whose non-front faces reflect a persisted side configuration.
 *
 * <p>The default texture convention appends the configured resource suffix to
 * the machine's ordinary top, bottom, or side texture. Implementations can
 * override {@link #getConfiguredTexture(Direction, MachineSideMode)} when their
 * texture family uses a different layout.</p>
 */
public interface IConfigurableMachineModel extends IMachineModel {

	MachineSideConfigurationDefinition getSideConfigurationDefinition ();

	default Identifier getConfiguredTexture (Direction face, MachineSideMode mode) {
		Objects.requireNonNull(face, "face");
		Objects.requireNonNull(mode, "mode");
		Identifier base = switch (face) {
			case UP -> getTopTexture();
			case DOWN -> getBottomTexture();
			default -> getSideTexture();
		};
		String suffix = switch (mode) {
			case NONE -> "";
			case ITEM_INPUT -> "_item";
			case ITEM_OUTPUT -> "_item_output";
			case FLUID_INPUT -> "_fluid";
			case FLUID_OUTPUT -> "_fluid_output";
			case ENERGY_INPUT -> "_power";
			case NETWORK -> "_network";
		};
		return suffix.isEmpty() ? base : base.withPath(path -> path + suffix);
	}
}
