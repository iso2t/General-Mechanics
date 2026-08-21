package general.api.block;

import general.api.crafting.RecipeDataProvider;

/**
 * A block that has an in-line recipe definition.
 */
public abstract class RecipeProviderBlock extends DecorativeBlock implements RecipeDataProvider {

	public RecipeProviderBlock (Properties properties) {
		super(properties);
	}

}
