package general.api.item.materials;

import lombok.Getter;
import net.minecraft.world.item.Item;

/**
 * Shared base for material forms that use the generated tinted item models.
 */
public abstract class MaterialPartItem extends Item {

	@Getter
	private final int color;

	protected MaterialPartItem (Properties properties, int color) {
		super(properties);
		this.color = color;
	}
}
