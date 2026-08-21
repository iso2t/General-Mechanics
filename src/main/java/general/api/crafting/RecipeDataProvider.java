package general.api.crafting;

import net.minecraft.world.level.ItemLike;

/**
 * An item, block, or other registered object that contributes recipes during
 * data generation.
 */
public interface RecipeDataProvider {

	void generateRecipes (RecipeGenerationContext context);

	/**
	 * Item used by the context's default recipe-unlock criterion.
	 */
	ItemLike getRecipeUnlockItem ();
}
