package general.mechanics.common.item;

import lombok.Getter;
import net.minecraft.world.item.Item;

/**
 * Reusable shape tool consumed by neither the Stamping Press recipe nor its
 * automation. The shape also selects an existing material-form icon for the
 * generated item model.
 */
public class StampingDieItem extends Item {

	@Getter
	private final Shape shape;

	@Getter
	private final int color;

	public StampingDieItem (Properties properties, Shape shape) {
		super(properties.stacksTo(1));
		this.shape = shape;
		this.color = 0xFF555B60;
	}

	public enum Shape {
		GEAR("item/material/gear"),
		NUGGET("item/material/nugget");

		@Getter
		private final String texture;

		Shape (String texture) {
			this.texture = texture;
		}
	}
}
