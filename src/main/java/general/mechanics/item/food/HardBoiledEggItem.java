package general.mechanics.item.food;

import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.api.model.IBasicModel;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class HardBoiledEggItem extends Item implements IBasicModel, RecipeDataProvider {

	private static final FoodProperties PROPERTIES = new FoodProperties.Builder().nutrition(1).saturationModifier(0f).alwaysEdible().build();

	public HardBoiledEggItem (Properties properties) {
		super(properties.food(PROPERTIES));
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		context.save(SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.EGG), RecipeCategory.FOOD, CookingBookCategory.FOOD, this, 0f, 200), "hard_boiled_egg");
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return Items.EGG;
	}
}
