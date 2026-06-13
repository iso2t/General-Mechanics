package general.mechanics.datagen.recipes;

import general.mechanics.GM;
import general.mechanics.api.util.IDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class CoreRecipeProvider extends RecipeProvider implements IDataProvider {

	protected final RecipeOutput consumer;

    public CoreRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
		this.consumer = output;
    }

	@Override
	protected void buildRecipes() {
	}

	public static class Runner extends RecipeProvider.Runner {

		protected Runner (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
			super(packOutput, registries);
		}

		@Override
		protected @NonNull RecipeProvider createRecipeProvider (HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput recipeOutput) {
			return new CoreRecipeProvider(provider, recipeOutput);
		}

		@Override
		public @NonNull String getName () {
			return GM.NAME + " Recipes";
		}
	}

	public static ResourceKey<Recipe<?>> createKey (String name) {
		return ResourceKey.create(Registries.RECIPE, GM.getResource(name));
	}

}
