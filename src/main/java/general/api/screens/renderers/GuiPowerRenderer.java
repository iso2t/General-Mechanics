package general.api.screens.renderers;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.IntSupplier;

public class GuiPowerRenderer extends AbstractBarRenderer {

	@Getter
	private final IntSupplier energyStored;

	@Getter
	private final IntSupplier energyCapacity;

	@Getter
	@Setter
	private Color renderColor = Color.BRIGHT_GREEN;

	public GuiPowerRenderer (RenderLocation location, IntSupplier energyStored, IntSupplier energyCapacity) {
		this(location, energyStored, energyCapacity, Size.getDefault());
	}

	public GuiPowerRenderer (RenderLocation location, IntSupplier energyStored, IntSupplier energyCapacity, Size size) {
		super(location.left(), location.top(), size.width(), size.maxHeight());
		this.energyStored = energyStored;
		this.energyCapacity = energyCapacity;
	}

	public List<Component> getTooltips () {
		int stored = getEnergyStored().getAsInt();
		int cap = getEnergyCapacity().getAsInt();
		return List.of(Component.literal(stored + " / " + cap + " %s".formatted("FE")));
	}

	@Override
	public void render (GuiGraphicsExtractor guiGraphics) {
		/*int cap = getEnergyCapacity().getAsInt();
		if (cap <= 0) return;
		int storedPx = (int) (getHeight() * (getEnergyStored().getAsInt() / (float) cap));
		guiGraphics.fillGradient(getXPos(), getYPos() + (getHeight() - storedPx), getXPos() + getWidth(), getYPos() + getHeight(), Color.BRIGHT_RED.getArgb(), Color.RED.getArgb());*/
		render(guiGraphics, getXPos(), getYPos());
	}

	public void render (GuiGraphicsExtractor guiGraphics, int x, int y) {
		int cap = getEnergyCapacity().getAsInt();
		if (cap <= 0) return;
		int storedPx = (int) (getHeight() * (getEnergyStored().getAsInt() / (float) cap));
		guiGraphics.fillGradient(x, y + (getHeight() - storedPx), x + getWidth(), y + getHeight(), Color.BRIGHT_RED.getArgb(), getRenderColor().getArgb());
	}

	/**
	 * Renders this bar at its configured position relative to a screen origin.
	 */
	public void renderRelative (GuiGraphicsExtractor guiGraphics, int screenX, int screenY) {
		render(guiGraphics, screenX + getXPos(), screenY + getYPos());
	}

	public record Size(int width, int maxHeight) {

		public static Size getDefault () {
			return new Size(8, 64);
		}

	}

	public record RenderLocation(int left, int top) {

		public static RenderLocation getDefault () {
			return new RenderLocation(158, 9);
		}

	}

}
