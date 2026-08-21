package general.api.screens.screen;

import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.api.screens.menu.AbstractMenu;
import general.api.screens.renderers.GuiFluidRenderer;
import general.api.screens.renderers.GuiPowerRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class AbstractScreen<T extends AbstractMenu<?, ?>> extends AbstractContainerScreen<T> {

	private static final boolean JEI_LOADED = GenAPI.isModLoaded("jei");

	public static final int RECIPE_VIEWER_BUTTON_X      = -16;
	public static final int RECIPE_VIEWER_BUTTON_Y      = 4;
	public static final int RECIPE_VIEWER_BUTTON_WIDTH  = 16;
	public static final int RECIPE_VIEWER_BUTTON_HEIGHT = 16;

	public static final Identifier STATUS_ACTIVE   = Resource.getMainMod("textures/gui/elements/status_active.png");
	public static final Identifier STATUS_INACTIVE = Resource.getMainMod("textures/gui/elements/status_inactive.png");
	public static final Identifier STATUS_ERROR    = Resource.getMainMod("textures/gui/elements/status_error.png");
	public static final Identifier INFO_ICON       = Resource.getMainMod("textures/gui/elements/info.png");
	public static final Identifier LOCKED_ICON     = Resource.getMainMod("textures/gui/elements/locked.png");
	public static final Identifier UNLOCKED_ICON   = Resource.getMainMod("textures/gui/elements/unlocked.png");

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
	}

	public AbstractScreen (T menu, Inventory inventory, String title) {
		super(menu, inventory, Component.translatable(title));
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

	@Override
	public void extractBackground (@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos, topPos, 0.f, 0.f, imageWidth, imageHeight, 256, 256);
		if (hasRecipeViewerButton()) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, INFO_ICON, leftPos + RECIPE_VIEWER_BUTTON_X, topPos + RECIPE_VIEWER_BUTTON_Y, 0.0F, 0.0F, RECIPE_VIEWER_BUTTON_WIDTH, RECIPE_VIEWER_BUTTON_HEIGHT, RECIPE_VIEWER_BUTTON_WIDTH, RECIPE_VIEWER_BUTTON_HEIGHT);
		}

		if (getPowerRenderer() != null) getPowerRenderer().renderRelative(graphics, leftPos, topPos);
		if (getProgressBarRenderer() != null) getProgressBarRenderer().render(graphics, leftPos, topPos, mouseX, mouseY);
		if (getFluidRenderer() != null) getFluidRenderer().render(graphics, leftPos, topPos, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked (MouseButtonEvent event, boolean doubleClick) {
		GuiFluidRenderer renderer = getFluidRenderer();
		if (event.button() == 0 && renderer != null && menu.hasFluidContainerSource() && !menu.getCarried().isEmpty() && renderer.isMouseOver(event.x(), event.y(), leftPos, topPos)) {
			if (minecraft.gameMode != null) {
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AbstractMenu.FILL_FLUID_CONTAINER_BUTTON);
			}
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	/**
	 * Whether this screen currently exposes the shared recipe-viewer button.
	 */
	public final boolean hasRecipeViewerButton () {
		return JEI_LOADED && menu.hasRecipeDefinitions();
	}

	/**
	 * Tests JEI's GUI-relative mouse coordinates against the shared button.
	 */
	public final boolean isRecipeViewerButtonHovered (double guiMouseX, double guiMouseY) {
		return hasRecipeViewerButton() && guiMouseX >= RECIPE_VIEWER_BUTTON_X && guiMouseX < RECIPE_VIEWER_BUTTON_X + RECIPE_VIEWER_BUTTON_WIDTH && guiMouseY >= RECIPE_VIEWER_BUTTON_Y && guiMouseY < RECIPE_VIEWER_BUTTON_Y + RECIPE_VIEWER_BUTTON_HEIGHT;
	}

	/**
	 * Absolute screen-space area occupied outside the primary GUI texture.
	 */
	public final Rect2i getRecipeViewerButtonArea () {
		return new Rect2i(leftPos + RECIPE_VIEWER_BUTTON_X, topPos + RECIPE_VIEWER_BUTTON_Y, RECIPE_VIEWER_BUTTON_WIDTH, RECIPE_VIEWER_BUTTON_HEIGHT);
	}

	/**
	 * The texture to use for this screen.
	 *
	 * @return {@link Identifier} path to the texture.
	 */
	public abstract Identifier getTexture ();

}
