package general.api.screens.renderers;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;

public class GuiPowerRenderer extends AbstractBarRenderer {

	@Getter
	private static final NumberFormat format = NumberFormat.getIntegerInstance();

	@Getter
	private final IntSupplier energyStored;

	@Getter
	private final IntSupplier energyCapacity;

	@Getter
	@Setter
	private Color renderColor = Color.GREEN;

	public GuiPowerRenderer (RenderLocation location, IntSupplier energyStored, IntSupplier energyCapacity) {
		this(location, energyStored, energyCapacity, Size.getDefault());
	}

	public GuiPowerRenderer (RenderLocation location, IntSupplier energyStored, IntSupplier energyCapacity, Size size) {
		super(location.left(), location.top(), size.width(), size.maxHeight());
		this.energyStored = Objects.requireNonNull(energyStored, "energyStored");
		this.energyCapacity = Objects.requireNonNull(energyCapacity, "energyCapacity");
	}

	public List<Component> getTooltips () {
		int stored = getEnergyStored().getAsInt();
		int cap = getEnergyCapacity().getAsInt();
		return List.of(Component.literal(format.format(stored) + " / " + format.format(cap) + " %s".formatted("FE")));
	}

	@Override
	public void render (GuiGraphicsExtractor guiGraphics) {
		render(guiGraphics, getXPos(), getYPos());
	}

	public void render (GuiGraphicsExtractor guiGraphics, int x, int y) {
		int cap = getEnergyCapacity().getAsInt();
		if (cap <= 0) return;
		int stored = Math.clamp(getEnergyStored().getAsInt(), 0, cap);
		int storedPx = (int) (getHeight() * (stored / (float) cap));
		guiGraphics.fillGradient(x, y + (getHeight() - storedPx), x + getWidth(), y + getHeight(), getRenderColor().getArgb(), getRenderColor().getArgb());
	}

	/**
	 * Renders this bar at its configured position relative to a screen origin.
	 */
	public void renderRelative (GuiGraphicsExtractor guiGraphics, int screenX, int screenY) {
		render(guiGraphics, screenX + getXPos(), screenY + getYPos());
	}

	public void renderRelative (GuiGraphicsExtractor guiGraphics, int screenX, int screenY, int mouseX, int mouseY) {
		int x = screenX + getXPos();
		int y = screenY + getYPos();
		render(guiGraphics, x, y);
		if (mouseX >= x && mouseX < x + getWidth() && mouseY >= y && mouseY < y + getHeight()) {
			guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, getTooltips(), mouseX, mouseY);
		}
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
