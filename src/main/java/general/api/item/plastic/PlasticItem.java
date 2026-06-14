package general.api.item.plastic;

import lombok.Getter;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

@Getter
public class PlasticItem extends Item {

	@Getter
	private final PlasticTypeItem parent;

	@Getter
	private final DyeColor color;

	public PlasticItem (PlasticTypeItem parent, DyeColor color, Properties properties) {
		super(properties);
		this.parent = parent;
		this.color = color;
		parent.addColoredVariant(this);
	}

}
