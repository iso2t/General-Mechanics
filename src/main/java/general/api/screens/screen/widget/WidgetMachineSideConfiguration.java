package general.api.screens.screen.widget;

import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import general.api.model.ConfigurableMachineModelData;
import general.api.resources.Resource;
import general.api.screens.renderers.MachineConfigurationRenderState;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Expandable side-configuration tab with a rotatable block preview and a row
 * for every machine-relative face.
 */
public final class WidgetMachineSideConfiguration extends AbstractWidget {

	@FunctionalInterface
	public interface ChangeAction {
		boolean change (MachineFace face, MachineSideMode mode);
	}

	private static final int TEXTURE_SIZE = 256;
	private static final int OPEN_HEIGHT  = 169;

	private static final int TAB_X       = 175;
	private static final int TAB_WIDTH   = 14;
	private static final int PANEL_X     = 175;
	private static final int PANEL_WIDTH = 80;

	private static final int   VIEWPORT_X      = 1;
	private static final int   VIEWPORT_Y      = 1;
	private static final int   VIEWPORT_WIDTH  = 174;
	private static final int   VIEWPORT_HEIGHT = 138;
	private static final float MODEL_SCALE     = 44.0F;
	private static final int   CONFIG_WIDTH    = 176;
	private static final int   CONFIG_HEIGHT   = 140;

	private static final int CLOSE_X    = 244;
	private static final int CLOSE_Y    = 3;
	private static final int CLOSE_SIZE = 8;

	private static final int ROW_X      = 179;
	private static final int ROW_Y      = 15;
	private static final int ROW_WIDTH  = 72;
	private static final int ROW_HEIGHT = 25;

	private static final Identifier TAB_CLOSED   = Resource.getMainMod("textures/gui/elements/side_tab_closed.png");
	private static final Identifier TAB_SELECTED = Resource.getMainMod("textures/gui/elements/side_tab_selected.png");
	private static final Identifier TAB_OPEN     = Resource.getMainMod("textures/gui/elements/side_tab_open.png");
	private static final Identifier CONFIG_MENU  = Resource.getMainMod("textures/gui/config_menu.png");
	private static final Identifier CLOSE        = Resource.getMainMod("textures/gui/elements/close_button.png");

	private final Supplier<BlockState>                   blockState;
	private final MachineSideConfigurationDefinition     definition;
	private final Function<MachineFace, MachineSideMode> modeProvider;
	private final ChangeAction                           changeAction;

	@Getter
	private boolean     open;
	private boolean     dragging;
	private boolean     dragMoved;
	private int         dragButton;
	private int         consumedButton = -1;
	private double      pressX;
	private double      pressY;
	private float       yaw            = 135.0F;
	private float       pitch          = 25.0F;
	private MachineFace selectedFace   = MachineFace.LEFT;

	public WidgetMachineSideConfiguration (Supplier<BlockState> blockState, MachineSideConfigurationDefinition definition, Function<MachineFace, MachineSideMode> modeProvider, ChangeAction changeAction) {
		super(TEXTURE_SIZE, OPEN_HEIGHT);
		this.blockState = Objects.requireNonNull(blockState, "blockState");
		this.definition = Objects.requireNonNull(definition, "definition");
		this.modeProvider = Objects.requireNonNull(modeProvider, "modeProvider");
		this.changeAction = Objects.requireNonNull(changeAction, "changeAction");
	}

	public void setOpen (boolean open) {
		this.open = open;
		if (!open) {
			dragging = false;
			dragMoved = false;
			consumedButton = -1;
		}
	}

	/**
	 * Absolute bounds of the panel that extends beyond the owning container GUI.
	 */
	public Rect2i getOpenPanelArea (int originX, int originY) {
		if (!isVisible() || !isOpen()) throw new IllegalStateException("The machine side-configuration panel is not open");
		return new Rect2i(originX + getX() + PANEL_X, originY + getY(), PANEL_WIDTH, OPEN_HEIGHT);
	}

