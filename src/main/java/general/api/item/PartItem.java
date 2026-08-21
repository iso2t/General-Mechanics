package general.api.item;

import general.api.crafting.RecipeDataProvider;
import net.minecraft.world.item.Item;

public abstract class PartItem extends Item implements RecipeDataProvider {

	public PartItem (Properties properties) {
		super(properties);
	}

}
