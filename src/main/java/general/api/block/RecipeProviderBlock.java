package general.api.block;

import general.api.crafting.IRecipeProvider;

/**
 * A block that has an in-line recipe definition.
 */
public abstract class RecipeProviderBlock extends DecorativeBlock implements IRecipeProvider {

	public RecipeProviderBlock(Properties properties) {
		super(properties);
	}

}
