package general.api.screens.screen;

import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.api.screens.menu.AbstractMenu;
import general.api.screens.renderers.GuiFluidRenderer;
import general.api.screens.renderers.GuiPowerRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import general.api.screens.screen.widget.*;
import general.api.screens.slot.ILockableSlot;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScreen<T extends AbstractMenu<?, ?>> extends AbstractContainerScreen<T> {

	private static final boolean JEI_LOADED = GenAPI.isModLoaded("jei");

	public static final int INFO_AREA_X = -26;
	public static final int INFO_AREA_Y = -1;

	/**
	 * @deprecated Query {@link #getRecipeViewerButtonRelativeArea()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static final int RECIPE_VIEWER_BUTTON_X      = INFO_AREA_X + (WidgetInfoArea.WIDTH - WidgetRecipeViewerButton.DEFAULT_RENDER_SIZE) / 2;
	/**
	 * @deprecated Query {@link #getRecipeViewerButtonRelativeArea()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static final int RECIPE_VIEWER_BUTTON_Y      = INFO_AREA_Y + WidgetInfoArea.CONTENT_TOP_INSET;
	/**
	 * @deprecated Query {@link #getRecipeViewerButtonRelativeArea()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static final int RECIPE_VIEWER_BUTTON_WIDTH  = 16;
	/**
	 * @deprecated Query {@link #getRecipeViewerButtonRelativeArea()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static final int RECIPE_VIEWER_BUTTON_HEIGHT = 16;
	/**
	 * @deprecated The lock button's position depends on which preceding widgets are visible.
	 */
	@Deprecated(forRemoval = true)
	public static final int ITEM_LOCK_BUTTON_X          = INFO_AREA_X + (WidgetInfoArea.WIDTH - WidgetItemLockButton.DEFAULT_RENDER_SIZE) / 2;
	/**
	 * @deprecated The lock button's position depends on which preceding widgets are visible.
	 */
	@Deprecated(forRemoval = true)
	public static final int ITEM_LOCK_BUTTON_Y          = RECIPE_VIEWER_BUTTON_Y + RECIPE_VIEWER_BUTTON_HEIGHT;
	/**
	 * @deprecated Query {@link #getItemLockButtonArea()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static final int ITEM_LOCK_BUTTON_WIDTH      = WidgetItemLockButton.DEFAULT_RENDER_SIZE;
	/**
	 * @deprecated Query {@link #getItemLockButtonArea()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static final int ITEM_LOCK_BUTTON_HEIGHT     = WidgetItemLockButton.DEFAULT_RENDER_SIZE;

	public static final  Identifier STATUS_ACTIVE            = Resource.getMainMod("textures/gui/elements/status_active.png");
	public static final  Identifier STATUS_INACTIVE          = Resource.getMainMod("textures/gui/elements/status_inactive.png");
	public static final  Identifier STATUS_ERROR             = Resource.getMainMod("textures/gui/elements/status_error.png");
	private static final Identifier EMPTY_LOCKED_SLOT_ICON   = Resource.getMainMod("textures/gui/elements/locked.png");
	private static final Identifier FACTORY_TITLE            = Resource.getMainMod("textures/gui/elements/factory_title.png");
	private static final int        LOCK_ICON_TEXTURE_SIZE   = 16;
	private static final int        LOCK_ICON_RENDER_SIZE    = 4;
	private static final int        FACTORY_TITLE_X          = 1;
	private static final int        FACTORY_TITLE_Y          = -16;
	private static final int        FACTORY_TITLE_WIDTH      = 173;
	private static final int        FACTORY_TITLE_HEIGHT     = 16;
	private static final int        FACTORY_TITLE_TEXT_COLOR = 0xFFE6E6E6;

	private final WidgetInfoArea       infoArea;
	private final List<AbstractWidget> overlayWidgets = new ArrayList<>();

	@Nullable
	private final WidgetRecipeViewerButton recipeViewerButton;

	@Nullable
	private final WidgetItemLockButton itemLockButton;

	@Nullable
	@Getter
	private final WidgetMachineSideConfiguration machineSideConfigurationWidget;

	@Nullable
	@Getter
	private GuiPowerRenderer powerRenderer;

	@Nullable
	@Getter
	private GuiProgressBarRenderer progressBarRenderer;

	@Nullable
	@Getter
	private GuiFluidRenderer fluidRenderer;

	public AbstractScreen (T menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		infoArea = new WidgetInfoArea(INFO_AREA_X, INFO_AREA_Y);
		recipeViewerButton = JEI_LOADED && menu.hasRecipeDefinitions() ? infoArea.addWidget(new WidgetRecipeViewerButton()) : null;
		itemLockButton = menu.hasItemSlotLocking() ? infoArea.addWidget(new WidgetItemLockButton(menu::areItemSlotsLocked, event -> {
			if (minecraft.gameMode == null) return false;
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AbstractMenu.TOGGLE_ITEM_LOCK_BUTTON);
			return true;
		})) : null;
		machineSideConfigurationWidget = menu.hasMachineSideConfiguration() ? addOverlayWidget(new WidgetMachineSideConfiguration(() -> menu.getBlockEntity().getBlockState(), menu.getMachineSideConfigurationDefinition(), menu::getMachineSideMode, (face, mode) -> {
			if (minecraft.gameMode == null) return false;
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AbstractMenu.machineSideConfigurationButton(face, mode));
			return true;
		})) : null;
		refreshMachineSideConfigurationAvailability();
	}

	public AbstractScreen (T menu, Inventory inventory, String title) {
		this(menu, inventory, Component.translatable(title));
	}

	protected final void setPowerRenderer (@Nullable GuiPowerRenderer powerRenderer) {
		this.powerRenderer = powerRenderer;
	}

	protected final void setProgressBarRenderer (@Nullable GuiProgressBarRenderer progressBarRenderer) {
		this.progressBarRenderer = progressBarRenderer;
	}

	protected final void setFluidRenderer (@Nullable GuiFluidRenderer fluidRenderer) {
		this.fluidRenderer = fluidRenderer;
	}

	/**
	 * Appends a custom widget to the shared expandable info area.
	 */
	protected final <W extends AbstractWidget> W addInfoWidget (W widget) {
		return infoArea.addWidget(widget);
	}

	/**
	 * Adds a GUI-relative overlay rendered above container labels and slots.
	 */
	protected final <W extends AbstractWidget> W addOverlayWidget (W widget) {
		overlayWidgets.add(java.util.Objects.requireNonNull(widget, "widget"));
		return widget;
	}

	@Override
	public void extractBackground (@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos, topPos, 0.f, 0.f, imageWidth, imageHeight, 256, 256);
		if (menu.isFactoryPresentationActive()) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, FACTORY_TITLE, leftPos + FACTORY_TITLE_X, topPos + FACTORY_TITLE_Y, 0.0F, 0.0F, FACTORY_TITLE_WIDTH, FACTORY_TITLE_HEIGHT, FACTORY_TITLE_WIDTH, FACTORY_TITLE_HEIGHT);
		}
		infoArea.render(graphics, mouseX, mouseY, leftPos, topPos + 1); // Move down one to align tops

		if (getPowerRenderer() != null) getPowerRenderer().renderRelative(graphics, leftPos, topPos, mouseX, mouseY);
		if (getProgressBarRenderer() != null) getProgressBarRenderer().render(graphics, leftPos, topPos, mouseX, mouseY);
		if (getFluidRenderer() != null) getFluidRenderer().render(graphics, leftPos, topPos, mouseX, mouseY);
	}

	@Override
	protected void extractLabels (@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (!menu.isFactoryPresentationActive()) {
			super.extractLabels(graphics, mouseX, mouseY);
			return;
		}
		int factoryTitleX = FACTORY_TITLE_X + (FACTORY_TITLE_WIDTH - font.width(title)) / 2;
		int factoryTitleY = (FACTORY_TITLE_Y + (FACTORY_TITLE_HEIGHT - font.lineHeight) / 2) + 1;
		graphics.text(font, title, factoryTitleX, factoryTitleY, FACTORY_TITLE_TEXT_COLOR, true);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -12566464, false);
	}

	@Override
	public void extractContents (@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		refreshMachineSideConfigurationAvailability();
		super.extractContents(graphics, mouseX, mouseY, partialTick);
		if (overlayWidgets.isEmpty()) return;
		graphics.nextStratum();
		for (AbstractWidget widget : overlayWidgets) widget.render(graphics, mouseX, mouseY, leftPos, topPos);
	}

	@Override
	public boolean mouseClicked (@NonNull MouseButtonEvent event, boolean doubleClick) {
		refreshMachineSideConfigurationAvailability();
		for (int index = overlayWidgets.size() - 1; index >= 0; index--) {
			if (overlayWidgets.get(index).mouseClicked(event, leftPos, topPos)) return true;
		}
		if (infoArea.mouseClicked(event, leftPos, topPos)) return true;
		GuiFluidRenderer renderer = getFluidRenderer();
		if (event.button() == 0 && renderer != null && menu.hasFluidContainerInteraction() && !menu.getCarried().isEmpty() && renderer.isMouseOver(event.x(), event.y(), leftPos, topPos)) {
			if (minecraft.gameMode != null) {
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AbstractMenu.TRANSFER_FLUID_CONTAINER_BUTTON);
			}
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged (@NonNull MouseButtonEvent event, double dragX, double dragY) {
		refreshMachineSideConfigurationAvailability();
		for (int index = overlayWidgets.size() - 1; index >= 0; index--) {
			if (overlayWidgets.get(index).mouseDragged(event, dragX, dragY, leftPos, topPos)) return true;
		}
		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased (@NonNull MouseButtonEvent event) {
		refreshMachineSideConfigurationAvailability();
		for (int index = overlayWidgets.size() - 1; index >= 0; index--) {
			if (overlayWidgets.get(index).mouseReleased(event, leftPos, topPos)) return true;
		}
		return super.mouseReleased(event);
	}

	private void refreshMachineSideConfigurationAvailability () {
		WidgetMachineSideConfiguration widget = machineSideConfigurationWidget;
		if (widget == null) return;
		boolean available = menu.isMachineSideConfigurationAvailable();
		widget.setVisible(available);
		widget.setActive(available);
		if (!available && widget.isOpen()) widget.setOpen(false);
	}

	@Override
	protected void renderSlotContents (@NonNull GuiGraphicsExtractor graphics, ItemStack itemStack, @NonNull Slot slot, @Nullable String itemCount) {
		if (itemStack.isEmpty() && slot instanceof ILockableSlot lockableSlot && lockableSlot.isLocked()) {
			ItemStack ghost = lockableSlot.getGhostStack();
			if (!ghost.isEmpty()) {
				graphics.fakeItem(ghost, slot.x, slot.y, slot.x + slot.y * imageWidth);
				graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x66FFFFFF);
				return;
			}
			super.renderSlotContents(graphics, itemStack, slot, itemCount);
			// draw lock icon on slot
			graphics.blit(RenderPipelines.GUI_TEXTURED, EMPTY_LOCKED_SLOT_ICON, slot.x - 2, slot.y + 18 - LOCK_ICON_RENDER_SIZE, 0.0F, 0.0F, LOCK_ICON_RENDER_SIZE, LOCK_ICON_RENDER_SIZE, LOCK_ICON_TEXTURE_SIZE, LOCK_ICON_TEXTURE_SIZE, LOCK_ICON_TEXTURE_SIZE, LOCK_ICON_TEXTURE_SIZE);
			return;
		}
		super.renderSlotContents(graphics, itemStack, slot, itemCount);
	}

	@Override
	protected void extractTooltip (@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		for (int index = overlayWidgets.size() - 1; index >= 0; index--) {
			if (overlayWidgets.get(index).isMouseOver(mouseX, mouseY, leftPos, topPos)) return;
		}
		super.extractTooltip(graphics, mouseX, mouseY);
		if (hoveredSlot instanceof ILockableSlot lockableSlot && !hoveredSlot.hasItem() && lockableSlot.isLocked() && menu.getCarried().isEmpty()) {
			ItemStack ghost = lockableSlot.getGhostStack();
			if (!ghost.isEmpty()) {
				graphics.setTooltipForNextFrame(font, getTooltipFromContainerItem(ghost), ghost.getTooltipImage(), ghost, mouseX, mouseY, ghost.get(DataComponents.TOOLTIP_STYLE));
			}
		}
	}

	/**
	 * Whether this screen currently exposes the shared recipe-viewer button.
	 */
	public final boolean hasRecipeViewerButton () {
		return recipeViewerButton != null;
	}

	public final boolean hasItemLockButton () {
		return itemLockButton != null;
	}

	public final boolean isItemLockButtonHovered (double mouseX, double mouseY) {
		return itemLockButton != null && infoArea.isWidgetMouseOver(itemLockButton, mouseX, mouseY, leftPos, topPos);
	}

	/**
	 * Tests JEI's GUI-relative mouse coordinates against the shared button.
	 */
	public final boolean isRecipeViewerButtonHovered (double guiMouseX, double guiMouseY) {
		return recipeViewerButton != null && infoArea.isWidgetMouseOver(recipeViewerButton, guiMouseX, guiMouseY, 0, 0);
	}

	/**
	 * Absolute screen-space area occupied outside the primary GUI texture.
	 */
	public final Rect2i getRecipeViewerButtonArea () {
		if (recipeViewerButton == null) throw new IllegalStateException("This screen does not expose a recipe-viewer widget");
		return infoArea.getWidgetArea(recipeViewerButton, leftPos, topPos);
	}

	/**
	 * GUI-relative recipe-viewer widget bounds used by optional viewer integrations.
	 */
	public final Rect2i getRecipeViewerButtonRelativeArea () {
		if (recipeViewerButton == null) throw new IllegalStateException("This screen does not expose a recipe-viewer widget");
		return infoArea.getWidgetArea(recipeViewerButton, 0, 0);
	}

	public final Rect2i getItemLockButtonArea () {
		if (itemLockButton == null) throw new IllegalStateException("This screen does not expose an item-lock widget");
		return infoArea.getWidgetArea(itemLockButton, leftPos, topPos);
	}

	public final boolean hasInfoArea () {
		return infoArea.isVisible();
	}

	/**
	 * Absolute screen-space area occupied by the entire expandable widget panel.
	 */
	public final Rect2i getInfoAreaArea () {
		if (!hasInfoArea()) throw new IllegalStateException("This screen has no visible info-area widgets");
		return infoArea.getArea(leftPos, topPos);
	}

	/**
	 * Whether the expandable machine side-configuration panel is currently open.
	 */
	public final boolean isMachineSideConfigurationOpen () {
		WidgetMachineSideConfiguration widget = machineSideConfigurationWidget;
		return widget != null && widget.isVisible() && widget.isOpen();
	}

	/**
	 * Absolute screen-space area occupied by the open machine side-configuration panel.
	 */
	public final Rect2i getMachineSideConfigurationArea () {
		WidgetMachineSideConfiguration widget = machineSideConfigurationWidget;
		if (widget == null) throw new IllegalStateException("This screen does not expose machine side configuration");
		return widget.getOpenPanelArea(leftPos, topPos);
	}

	/**
	 * The texture to use for this screen.
	 *
	 * @return {@link Identifier} path to the texture.
	 */
	public abstract Identifier getTexture ();

}
