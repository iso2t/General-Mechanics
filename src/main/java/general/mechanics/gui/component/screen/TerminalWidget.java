package general.mechanics.gui.component.screen;

import general.mechanics.GM;
import general.mechanics.gui.component.TabComponent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Reusable terminal panel widget with fixed outer bounds (texture-defined)
 * and a constrained inner content area.
 */
public class TerminalWidget extends AbstractWidget implements SlotRenderBlocker {

    private static final int BORDER = 4;
    private static final int TITLE_BAR_HEIGHT = 12;
    private static final int TEX_W = 256;
    private static final int TEX_H = 256;

    public enum Preset {
        TERMINAL_64x64("textures/gui/terminal/terminal_64x64.png", 64, 64),
        TERMINAL_128x64("textures/gui/terminal/terminal_128x64.png", 128, 64),
        TERMINAL_128x128("textures/gui/terminal/terminal_128x128.png", 128, 128);

        private final Identifier texture;
        private final int innerWidth;
        private final int innerHeight;

        Preset(String texturePath, int innerWidth, int innerHeight) {
            this.texture = GM.getResource(texturePath);
            this.innerWidth = innerWidth;
            this.innerHeight = innerHeight;
        }

        public Identifier texture() {
            return texture;
        }

        public int innerWidth() {
            return innerWidth;
        }

        public int innerHeight() {
            return innerHeight;
        }
    }

    @Getter
    private final Identifier texture;

    @Getter
    private final int innerWidth;
    @Getter
    private final int innerHeight;

    private final List<Child> children = new ArrayList<>();

    @Getter
    @Setter
    private boolean draggable = true;

    @Getter
    @Setter
    private boolean blocksSlotRenderingFlag = false;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    @Override
    public boolean blocksSlotRendering() {
        return blocksSlotRenderingFlag;
    }

    public TerminalWidget(int x, int y, Preset preset) {
        super(x, y, preset.innerWidth() + BORDER * 2, preset.innerHeight() + BORDER * 2, Component.empty());
        this.texture = preset.texture();
        this.innerWidth = preset.innerWidth();
        this.innerHeight = preset.innerHeight();
    }

    public TerminalWidget (int x, int y, Identifier texture, int width, int height) {
        super(x, y, width + BORDER * 2, height + BORDER * 2, Component.empty());
        this.texture = texture;
        this.innerWidth = width;
        this.innerHeight = height;
    }

    public void clearComponents() {
        children.clear();
    }

    public void addComponent(TabComponent<? extends AbstractWidget> component, int relativeX, int relativeY) {
        addComponent(component, relativeX, relativeY, ClampMode.INNER);
    }

    public void addComponent(TabComponent<? extends AbstractWidget> component, int relativeX, int relativeY, ClampMode clampMode) {
        children.add(new Child(component, relativeX, relativeY, clampMode));
        updateChildPositions();
    }

    public void addComponent(TabComponent<? extends AbstractWidget> component) {
        addComponent(component, component.getPositionX(), component.getPositionY());
    }

    public void setComponentOffset(TabComponent<? extends AbstractWidget> component, int relativeX, int relativeY) {
        for (Child child : children) {
            if (child.component == component) {
                child.relativeX = relativeX;
                child.relativeY = relativeY;
                updateChildPositions();
                return;
            }
        }
    }

    public void setComponentClampMode(TabComponent<? extends AbstractWidget> component, ClampMode clampMode) {
        for (Child child : children) {
            if (child.component == component) {
                child.clampMode = clampMode;
                updateChildPositions();
                return;
            }
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        updateChildPositions();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateChildPositions();
    }

    public int getInnerX() {
        return getX() + BORDER;
    }

    public int getInnerY() {
        return getY() + BORDER;
    }

    public boolean isInTitleBar(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + width
                && mouseY >= getY() && mouseY <= getY() + TITLE_BAR_HEIGHT;
    }

    private void updateChildPositions() {
        for (Child child : children) {
            AbstractWidget w = child.component.getBuilder();

            int x = getX() + child.relativeX;
            int y = getY() + child.relativeY;

            int finalX = x;
            int finalY = y;

            if (child.clampMode == ClampMode.INNER) {
                int innerX = getInnerX();
                int innerY = getInnerY();
                int innerW = getInnerWidth();
                int innerH = getInnerHeight();
                finalX = clamp(x, innerX, innerX + Math.max(0, innerW - w.getWidth()));
                finalY = clamp(y, innerY, innerY + Math.max(0, innerH - w.getHeight()));
            } else if (child.clampMode == ClampMode.OUTER) {
                finalX = clamp(x, getX(), getX() + Math.max(0, width - w.getWidth()));
                finalY = clamp(y, getY(), getY() + Math.max(0, height - w.getHeight()));
            }

            w.setX(finalX);
            w.setY(finalY);
        }
    }

    private static int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    @Override
    protected void extractWidgetRenderState (@NotNull GuiGraphicsExtractor g, int mouseX, int mouseY, float pt) {
        g.blit(texture, getX(), getY(), 0, 0, width, height, TEX_W, TEX_H);

        updateChildPositions();
        for (Child child : children) {
            child.component.getBuilder().render(g, mouseX, mouseY, pt);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        updateChildPositions();
        for (Child child : children) {
            if (child.component.getBuilder().mouseClicked(mouseX, mouseY, button)) return true;
        }

        if (!isMouseOver(mouseX, mouseY)) return false;

        if (draggable && button == GLFW.GLFW_MOUSE_BUTTON_1 && isInTitleBar(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = (int) mouseX - getX();
            dragOffsetY = (int) mouseY - getY();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!visible) return false;
        if (!draggable) return false;
        if (!dragging) return false;
        if (button != GLFW.GLFW_MOUSE_BUTTON_1) return false;

        setX((int) mouseX - dragOffsetX);
        setY((int) mouseY - dragOffsetY);
        updateChildPositions();
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        dragging = false;
        return false;
    }

    public enum ClampMode {
        INNER,
        OUTER,
        NONE
    }

    private static final class Child {
        private final TabComponent<? extends AbstractWidget> component;
        private int relativeX;
        private int relativeY;
        private ClampMode clampMode;

        private Child(TabComponent<? extends AbstractWidget> component, int relativeX, int relativeY, ClampMode clampMode) {
            this.component = component;
            this.relativeX = relativeX;
            this.relativeY = relativeY;
            this.clampMode = clampMode;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }
}