	@Override
	protected void renderWidget (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
		if (!open) {
			Identifier texture = contains(mouseX - x, mouseY - y, TAB_X, 0, TAB_WIDTH, OPEN_HEIGHT) ? TAB_SELECTED : TAB_CLOSED;
			blit(graphics, texture, x, y);
			if (texture == TAB_SELECTED) tooltip(graphics, mouseX, mouseY, Component.translatableWithFallback("gui.generalmechanics.machine.side_config.open", "Configure Sides"));
			return;
		}

		blit(graphics, TAB_OPEN, x, y);
		blit(graphics, CONFIG_MENU, x, y);
		graphics.blit(RenderPipelines.GUI_TEXTURED, CLOSE, x + CLOSE_X, y + CLOSE_Y, 0.0F, 0.0F, CLOSE_SIZE, CLOSE_SIZE, CLOSE_SIZE, CLOSE_SIZE);

		BlockState previewState = normalizedPreviewState();
		graphics.submitPictureInPictureRenderState(new MachineConfigurationRenderState(previewState, currentModes(), yaw, pitch, x + VIEWPORT_X, y + VIEWPORT_Y, x + VIEWPORT_X + VIEWPORT_WIDTH, y + VIEWPORT_Y + VIEWPORT_HEIGHT, MODEL_SCALE, graphics.peekScissorStack()));

		MachineFace hoveredFace = faceAt(mouseX - x, mouseY - y);
		renderRows(graphics, mouseX, mouseY, x, y, hoveredFace);
		renderTooltip(graphics, mouseX, mouseY, x, y, hoveredFace);
	}

	@Override
	protected boolean isMouseOverWidget (double mouseX, double mouseY) {
		if (!open) return contains(mouseX, mouseY, TAB_X, 0, TAB_WIDTH, OPEN_HEIGHT);
		return contains(mouseX, mouseY, 0, 0, CONFIG_WIDTH, CONFIG_HEIGHT) || contains(mouseX, mouseY, PANEL_X, 0, PANEL_WIDTH, OPEN_HEIGHT);
	}

	@Override
	protected boolean onClick (MouseButtonEvent event, int x, int y) {
		double localX = event.x() - x;
		double localY = event.y() - y;
		if (!open) {
			if (event.button() != 0) return false;
			consumedButton = event.button();
			setOpen(true);
			playClickSound();
			return true;
		}

		if (event.button() == 0 && contains(localX, localY, CLOSE_X, CLOSE_Y, CLOSE_SIZE, CLOSE_SIZE)) {
			consumedButton = event.button();
			setOpen(false);
			playClickSound();
			return true;
		}

		MachineFace rowFace = rowAt(localX, localY);
		if (rowFace != null && (event.button() == 0 || event.button() == 1)) {
			consumedButton = event.button();
			selectedFace = rowFace;
			cycle(rowFace, event.button() == 0 ? 1 : -1);
			return true;
		}

		if (contains(localX, localY, VIEWPORT_X, VIEWPORT_Y, VIEWPORT_WIDTH, VIEWPORT_HEIGHT) && (event.button() == 0 || event.button() == 1)) {
			consumedButton = event.button();
			dragging = true;
			dragMoved = false;
			dragButton = event.button();
			pressX = event.x();
			pressY = event.y();
			return true;
		}
		consumedButton = event.button();
		return true;
	}

	@Override
	protected boolean onDrag (MouseButtonEvent event, double dragX, double dragY, int x, int y) {
		if (!dragging || event.button() != dragButton) return false;
		if (Math.abs(event.x() - pressX) + Math.abs(event.y() - pressY) > 2.0) dragMoved = true;
		if (dragMoved) {
			yaw = wrapDegrees(yaw + (float) dragX * 1.5F);
			pitch = Math.clamp(pitch + (float) dragY * 1.5F, -80.0F, 80.0F);
		}
		return true;
	}

	@Override
	protected boolean onRelease (MouseButtonEvent event, int x, int y) {
		boolean consumed = event.button() == consumedButton;
		if (!dragging || event.button() != dragButton) {
			if (consumed) consumedButton = -1;
			return consumed;
		}
		dragging = false;
		consumedButton = -1;
		if (!dragMoved) {
			MachineFace face = faceAt(event.x() - x, event.y() - y);
			if (face != null) {
				selectedFace = face;
				cycle(face, event.button() == 0 ? 1 : -1);
			}
		}
		return true;
	}

