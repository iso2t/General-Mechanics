package general.api.screens.screen;

import general.api.screens.menu.AbstractMenu;
import general.api.screens.renderers.GuiFluidRenderer;
import general.api.screens.renderers.GuiPowerRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class AbstractScreen<T extends AbstractMenu<?, ?>> extends AbstractContainerScreen<T> {

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

		if (getPowerRenderer() != null) getPowerRenderer().renderRelative(graphics, leftPos, topPos);
		if (getProgressBarRenderer() != null) getProgressBarRenderer().render(graphics, leftPos, topPos, mouseX, mouseY);
		if (getFluidRenderer() != null) getFluidRenderer().render(graphics, leftPos, topPos, mouseX, mouseY);
	}

	/**
	 * The texture to use for this screen.
	 * @return {@link Identifier} path to the texture.
	 */
	public abstract Identifier getTexture();

}
