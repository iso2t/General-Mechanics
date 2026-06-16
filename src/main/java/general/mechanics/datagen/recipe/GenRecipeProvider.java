package general.mechanics.datagen.recipe;

import general.api.crafting.IRecipeProvider;
import general.mechanics.GenMech;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenParts;
import general.mechanics.registries.GenTools;
import lombok.NonNull;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class GenRecipeProvider extends RecipeProvider {

	protected final RecipeOutput consumer;

	protected GenRecipeProvider (HolderLookup.Provider registries, RecipeOutput consumer) {
		super(registries, consumer);
		this.consumer = consumer;
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
			return GenMech.NAME + " Recipes";
		}
	}

	@Override
	protected void buildRecipes () {
		for (var item : GenItems.INSTANCE.getItems()) {
			if (item.get() instanceof IRecipeProvider provider) {
				provider.registerCraftingRecipes(this.items, consumer, has(provider.getCriterionItem()));
			}
		}

		for (var tool : GenTools.INSTANCE.getItems()) {
			if (tool.get() instanceof IRecipeProvider provider) {
				provider.registerCraftingRecipes(this.items, consumer, has(provider.getCriterionItem()));
			}
		}

		for (var part : GenParts.INSTANCE.getItems()) {
			if (part.get() instanceof IRecipeProvider provider) {
				provider.registerCraftingRecipes(this.items, consumer, has(provider.getCriterionItem()));
			}
		}

		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			if (block.get() instanceof IRecipeProvider provider) {
				provider.registerCraftingRecipes(this.items, consumer, has(provider.getCriterionItem()));
			}
		}
	}

}