	private void renderRows (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, @Nullable MachineFace viewportFace) {
		var font = Minecraft.getInstance().font;
		for (MachineFace face : MachineFace.values()) {
			int rowY = y + ROW_Y + face.id() * ROW_HEIGHT;
			boolean hovered = contains(mouseX, mouseY, x + ROW_X, rowY, ROW_WIDTH, ROW_HEIGHT);
			if (face == selectedFace || face == viewportFace) graphics.fill(x + ROW_X, rowY, x + ROW_X + ROW_WIDTH, rowY + ROW_HEIGHT, 0x80527496);
			else if (hovered) graphics.fill(x + ROW_X, rowY, x + ROW_X + ROW_WIDTH, rowY + ROW_HEIGHT, 0x80606060);
			if (!definition.isConfigurable(face)) graphics.fill(x + ROW_X, rowY, x + ROW_X + ROW_WIDTH, rowY + ROW_HEIGHT, 0x60303030);

			graphics.text(font, faceName(face), x + ROW_X + 3, rowY + 3, definition.isConfigurable(face) ? 0xFFFFFFFF : 0xFFAAAAAA, false);
			graphics.text(font, shortModeName(mode(face)), x + ROW_X + 3, rowY + 13, modeColor(mode(face)), false);
		}
	}

	private void renderTooltip (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, @Nullable MachineFace viewportFace) {
		if (contains(mouseX, mouseY, x + CLOSE_X, y + CLOSE_Y, CLOSE_SIZE, CLOSE_SIZE)) {
			tooltip(graphics, mouseX, mouseY, Component.translatableWithFallback("gui.generalmechanics.machine.side_config.close", "Close Side Configuration"));
			return;
		}

		MachineFace rowFace = rowAt(mouseX - x, mouseY - y);
		MachineFace face = rowFace != null ? rowFace : viewportFace;
		if (face == null) return;
		Component current = Component.translatableWithFallback("gui.generalmechanics.machine.side_config.face_mode", "%s: %s", faceName(face), definition.isConfigurable(face) ? modeName(mode(face)) : Component.translatableWithFallback("gui.generalmechanics.machine.side_config.locked", "Locked"));
		graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(current), mouseX, mouseY);
	}

	private void cycle (MachineFace face, int step) {
		if (!definition.isConfigurable(face)) return;
		List<MachineSideMode> modes = List.copyOf(definition.getAllowedModes(face));
		if (modes.size() < 2) return;
		int current = modes.indexOf(mode(face));
		int next = Math.floorMod((current < 0 ? 0 : current) + step, modes.size());
		if (changeAction.change(face, modes.get(next))) playClickSound();
	}

	private ConfigurableMachineModelData.Modes currentModes () {
		return new ConfigurableMachineModelData.Modes(mode(MachineFace.FRONT), mode(MachineFace.BACK), mode(MachineFace.LEFT), mode(MachineFace.RIGHT), mode(MachineFace.TOP), mode(MachineFace.BOTTOM));
	}

	private MachineSideMode mode (MachineFace face) {
		return Objects.requireNonNull(modeProvider.apply(face), "Machine side mode provider returned null");
	}

	private BlockState normalizedPreviewState () {
		BlockState state = Objects.requireNonNull(blockState.get(), "Machine block-state supplier returned null");
		return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH) : state;
	}

	private @Nullable MachineFace rowAt (double localX, double localY) {
		if (!contains(localX, localY, ROW_X, ROW_Y, ROW_WIDTH, ROW_HEIGHT * MachineFace.values().length)) return null;
		return MachineFace.byId((int) (localY - ROW_Y) / ROW_HEIGHT).orElse(null);
	}

	private @Nullable MachineFace faceAt (double localX, double localY) {
		if (!contains(localX, localY, VIEWPORT_X, VIEWPORT_Y, VIEWPORT_WIDTH, VIEWPORT_HEIGHT)) return null;
		float centerX = VIEWPORT_X + VIEWPORT_WIDTH / 2.0F;
		float centerY = VIEWPORT_Y + VIEWPORT_HEIGHT / 2.0F;
		Vector3f origin = new Vector3f((float) (localX - centerX) / MODEL_SCALE, -(float) (localY - centerY) / MODEL_SCALE, 4.0F);
		Vector3f direction = new Vector3f(0.0F, 0.0F, -1.0F);
		Quaternionf inverse = rotation().conjugate(new Quaternionf());
		inverse.transform(origin);
		inverse.transform(direction);

		RayHit hit = clipAxis(new RayHit(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, null), origin.x, direction.x, Direction.WEST, Direction.EAST);
		if (hit == null) return null;
		hit = clipAxis(hit, origin.y, direction.y, Direction.DOWN, Direction.UP);
		if (hit == null) return null;
		hit = clipAxis(hit, origin.z, direction.z, Direction.NORTH, Direction.SOUTH);
		if (hit == null || hit.far() < Math.max(hit.near(), 0.0F) || hit.face() == null) return null;
		return MachineFace.fromWorldDirection(Direction.NORTH, hit.face());
	}

	private Quaternionf rotation () {
		return new Quaternionf().rotateX((float) Math.toRadians(pitch)).rotateY((float) Math.toRadians(yaw));
	}

	private static @Nullable RayHit clipAxis (RayHit hit, float origin, float direction, Direction negativeFace, Direction positiveFace) {
		if (Math.abs(direction) < 1.0E-5F) return origin >= -0.5F && origin <= 0.5F ? hit : null;
		float first = (-0.5F - origin) / direction;
		float second = (0.5F - origin) / direction;
		Direction firstFace = negativeFace;
		if (first > second) {
			float swap = first;
			first = second;
			second = swap;
			firstFace = positiveFace;
		}
		float near = hit.near();
		Direction face = hit.face();
		if (first > near) {
			near = first;
			face = firstFace;
		}
		float far = Math.min(hit.far(), second);
		return near <= far ? new RayHit(near, far, face) : null;
	}

	private static Component faceName (MachineFace face) {
		String fallback = switch (face) {
			case FRONT -> "Front";
			case BACK -> "Back";
			case LEFT -> "Left";
			case RIGHT -> "Right";
			case TOP -> "Top";
			case BOTTOM -> "Bottom";
		};
		return Component.translatableWithFallback("gui.generalmechanics.machine.side_config.face." + face.getSerializedName(), fallback);
	}

	private static Component modeName (MachineSideMode mode) {
		String fallback = switch (mode) {
			case NONE -> "Disabled";
			case ITEM_INPUT -> "Item Input";
			case ITEM_OUTPUT -> "Item Output";
			case FLUID_INPUT -> "Fluid Input";
			case FLUID_OUTPUT -> "Fluid Output";
			case ENERGY_INPUT -> "Energy Input";
			case NETWORK -> "Network";
		};
		return Component.translatableWithFallback("gui.generalmechanics.machine.side_config.mode." + mode.getSerializedName(), fallback);
	}

	private static Component shortModeName (MachineSideMode mode) {
		String fallback = switch (mode) {
			case NONE -> "Disabled";
			case ITEM_INPUT -> "Item In";
			case ITEM_OUTPUT -> "Item Out";
			case FLUID_INPUT -> "Fluid In";
			case FLUID_OUTPUT -> "Fluid Out";
			case ENERGY_INPUT -> "Energy";
			case NETWORK -> "Network";
		};
		return Component.translatableWithFallback("gui.generalmechanics.machine.side_config.mode_short." + mode.getSerializedName(), fallback);
	}

	private static int modeColor (MachineSideMode mode) {
		return switch (mode) {
			case NONE -> 0xFFAAAAAA;
			case ITEM_INPUT -> 0xFFFFC14D;
			case ITEM_OUTPUT -> 0xFFFF8A3D;
			case FLUID_INPUT -> 0xFF5FAFFF;
			case FLUID_OUTPUT -> 0xFF327DFF;
			case ENERGY_INPUT -> 0xFF66E58A;
			case NETWORK -> 0xFFC686FF;
		};
	}

	private static float wrapDegrees (float degrees) {
		float wrapped = degrees % 360.0F;
		return wrapped < 0.0F ? wrapped + 360.0F : wrapped;
	}

	private static void blit (GuiGraphicsExtractor graphics, Identifier texture, int x, int y) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
	}

	private static void tooltip (GuiGraphicsExtractor graphics, int mouseX, int mouseY, Component tooltip) {
		graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
	}

	private static void playClickSound () {
		net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
	}

	private static boolean contains (double x, double y, int left, int top, int width, int height) {
		return x >= left && x < left + width && y >= top && y < top + height;
	}

	private record RayHit(float near, float far, @Nullable Direction face) {
	}
}
