package general.api.screens.screen.widget;

import general.api.resources.Resource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vertically expanding background and layout owner for auxiliary screen widgets.
 *
 * <p>Children are centered and stacked in insertion order. Only visible children
 * participate in layout. The background is assembled from fixed top and bottom
 * caps with a one-pixel middle row tiled across the required content height.</p>
 */
public class WidgetInfoArea extends AbstractWidget {

	public static final int WIDTH                = 25;
	public static final int CONTENT_WIDTH        = WIDTH - 2;
	public static final int TOP_HEIGHT           = 4;
	public static final int MIDDLE_HEIGHT        = 1;
	public static final int BOTTOM_HEIGHT        = 4;
	public static final int CONTENT_TOP_INSET    = 1;
	public static final int CONTENT_BOTTOM_INSET = 3;

	private static final Identifier TOP    = Resource.getMainMod("textures/gui/elements/widget/widget_info_top.png");
	private static final Identifier BOTTOM = Resource.getMainMod("textures/gui/elements/widget/widget_info_bottom.png");
	private static final Identifier MIDDLE = Resource.getMainMod("textures/gui/elements/widget/widget_info_middle.png");

	private final List<AbstractWidget> children = new ArrayList<>();

	public WidgetInfoArea (int x, int y) {
		super(WIDTH, TOP_HEIGHT + BOTTOM_HEIGHT);
		setPosition(x, y);
	}

	/**
	 * Appends a child and returns it for convenient retention by the owning screen.
	 */
	public final <W extends AbstractWidget> W addWidget (W widget) {
		Objects.requireNonNull(widget, "widget");
		if (children.contains(widget)) throw new IllegalArgumentException("Widget is already present in this info area");
		if (widget.getWidth() > CONTENT_WIDTH) throw new IllegalArgumentException("Widget width " + widget.getWidth() + " exceeds info area content width " + CONTENT_WIDTH);
		children.add(widget);
		layoutChildren();
		return widget;
	}

	public final boolean removeWidget (AbstractWidget widget) {
		boolean removed = children.remove(Objects.requireNonNull(widget, "widget"));
		if (removed) layoutChildren();
		return removed;
	}

	public final List<AbstractWidget> getWidgets () {
		return List.copyOf(children);
	}

	public final boolean isEmpty () {
		for (AbstractWidget child : children) {
			if (child.isVisible()) return false;
		}
		return true;
	}

	@Override
	public boolean isVisible () {
		return super.isVisible() && !isEmpty();
	}

	@Override
	public int getHeight () {
		layoutChildren();
		return super.getHeight();
	}

	/**
	 * Returns a child's bounds in the coordinate space established by the supplied
	 * owner origin.
	 */
	public final Rect2i getWidgetArea (AbstractWidget widget, int originX, int originY) {
		requireChild(widget);
		layoutChildren();
		return widget.getArea(originX + getX(), originY + getY());
	}

	public final boolean isWidgetMouseOver (AbstractWidget widget, double mouseX, double mouseY, int originX, int originY) {
		requireChild(widget);
		layoutChildren();
		return widget.isVisible() && widget.isMouseOver(mouseX, mouseY, originX + getX(), originY + getY());
	}

	@Override
	protected void renderWidget (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
		int contentHeight = layoutChildren();
		graphics.blit(RenderPipelines.GUI_TEXTURED, TOP, x, y, 0.0F, 0.0F, WIDTH, TOP_HEIGHT, WIDTH, TOP_HEIGHT);
		graphics.blit(RenderPipelines.GUI_TEXTURED, MIDDLE, x, y + TOP_HEIGHT, 0.0F, 0.0F, WIDTH, contentHeight, WIDTH, MIDDLE_HEIGHT, WIDTH, MIDDLE_HEIGHT);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BOTTOM, x, y + TOP_HEIGHT + contentHeight, 0.0F, 0.0F, WIDTH, BOTTOM_HEIGHT, WIDTH, BOTTOM_HEIGHT);
		for (AbstractWidget child : children) {
			child.render(graphics, mouseX, mouseY, x, y);
		}
	}

	@Override
	protected boolean onClick (MouseButtonEvent event, int x, int y) {
		layoutChildren();
		for (int index = children.size() - 1; index >= 0; index--) {
			if (children.get(index).mouseClicked(event, x, y)) return true;
		}
		return false;
	}

	private int layoutChildren () {
		int childY = CONTENT_TOP_INSET;
		for (AbstractWidget child : children) {
			if (!child.isVisible()) continue;
			child.setPosition((WIDTH - child.getWidth()) / 2, childY);
			childY += child.getHeight();
		}
		int panelHeight = Math.max(TOP_HEIGHT + BOTTOM_HEIGHT, childY + CONTENT_BOTTOM_INSET);
		setHeight(panelHeight);
		return panelHeight - TOP_HEIGHT - BOTTOM_HEIGHT;
	}

	private void requireChild (AbstractWidget widget) {
		Objects.requireNonNull(widget, "widget");
		if (!children.contains(widget)) throw new IllegalArgumentException("Widget does not belong to this info area");
	}

}
