package general.mechanics.item.food;

import general.api.model.IBasicModel;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class HoneyBunItem extends Item implements IBasicModel {

	private static final FoodProperties PROPERTIES = new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f).alwaysEdible().build();

	public HoneyBunItem (Properties properties) {
		super(properties.food(PROPERTIES));
	}

}
