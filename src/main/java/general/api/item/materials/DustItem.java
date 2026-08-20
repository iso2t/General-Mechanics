package general.api.item.materials;

import lombok.Getter;
import net.minecraft.world.item.Item;

public class DustItem extends Item {

	@Getter
	private final IngotItem parent;

	public DustItem (IngotItem parent, Properties properties) {
		super(properties);
		this.parent = parent;
		parent.setDustItem(this);
	}

}
