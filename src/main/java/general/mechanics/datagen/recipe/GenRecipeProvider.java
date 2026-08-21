package general.mechanics.datagen.recipe;

import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.mechanics.Mechanics;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import lombok.NonNull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class GenRecipeProvider extends RecipeProvider {

	protected GenRecipeProvider (HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	public static class Runner extends RecipeProvider.Runner {

		public Runner (PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
			super(packOutput, registries);
		}

		@Override
		protected @NonNull RecipeProvider createRecipeProvider (HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput recipeOutput) {
			return new GenRecipeProvider(provider, recipeOutput);
		}

		@Override
		public @NonNull String getName () {
			return Mechanics.NAME + " Recipes";
		}
	}

	@Override
	protected void buildRecipes () {
		for (var item : GenItems.INSTANCE.getItems()) {
			if (item.get() instanceof RecipeDataProvider provider) {
				generate(provider, BuiltInRegistries.ITEM.getKey(item.get()));
			}
		}

		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			if (block.get() instanceof RecipeDataProvider provider) {
				generate(provider, BuiltInRegistries.BLOCK.getKey(block.get()));
			}
		}
	}

	private void generate (RecipeDataProvider provider, Identifier ownerId) {
		provider.generateRecipes(new RecipeGenerationContext(registries, output, ownerId, provider.getRecipeUnlockItem()));
	}

}
