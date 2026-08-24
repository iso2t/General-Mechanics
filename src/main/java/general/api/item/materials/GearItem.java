package general.api.item.materials;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

public class GearItem extends MaterialPartItem {

	@Getter
	private final @Nullable IngotItem parent;

	public GearItem (IngotItem parent, Properties properties) {
		super(properties, parent.getColor());
		this.parent = parent;
		parent.setGearItem(this);
	}

	public GearItem (int color, Properties properties) {
		super(properties, color);
		this.parent = null;
	}

}
