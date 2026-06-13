package general.mechanics.gui.overlay;

import general.mechanics.api.entity.block.BasePoweredBlockEntity;
import general.mechanics.api.gui.MachineUiState;
import general.mechanics.gui.component.button.CloseTabButton;
import general.mechanics.gui.component.button.SideModeFaceButtonComponent;
import general.mechanics.gui.component.screen.TerminalWidget;
import general.mechanics.gui.screen.base.BaseScreen;
import general.mechanics.network.SetIoSideModeC2S;
import general.mechanics.api.util.LocalSide;
import general.mechanics.api.component.io.IoType;
import general.mechanics.gui.util.IconButtonNoBG;
import general.mechanics.GM;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import general.mechanics.gui.component.TabComponent;

import java.util.function.Supplier;

public class SideConfigOverlay extends AbstractWidget {

    private static final int TAB_SIZE = 19;
    private static final int TAB_GAP = 1;
    private static final int TAB_Y_OFFSET = 2;
    private static final int TAB_X_OFFSET_UNSELECTED = 0;
    private static final int TAB_X_OFFSET_SELECTED = 1;

    private static final Identifier ITEMS_TAB = GM.getResource("textures/gui/elements/tabs/items_tab.png");
    private static final Identifier ENERGY_TAB = GM.getResource("textures/gui/elements/tabs/energy_tab.png");
    private static final Identifier FLUIDS_TAB = GM.getResource("textures/gui/elements/tabs/fluid_tab.png");

    private final BaseScreen<?> parent;
    private final BlockPos bePos;
    private final Supplier<MachineUiState> state;

    @Getter
    private boolean visible = false;

    @Getter
    private final TerminalWidget terminal;

    private final CloseTabButton closeButton;

    private final TabComponent<IconButtonNoBG> itemsTab;
    private final TabComponent<IconButtonNoBG> energyTab;
    private final TabComponent<IconButtonNoBG> fluidsTab;

    private IoType selectedType = IoType.ITEMS;

    private final SideModeFaceButtonComponent upButton;
    private final SideModeFaceButtonComponent leftButton;
    private final SideModeFaceButtonComponent frontButton;
    private final SideModeFaceButtonComponent rightButton;
    private final SideModeFaceButtonComponent downButton;
    private final SideModeFaceButtonComponent backButton;

    public SideConfigOverlay(BaseScreen<?> parent, Supplier<MachineUiState> state, BlockPos pos) {
        super(0, 0, 0, 0, Component.empty());
        this.parent = parent;
        this.state = state;
        this.bePos = pos;

        this.terminal = new TerminalWidget(0, 0, TerminalWidget.Preset.TERMINAL_64x64);
        this.closeButton = new CloseTabButton(0, 0, this::closeScreen);

        this.itemsTab = new TabComponent<>(0, 0, null, new IconButtonNoBG(0, 0, TAB_SIZE, TAB_SIZE, ITEMS_TAB, 0, 0, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE, 0, b -> selectedType = IoType.ITEMS)) {
        };
        this.energyTab = new TabComponent<>(0, 0, null, new IconButtonNoBG(0, 0, TAB_SIZE, TAB_SIZE, ENERGY_TAB, 0, 0, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE, 0, b -> selectedType = IoType.ENERGY)) {
        };
        this.fluidsTab = new TabComponent<>(0, 0, null, new IconButtonNoBG(0, 0, TAB_SIZE, TAB_SIZE, FLUIDS_TAB, 0, 0, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE, 0, b -> selectedType = IoType.FLUIDS)) {
        };

        itemsTab.getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.io.items")));
        energyTab.getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.io.energy")));
        fluidsTab.getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.io.fluids")));

        this.upButton = new SideModeFaceButtonComponent(0, 0, Direction.UP, () -> getMode(Direction.UP), () -> cycleSide(Direction.UP));
        this.leftButton = new SideModeFaceButtonComponent(0, 0, Direction.WEST, () -> getMode(getLocalLeft()), () -> cycleSide(getLocalLeft()));
        this.frontButton = new SideModeFaceButtonComponent(0, 0, Direction.NORTH, () -> getMode(getLocalFront()), () -> cycleSide(getLocalFront()));
        this.rightButton = new SideModeFaceButtonComponent(0, 0, Direction.EAST, () -> getMode(getLocalRight()), () -> cycleSide(getLocalRight()));
        this.downButton = new SideModeFaceButtonComponent(0, 0, Direction.DOWN, () -> getMode(Direction.DOWN), () -> cycleSide(Direction.DOWN));
        this.backButton = new SideModeFaceButtonComponent(0, 0, Direction.SOUTH, () -> getMode(getLocalBack()), () -> cycleSide(getLocalBack()));

        terminal.addComponent(closeButton, 0, 0, TerminalWidget.ClampMode.OUTER);
        terminal.addComponent(itemsTab, 0, 0, TerminalWidget.ClampMode.NONE);
        terminal.addComponent(energyTab, 0, 0, TerminalWidget.ClampMode.NONE);
        terminal.addComponent(fluidsTab, 0, 0, TerminalWidget.ClampMode.NONE);
        terminal.addComponent(upButton, 0, 0, TerminalWidget.ClampMode.INNER);
        terminal.addComponent(leftButton, 0, 0, TerminalWidget.ClampMode.INNER);
        terminal.addComponent(frontButton, 0, 0, TerminalWidget.ClampMode.INNER);
        terminal.addComponent(rightButton, 0, 0, TerminalWidget.ClampMode.INNER);
        terminal.addComponent(downButton, 0, 0, TerminalWidget.ClampMode.INNER);
        terminal.addComponent(backButton, 0, 0, TerminalWidget.ClampMode.INNER);

        anchorToParent();
        layoutChildren();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        terminal.setBlocksSlotRenderingFlag(visible);
        if (visible) {
            anchorToParent();
            layoutChildren();
        }
    }

