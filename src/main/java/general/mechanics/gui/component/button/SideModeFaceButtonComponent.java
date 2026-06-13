package general.mechanics.gui.component.button;

import general.mechanics.api.entity.block.BasePoweredBlockEntity;
import general.mechanics.gui.util.IconButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SideModeFaceButtonComponent extends TabButtonComponent {

	private static final int SIZE = 16;

	private final Direction direction;

	public SideModeFaceButtonComponent (int positionX, int positionY, Direction direction, Supplier<BasePoweredBlockEntity.SideMode> modeSupplier, Runnable onPress) {
		super(positionX, positionY, null, (x, y, press) -> new ModeIconButton(x, y, direction, modeSupplier, press), onPress);
		this.direction = direction;

		getBuilder().setTooltip(Tooltip.create(Component.literal(getFaceName(direction))));
	}

	public Direction getDirection () {
		return direction;
	}

	public void setPosition (int x, int y) {
		getBuilder().setX(x);
		getBuilder().setY(y);
	}

	private static final class ModeIconButton extends IconButton {

		private final Supplier<BasePoweredBlockEntity.SideMode> modeSupplier;

		private ModeIconButton (int x, int y, Direction direction, Supplier<BasePoweredBlockEntity.SideMode> modeSupplier, Runnable onPress) {
			super(x, y, SIZE, SIZE, null, 0, 0, 0, 0, 0, 0, 0, button -> onPress.run());
			this.modeSupplier = modeSupplier;
		}

		@Override
		protected void extractContents (@NotNull GuiGraphicsExtractor g, int mouseX, int mouseY, float pt) {
			int tint = switch (modeSupplier.get()) {
				case INPUT -> 0x8833AA33;
				case OUTPUT -> 0x88CC3333;
				case IGNORED -> 0x88777777;
			};
			g.fill(getX(), getY(), getX() + width, getY() + height, tint);
		}
	}

	private static String getFaceName (Direction dir) {
		return switch (dir) {
			case UP -> "Top";
			case DOWN -> "Bottom";
			case NORTH -> "Front";
			case SOUTH -> "Back";
			case WEST -> "Left";
			case EAST -> "Right";
		};
	}
}
