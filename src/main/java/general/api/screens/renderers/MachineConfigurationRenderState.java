package general.api.screens.renderers;

import general.api.model.ConfigurableMachineModelData;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Immutable state submitted by the machine-configuration GUI viewport.
 */
public record MachineConfigurationRenderState(BlockState blockState, ConfigurableMachineModelData.Modes modes, float yaw, float pitch, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea,
                                              @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {

	public MachineConfigurationRenderState {
		Objects.requireNonNull(blockState, "blockState");
		Objects.requireNonNull(modes, "modes");
	}

	public MachineConfigurationRenderState (BlockState blockState, ConfigurableMachineModelData.Modes modes, float yaw, float pitch, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea) {
		this(blockState, modes, yaw, pitch, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
	}
}
