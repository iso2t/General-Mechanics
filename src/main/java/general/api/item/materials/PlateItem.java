package general.api.item.materials;

import lombok.Getter;
import net.minecraft.world.item.Item;

public class PlateItem extends Item {

	@Getter
	private final IngotItem parent;

	public PlateItem (IngotItem parent, Properties properties) {
		super(properties);
		this.parent = parent;
		parent.setPlateItem(this);
	}

}
