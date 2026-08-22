package general.api.screens.renderers;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractBarRenderer {

	@Getter
	private final int xPos;

	@Getter
	private final int yPos;

	@Getter
	private final int width;

	@Getter
	private final int height;

	public AbstractBarRenderer (int xPos, int yPos, int width, int height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}

	public abstract void render (GuiGraphicsExtractor graphics);

	@Getter
	public enum Color {
		RED(0xFF600B00),
		BRIGHT_RED(0xFFB51500),
		ORANGE(0xFFFF4500),
		GREEN(0xFF00FF00),
		DARK_GREEN(0xFF1E7E34),
		BRIGHT_GREEN(0xFF00B500),
		BLUE(0xFF0000FF),
		YELLOW(0xFFFFFF00),
		BLACK(0xFF000000),
		WHITE(0xFFFFFFFF);

		private final int argb;

		Color (int argb) {
			this.argb = argb;
		}

		@Override
		public String toString () {
			return String.format("#%08X", argb);
		}
	}

}