    private void anchorToParent() {
        terminal.setX(parent.getGuiLeft());
        terminal.setY(parent.getGuiTop());
    }

    private void syncBoundsToTerminal() {
        setX(terminal.getX());
        setY(terminal.getY());
        setWidth(terminal.getWidth());
        setHeight(terminal.getHeight());
    }

    private void layoutChildren() {
        syncBoundsToTerminal();
        int size = 16;
        int gap = 1;

        int cx = terminal.getInnerX() + terminal.getInnerWidth() / 2 - size / 2;
        int cy = terminal.getInnerY() + terminal.getInnerHeight() / 2 - size / 2;

        terminal.setComponentOffset(upButton, cx - terminal.getX(), (cy - (size + gap)) - terminal.getY());
        terminal.setComponentOffset(leftButton, (cx - (size + gap)) - terminal.getX(), cy - terminal.getY());
        terminal.setComponentOffset(frontButton, cx - terminal.getX(), cy - terminal.getY());
        terminal.setComponentOffset(rightButton, (cx + (size + gap)) - terminal.getX(), cy - terminal.getY());
        terminal.setComponentOffset(downButton, cx - terminal.getX(), (cy + (size + gap)) - terminal.getY());
        terminal.setComponentOffset(backButton, (cx + (size + gap)) - terminal.getX(), (cy + (size + gap)) - terminal.getY());

        int closeX = terminal.getX() + terminal.getWidth() - 12;
        int closeY = terminal.getY() + 4;
        terminal.setComponentOffset(closeButton, closeX - terminal.getX(), closeY - terminal.getY());

        layoutTab(itemsTab, 0, IoType.ITEMS);
        layoutTab(energyTab, 1, IoType.ENERGY);
        layoutTab(fluidsTab, 2, IoType.FLUIDS);
    }

    private void layoutTab(TabComponent<IconButtonNoBG> tab, int index, IoType type) {
        int xOff = (selectedType == type) ? TAB_X_OFFSET_SELECTED : TAB_X_OFFSET_UNSELECTED;

        int relX = -TAB_SIZE + xOff;
        int relY = TAB_Y_OFFSET + index * (TAB_SIZE + TAB_GAP);
        terminal.setComponentOffset(tab, relX, relY);
    }

    @Override
    protected void extractWidgetRenderState (@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pt) {
    }

    private void closeScreen() {
        this.setVisible(false);
    }

    private Direction getFacing() {
        var level = Minecraft.getInstance().level;
        if (level == null) return Direction.NORTH;

        var state = level.getBlockState(bePos);
        if (state.hasProperty(BlockStateProperties.FACING)) {
            var facing = state.getValue(BlockStateProperties.FACING);
            if (facing.getAxis().isHorizontal()) return facing;
        }
        return Direction.NORTH;
    }

    private Direction getLocalFront() {
        return LocalSide.FRONT.toWorld(getFacing());
    }

    private Direction getLocalBack() {
        return LocalSide.BACK.toWorld(getFacing());
    }

    private Direction getLocalLeft() {
        return LocalSide.LEFT.toWorld(getFacing());
    }

    private Direction getLocalRight() {
        return LocalSide.RIGHT.toWorld(getFacing());
    }

    public void renderOnTop(GuiGraphicsExtractor g, int mouseX, int mouseY, float pt) {
        if (!visible) return;

        var pose = g.pose();
        //pose.pushPose();
        // Dim entire screen
        g.fill(0, 0, parent.width, parent.height, 0x88000000);
        pose.translate(0, 0, 200);

        //RenderSystem.disableDepthTest();
        layoutChildren();
        terminal.render(g, mouseX, mouseY, pt);

        //int labelX = terminal.getInnerX() + 2;
        //int labelY = terminal.getInnerY() + terminal.getInnerHeight() - 10;
        //g.drawString(Minecraft.getInstance().font, selectedType.name(), labelX, labelY, 0xAAAAAA, false);
        //RenderSystem.enableDepthTest();

        //pose.popPose();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!visible) return false;
        if (terminal.mouseClicked(x, y, button)) return true;
        if (terminal.isMouseOver(x, y)) return true;
        closeScreen();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!visible) return false;
        if (terminal.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            layoutChildren();
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        terminal.mouseReleased(mouseX, mouseY, button);
        return true;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        if (!visible) return false;
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            closeScreen();
            return true;
        }
        return true; // modal: eat keys
    }

    private void cycleSide(Direction dir) {
        var next = getMode(dir).next();
        PacketDistributor.sendToServer(new SetIoSideModeC2S(bePos, selectedType.id(), dir.get3DDataValue(), next.id()));

        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private BasePoweredBlockEntity.SideMode getMode(Direction dir) {
        var ui = state.get();
        if (ui == null) return BasePoweredBlockEntity.SideMode.IGNORED;
        return ui.sideMode(selectedType, dir);
    }

}
