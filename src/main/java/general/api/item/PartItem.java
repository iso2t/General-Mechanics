package general.api.item;

import general.api.crafting.IRecipeProvider;
import net.minecraft.world.item.Item;

public abstract class PartItem extends Item implements IRecipeProvider {

	public PartItem (Properties properties) {
		super(properties);
	}

}
