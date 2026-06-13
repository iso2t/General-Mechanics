package general.mechanics.gui.component;

import general.mechanics.GM;
import general.mechanics.api.gui.MachineUiState;
import general.mechanics.gui.component.button.*;
import general.mechanics.gui.menu.base.BaseMenu;
import general.mechanics.gui.screen.base.BaseScreen;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

/**
 * Represents a UI tab used for displaying and managing machine settings in a graphical interface.
 * This tab includes various configuration options such as redstone mode, auto import/export toggles,
 * and general machine state toggles. The tab integrates into a parent GUI screen and provides mechanisms
 * for displaying buttons, handling user interactions, and updating machine states.
 */
public class MachineSettingsTab {

    private final BaseScreen<?> parent;

    private final int guiOffset;

    @Getter
    private boolean isSideTabOpen;

    @Getter
    private boolean settingsPanelOpen;
    private boolean buttonsAdded;

    private RedstoneButton redstoneButton;
    private AutoExportButton autoExportButton;
    private AutoImportButton autoImportButton;
    private EnabledToggleButton enabledToggleButton;
    private CloseTabButton closeTabButton;
    private SideConfigButton sideConfigButton;

    private final Identifier sideTabClosed = GM.getResource("textures/gui/elements/side_tab_closed.png");
    private final Identifier sideTabSelected = GM.getResource("textures/gui/elements/side_tab_selected.png");
    private final Identifier sideTabOpen = GM.getResource("textures/gui/elements/side_tab_open.png");
    private final Identifier upgradeSlotGui = GM.getResource("textures/gui/elements/upgrade_slot.png");

    public MachineSettingsTab(BaseScreen<?> parent, int guiOffset) {
        this.parent = parent;
        this.guiOffset = guiOffset;
        this.isSideTabOpen = false;
        this.settingsPanelOpen = false;
        this.buttonsAdded = false;
    }

    public void init() {
        this.buttonsAdded = false;
        setupButtons();

        if (isSideTabOpen) {
            addButtonsToScreen();
        }
    }

    public void onClose() {
        closeScreen();
    }

    public void closeScreen() {
        isSideTabOpen = false;
        settingsPanelOpen = false;
        getMenu().setSettingsPanelOpen(false);
        removeButtonsFromScreen();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (parent.width - parent.getImageWidth()) / 2;
        int y = (parent.height - parent.getImageHeight()) / 2;

        if (parent.gmIsMouseAboveArea((int) mouseX, (int) mouseY, x + parent.getImageWidth() + guiOffset, y, 0, 0, 12, parent.getImageHeight())
                && !isSideTabOpen
                && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            isSideTabOpen = true;
            settingsPanelOpen = true;
            getMenu().setSettingsPanelOpen(true);
            addButtonsToScreen();
            return true;
        }
        return false;
    }

    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        if (!parent.isConfigurable() || Minecraft.getInstance().screen != parent) {
            return;
        }

        if (parent.gmIsMouseAboveArea(mouseX, mouseY, x + parent.getImageWidth() + guiOffset, y, 0, 0, 12, 176) && !isSideTabOpen) {
            graphics.blit(sideTabSelected, x, y, 0, 0, 256, 256);
        } else if (!isSideTabOpen) {
            graphics.blit(sideTabClosed, x, y, 0, 0, 256, 256);
        }

        toggleSideTab(graphics, mouseX, mouseY, x, y);
    }

    private void toggleSideTab(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        if (!isSideTabOpen) return;
        graphics.blit(sideTabOpen, x, y, 0, 0, 256, 256);

        addTabElements(graphics, mouseX, mouseY, x, y);
    }

    private void addTabElements(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        int posX = parent.getGuiLeft() + parent.getImageWidth() + guiOffset;
        int posY = parent.getGuiTop() + 6;
        int panelWidth = 80;

        graphics.textRenderer().accept(posX + (panelWidth / 2), posY, Component.translatable("gui.gm.settings"));

        drawSlotWithDesc(graphics, posX, posY);
        refreshButtons();
        addButtonsToScreen();

        parent.addAdditionalTabElements(graphics, mouseX, mouseY, x, y);
    }

    private void refreshButtons() {
        if (redstoneButton != null) redstoneButton.refresh();
        if (autoExportButton != null) autoExportButton.refresh();
        if (autoImportButton != null) autoImportButton.refresh();
        if (enabledToggleButton != null) enabledToggleButton.refresh();
    }

    private void drawSlotWithDesc (GuiGraphicsExtractor graphics, int posX, int posY) {
        graphics.blit(getUpgradeSlotGui(), posX + 1, posY + 10, 0, 0, 18, 18, 18, 18);
        graphics.blit(getUpgradeSlotGui(), posX + 1, posY + 28, 0, 0, 18, 18, 18, 18);
        graphics.blit(getUpgradeSlotGui(), posX + 1, posY + 46, 0, 0, 18, 18, 18, 18);
        graphics.blit(getUpgradeSlotGui(), posX + 1, posY + 64, 0, 0, 18, 18, 18, 18);
    }

    private void setupButtons() {
        int posX = parent.getGuiLeft() + parent.getImageWidth() + guiOffset;
        int posY = parent.getGuiTop() + 6;
        int panelWidth = 80;
        int buttonCenter = posX + (panelWidth / 2) - 10;

        Supplier<MachineUiState> stateSupplier = () -> getMenu().getUiState();
        Supplier<BlockPos> posSupplier = () -> getMenu().blockEntity.getBlockPos();

        this.redstoneButton = new RedstoneButton(buttonCenter - 21, posY + 85, stateSupplier, posSupplier);
        this.autoExportButton = new AutoExportButton(buttonCenter, posY + 85, stateSupplier, posSupplier);
        this.autoImportButton = new AutoImportButton(buttonCenter, posY + 106, stateSupplier, posSupplier);
        this.enabledToggleButton = new EnabledToggleButton(buttonCenter + 21, posY + 85, stateSupplier, posSupplier);

        this.sideConfigButton = new SideConfigButton((buttonCenter + 10) - 35, posY + 135, 70, 20, this::sidedConfig);
        this.closeTabButton = new CloseTabButton(buttonCenter + 35, posY, this::closeScreen);
    }

    private void sidedConfig() {
        parent.openSideConfig();
    }

    private void addButtonsToScreen() {
        if (!buttonsAdded) {
            parent.gmAddRenderableWidget(redstoneButton.getBuilder());
            parent.gmAddRenderableWidget(autoExportButton.getBuilder());
            parent.gmAddRenderableWidget(autoImportButton.getBuilder());
            parent.gmAddRenderableWidget(enabledToggleButton.getBuilder());
            parent.gmAddRenderableWidget(sideConfigButton.getBuilder());
            parent.gmAddRenderableWidget(closeTabButton.getBuilder());
            buttonsAdded = true;
        }
    }

    private void removeButtonsFromScreen() {
        if (buttonsAdded) {
            parent.gmRemoveWidget(redstoneButton.getBuilder());
            parent.gmRemoveWidget(autoExportButton.getBuilder());
            parent.gmRemoveWidget(autoImportButton.getBuilder());
            parent.gmRemoveWidget(enabledToggleButton.getBuilder());
            parent.gmRemoveWidget(sideConfigButton.getBuilder());
            parent.gmRemoveWidget(closeTabButton.getBuilder());
            buttonsAdded = false;
        }
    }

    private Identifier getUpgradeSlotGui () {
        return upgradeSlotGui;
    }

    private BaseMenu<?, ?> getMenu() {
        return parent.gmGetMenu();
    }
}
