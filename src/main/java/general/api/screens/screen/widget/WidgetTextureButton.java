package general.api.screens.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Textured button with supplier-backed appearance and tooltip state.
 *
 * <p>Suppliers are evaluated when rendered, allowing synchronized menu state to
 * change a button without rebuilding the widget tree.</p>
 */
public class WidgetTextureButton extends AbstractWidget {

	@FunctionalInterface
	public interface ClickAction {
		boolean click (MouseButtonEvent event);
	}

	private final           Supplier<Identifier> texture;
	private final @Nullable Supplier<Component>  tooltip;
	private final @Nullable ClickAction          clickAction;
	private final           int                  textureWidth;
	private final           int                  textureHeight;

	/**
	 * Creates a button whose texture and displayed dimensions are identical.
	 */
	public WidgetTextureButton (int width, int height, Supplier<Identifier> texture, @Nullable Supplier<Component> tooltip, @Nullable ClickAction clickAction) {
		this(width, height, width, height, texture, tooltip, clickAction);
	}

	/**
	 * Creates a button with independent displayed and source-texture dimensions.
	 * The complete source texture is scaled into the widget's displayed bounds;
	 * hover and click detection use the displayed dimensions.
	 */
	public WidgetTextureButton (int width, int height, int textureWidth, int textureHeight, Supplier<Identifier> texture, @Nullable Supplier<Component> tooltip, @Nullable ClickAction clickAction) {
		super(width, height);
		if (textureWidth <= 0) throw new IllegalArgumentException("Texture width must be greater than zero");
		if (textureHeight <= 0) throw new IllegalArgumentException("Texture height must be greater than zero");
		this.texture = Objects.requireNonNull(texture, "texture");
		this.tooltip = tooltip;
		this.clickAction = clickAction;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	protected void renderWidget (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
		Identifier currentTexture = Objects.requireNonNull(texture.get(), "Widget texture supplier returned null");
		graphics.blit(RenderPipelines.GUI_TEXTURED, currentTexture, x, y, 0.0F, 0.0F, getWidth(), getHeight(), textureWidth, textureHeight, textureWidth, textureHeight);
		if (tooltip != null && isActive() && mouseX >= x && mouseX < x + getWidth() && mouseY >= y && mouseY < y + getHeight()) {
			Component currentTooltip = Objects.requireNonNull(tooltip.get(), "Widget tooltip supplier returned null");
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, currentTooltip, mouseX, mouseY);
		}
	}

	@Override
	protected boolean onClick (MouseButtonEvent event, int x, int y) {
		if (event.button() != 0) return false;
		playClickSound();
		return clickAction != null && clickAction.click(event);
	}

	/**
	 * Plays Minecraft's standard UI-button feedback. Subclasses may override this
	 * when a specialized control needs different audible feedback.
	 */
	protected void playClickSound () {
		net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
	}
}
