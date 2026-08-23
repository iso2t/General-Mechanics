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
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

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

		generateLimestoneProgressionRecipes();
		for (var family : GenBlocks.LIMESTONE_FAMILIES) generateFamilyRecipes(family);
	}

	private void generateLimestoneProgressionRecipes () {
		var limestoneContext = new RecipeGenerationContext(registries, output, GenBlocks.LIMESTONE.getId(), GenBlocks.LIMESTONE);
		limestoneContext.save(SimpleCookingRecipeBuilder.smelting(Ingredient.of(GenBlocks.LIMESTONE), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, GenBlocks.LIMESTONE_POLISHED, 0.1F, 200), "building_blocks/limestone_polished_from_smelting");

		var polishedContext = new RecipeGenerationContext(registries, output, GenBlocks.LIMESTONE_POLISHED.getId(), GenBlocks.LIMESTONE_POLISHED);
		polishedContext.save(ShapedRecipeBuilder.shaped(polishedContext.items(), RecipeCategory.BUILDING_BLOCKS, GenBlocks.LIMESTONE_BRICKS, 4).define('#', GenBlocks.LIMESTONE_POLISHED).pattern("##").pattern("##"), "building_blocks/limestone_bricks");
	}

	private void generateFamilyRecipes (GenBlocks.BlockFamilyDefinition family) {
		var context = new RecipeGenerationContext(registries, output, family.base().getId(), family.base());

		context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.BUILDING_BLOCKS, family.slab(), 6).define('#', family.base()).pattern("###"), "building_blocks/" + family.slab().getId().getPath());
		context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.BUILDING_BLOCKS, family.stairs(), 4).define('#', family.base()).pattern("#  ").pattern("## ").pattern("###"), "building_blocks/" + family.stairs().getId().getPath());
	}

	private void generate (RecipeDataProvider provider, Identifier ownerId) {
		provider.generateRecipes(new RecipeGenerationContext(registries, output, ownerId, provider.getRecipeUnlockItem()));
	}

}
