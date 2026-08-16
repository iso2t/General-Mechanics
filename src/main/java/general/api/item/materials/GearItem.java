package general.api.item.materials;

import lombok.Getter;
import net.minecraft.world.item.Item;

public class GearItem extends Item {

	@Getter
	private final IngotItem parent;

	public GearItem (IngotItem parent, Properties properties) {
		super(properties);
		this.parent = parent;
		parent.setGearItem(this);
	}

}
