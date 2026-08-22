package general.api.screens.renderers;

import general.api.resources.Resource;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;

public class GuiProgressBarRenderer extends AbstractBarRenderer {

	private static final int DEFAULT_LENGTH    = 22;
	private static final int DEFAULT_THICKNESS = 3;
	private static final int FRAME_PADDING     = 1;

	private static final Identifier VERTICAL_TEXTURE   = Resource.getMainMod("textures/gui/elements/progress_bar.png");
	private static final Identifier HORIZONTAL_TEXTURE = Resource.getMainMod("textures/gui/elements/progress_bar_horizontal.png");

	@Getter
	private       int         progress = 0;
	private final IntSupplier progressSupplier;
	private final IntSupplier maxProgressSupplier;

	@Getter
	private RenderMode renderMode = RenderMode.VERTICAL;

	public GuiProgressBarRenderer (int xMin, int yMin) {
		this(xMin, yMin, RenderMode.VERTICAL);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, RenderMode renderMode) {
		this(xMin, yMin, defaultWidth(renderMode), defaultHeight(renderMode), renderMode, () -> 0, () -> 0);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, int width, int height) {
		this(xMin, yMin, width, height, RenderMode.VERTICAL, () -> 0, () -> 0);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
		this(xMin, yMin, RenderMode.VERTICAL, progressSupplier, maxProgressSupplier);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, RenderMode renderMode, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
		this(xMin, yMin, defaultWidth(renderMode), defaultHeight(renderMode), renderMode, progressSupplier, maxProgressSupplier);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, int width, int height, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
		this(xMin, yMin, width, height, RenderMode.VERTICAL, progressSupplier, maxProgressSupplier);
	}

	public GuiProgressBarRenderer (int xMin, int yMin, int width, int height, RenderMode renderMode, IntSupplier progressSupplier, IntSupplier maxProgressSupplier) {
		super(xMin, yMin, width, height);
		this.renderMode = Objects.requireNonNull(renderMode, "renderMode");
		this.progressSupplier = Objects.requireNonNull(progressSupplier, "progressSupplier");
		this.maxProgressSupplier = Objects.requireNonNull(maxProgressSupplier, "maxProgressSupplier");
	}

	public Identifier getTexture () {
		return switch (renderMode) {
			case VERTICAL -> VERTICAL_TEXTURE;
			case HORIZONTAL -> HORIZONTAL_TEXTURE;
		};
	}

	/**
	 * Changes how progress is interpreted without changing this renderer's stored
	 * dimensions. Prefer the render-mode constructors when using the standard
	 * 22-by-3 or 3-by-22 dimensions.
	 */
	public void setRenderMode (RenderMode renderMode) {
		this.renderMode = Objects.requireNonNull(renderMode, "renderMode");
	}

	public List<Component> getTooltips () {
		int max = maxProgressSupplier.getAsInt();
		int current = Math.clamp(progressSupplier.getAsInt(), 0, Math.max(max, 0));
		int percent = max > 0 ? (int) ((long) current * 100 / max) : progress * 100 / getProgressLength();
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
		this.progress = Math.clamp(amount, 0, getProgressLength());
		int renderX = x + getXPos();
		int renderY = y + getYPos();

		int frameWidth = getWidth() + FRAME_PADDING * 2;
		int frameHeight = getHeight() + FRAME_PADDING * 2;
		int textureWidth = renderMode == RenderMode.VERTICAL ? DEFAULT_THICKNESS + FRAME_PADDING * 2 : DEFAULT_LENGTH + FRAME_PADDING * 2;
		int textureHeight = renderMode == RenderMode.VERTICAL ? DEFAULT_LENGTH + FRAME_PADDING * 2 : DEFAULT_THICKNESS + FRAME_PADDING * 2;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), renderX - FRAME_PADDING, renderY - FRAME_PADDING, 0, 0, frameWidth, frameHeight, textureWidth, textureHeight, textureWidth, textureHeight);

		if (progress <= 0) return;
		switch (renderMode) {
			case VERTICAL -> guiGraphics.fillGradient(renderX, renderY + getHeight() - progress, renderX + getWidth(), renderY + getHeight(), Color.GREEN.getArgb(), Color.BRIGHT_GREEN.getArgb());
			case HORIZONTAL -> guiGraphics.fillGradient(renderX, renderY, renderX + progress, renderY + getHeight(), Color.GREEN.getArgb(), Color.BRIGHT_GREEN.getArgb());
		}
	}

	public void render (GuiGraphicsExtractor guiGraphics, int screenX, int screenY, int mouseX, int mouseY) {
		render(guiGraphics, scaledProgress(), screenX, screenY);
		int x = screenX + getXPos() - 1;
		int y = screenY + getYPos() - 1;
		if (mouseX >= x && mouseX < x + getWidth() + 2 && mouseY >= y && mouseY < y + getHeight() + 2) {
			guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, getTooltips(), mouseX, mouseY);
		}
	}

	private int scaledProgress () {
		int max = maxProgressSupplier.getAsInt();
		if (max <= 0) return 0;
		int current = Math.clamp(progressSupplier.getAsInt(), 0, max);
		int length = getProgressLength();
		return Math.clamp((int) Math.ceil(current * (double) length / max), 0, length);
	}

	private int getProgressLength () {
		return switch (renderMode) {
			case VERTICAL -> getHeight();
			case HORIZONTAL -> getWidth();
		};
	}

	private static int defaultWidth (RenderMode renderMode) {
		return Objects.requireNonNull(renderMode, "renderMode") == RenderMode.VERTICAL ? DEFAULT_THICKNESS : DEFAULT_LENGTH;
	}

	private static int defaultHeight (RenderMode renderMode) {
		return Objects.requireNonNull(renderMode, "renderMode") == RenderMode.VERTICAL ? DEFAULT_LENGTH : DEFAULT_THICKNESS;
	}

	public enum RenderMode {
		VERTICAL,
		HORIZONTAL
	}

}
