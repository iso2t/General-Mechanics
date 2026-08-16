package general.api.item.materials;

import lombok.Getter;
import net.minecraft.world.item.Item;

public class RawItem extends Item {

	@Getter
	private final IngotItem parent;

	public RawItem (IngotItem parent, Properties properties) {
		super(properties);
		this.parent = parent;
		parent.setRawItem(this);
	}

}
