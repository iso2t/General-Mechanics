package general.api.item.materials;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

public class DustItem extends MaterialPartItem {

	@Getter
	private final @Nullable IngotItem parent;

	public DustItem (IngotItem parent, Properties properties) {
		super(properties, parent.getColor());
		this.parent = parent;
		parent.setDustItem(this);
	}

	public DustItem (int color, Properties properties) {
		super(properties, color);
		this.parent = null;
	}

}
