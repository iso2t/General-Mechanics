package general.api.block;

import general.api.crafting.IRecipeProvider;

public abstract class RecipeProviderBlock extends DecorativeBlock implements IRecipeProvider {

	public RecipeProviderBlock(Properties properties) {
		super(properties);
	}

}
