package general.api.screens.screen.widget;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;

/**
 * Lightweight GUI-relative widget used by General API screens.
 *
 * <p>A widget's position is relative to the origin supplied by its owner. This
 * allows a screen to anchor a widget to {@code leftPos}/{@code topPos}, and a
 * composite widget to use the same contract for its children. Dimensions are
 * expressed in GUI pixels.</p>
 */
public abstract class AbstractWidget {

	@Getter
	private int x;

	@Getter
	private int y;

	@Getter
	private final int width;

	@Getter
	private int height;

	@Getter
	@Setter
	private boolean visible = true;

	@Getter
	@Setter
	private boolean active = true;

	public AbstractWidget (int width, int height) {
		if (width <= 0) throw new IllegalArgumentException("Widget width must be greater than zero");
		if (height <= 0) throw new IllegalArgumentException("Widget height must be greater than zero");
		this.width = width;
		this.height = height;
	}

	/**
	 * Renders this widget relative to the supplied owner origin.
	 */
	public final void render (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int originX, int originY) {
		if (!isVisible()) return;
		renderWidget(graphics, mouseX, mouseY, originX + x, originY + y);
	}

	/**
	 * Dispatches a mouse click using the same owner origin as {@link #render}.
	 */
	public final boolean mouseClicked (MouseButtonEvent event, int originX, int originY) {
		if (!isVisible() || !active || !isMouseOver(event.x(), event.y(), originX, originY)) return false;
		return onClick(event, originX + x, originY + y);
	}

	/**
	 * Dispatches an in-progress drag. Unlike a click, the pointer may leave the
	 * widget after the drag begins, so subclasses decide whether they own it.
	 */
	public final boolean mouseDragged (MouseButtonEvent event, double dragX, double dragY, int originX, int originY) {
		if (!isVisible() || !active) return false;
		return onDrag(event, dragX, dragY, originX + x, originY + y);
	}

	/**
	 * Dispatches a mouse release so stateful widgets can complete a click or drag.
	 */
	public final boolean mouseReleased (MouseButtonEvent event, int originX, int originY) {
		if (!isVisible() || !active) return false;
		return onRelease(event, originX + x, originY + y);
	}

	public final boolean isMouseOver (double mouseX, double mouseY, int originX, int originY) {
		int absoluteX = originX + x;
		int absoluteY = originY + y;
		return isMouseOverWidget(mouseX - absoluteX, mouseY - absoluteY);
	}

	/**
	 * Returns this widget's bounds in the coordinate space established by the
	 * supplied owner origin.
	 */
	public final Rect2i getArea (int originX, int originY) {
		return new Rect2i(originX + x, originY + y, width, getHeight());
	}

	final void setPosition (int x, int y) {
		this.x = x;
		this.y = y;
	}

	protected final void setHeight (int height) {
		if (height <= 0) throw new IllegalArgumentException("Widget height must be greater than zero");
		this.height = height;
	}

	protected abstract void renderWidget (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y);

	/**
	 * Handles an already validated click inside the widget. Coordinates identify
	 * the widget's absolute top-left corner.
	 */
	protected boolean onClick (MouseButtonEvent event, int x, int y) {
		return false;
	}

	/**
	 * Custom local-space hit testing for non-rectangular or stateful widgets.
	 */
	protected boolean isMouseOverWidget (double mouseX, double mouseY) {
		return mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < getHeight();
	}

	protected boolean onDrag (MouseButtonEvent event, double dragX, double dragY, int x, int y) {
		return false;
	}

	protected boolean onRelease (MouseButtonEvent event, int x, int y) {
		return false;
	}

}
