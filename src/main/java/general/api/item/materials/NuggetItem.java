package general.api.item.materials;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

public class NuggetItem extends MaterialPartItem {

	@Getter
	private final @Nullable IngotItem parent;

	public NuggetItem (IngotItem parent, Properties properties) {
		super(properties, parent.getColor());
		this.parent = parent;
		parent.setNuggetItem(this);
	}

	public NuggetItem (int color, Properties properties) {
		super(properties, color);
		this.parent = null;
	}

}
