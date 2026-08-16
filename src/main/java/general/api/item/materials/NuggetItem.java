package general.api.item.materials;

import lombok.Getter;
import net.minecraft.world.item.Item;

public class NuggetItem extends Item {

	@Getter
	private final IngotItem parent;

	public NuggetItem (IngotItem parent, Properties properties) {
		super(properties);
		this.parent = parent;
		parent.setNuggetItem(this);
	}

}
