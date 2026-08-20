package general.api.screens.renderers;

import general.api.resources.Resource;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.IntSupplier;

public class GuiProgressBarRenderer extends AbstractBarRenderer {
	private static final Identifier TEXTURE = Resource.getMainMod("textures/gui/elements/progress_bar.png");

	@Getter
	private       int         progress = 0;
	private final IntSupplier progressSupplier;
	private final IntSupplier maxProgressSupplier;

	public GuiProgressBarRenderer (int xMin, int yMin) {
		this(xMin, yMin, 3, 22, () -> 0, () -> 0);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, int width, int height) {
		this(xMin, yMin, width, height, () -> 0, () -> 0);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
		this(xMin, yMin, 3, 22, progressSupplier, maxProgressSupplier);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, int width, int height, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
		super(xMin, yMin, width, height);
		this.progressSupplier = progressSupplier;
		this.maxProgressSupplier = maxProgressSupplier;
	}

	public List<Component> getTooltips () {
		int max = maxProgressSupplier.getAsInt();
		int current = Math.clamp(progressSupplier.getAsInt(), 0, Math.max(max, 0));
		int percent = max > 0 ? (int) ((long) current * 100 / max) : progress * 100 / getHeight();
		return List.of(Component.literal(percent + "%"));
	}

	@Override
	public void render (GuiGraphicsExtractor guiGraphics) {
		render(guiGraphics, scaledProgress(), 0, 0);
	}

	public void render (GuiGraphicsExtractor guiGraphics, int amount) {
		render(guiGraphics, amount, 0, 0);
	}

	public void render (GuiGraphicsExtractor guiGraphics, int amount, int x, int y) {
		this.progress = Math.clamp(amount, 0, getHeight());
		int renderX = x + getXPos();
		int renderY = y + getYPos();
		if (getWidth() == 3 && getHeight() == 22) {
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, renderX - 1, renderY - 1, 0, 0, 5, 24, 5, 24);
		}
		if (progress > 0) {
			guiGraphics.fillGradient(renderX, renderY + (getHeight() - progress), renderX + getWidth(), renderY + getHeight(), Color.GREEN.getArgb(), Color.BRIGHT_GREEN.getArgb());
		}
	}

	public void render (GuiGraphicsExtractor guiGraphics, int screenX, int screenY, int mouseX, int mouseY) {
		render(guiGraphics, scaledProgress(), screenX, screenY);
		int x = screenX + getXPos() - 1;
		int y = screenY + getYPos() - 1;
		if (mouseX >= x && mouseX < x + getWidth() + 2 && mouseY >= y && mouseY < y + getHeight() + 2) {
			guiGraphics.setComponentTooltipForNextFrame(net.minecraft.client.Minecraft.getInstance().font, getTooltips(), mouseX, mouseY);
		}
	}

	private int scaledProgress () {
		int max = maxProgressSupplier.getAsInt();
		if (max <= 0) return 0;
		int current = Math.clamp(progressSupplier.getAsInt(), 0, max);
		return Math.clamp((int) Math.ceil(current * (double) getHeight() / max), 0, getHeight());
	}

}
