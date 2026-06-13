package general.mechanics.datagen.recipes;

import general.mechanics.registries.CoreElements;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

public class SmeltingRecipes extends CoreRecipeProvider {

    private static final int DEFAULT_SMELTING_TIME = 200;

    public SmeltingRecipes (HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

    @Override
    public void buildRecipes () {

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(CoreElements.VANADIUM_INGOT.get().getRawItem().asItem()), RecipeCategory.BREWING, CookingBookCategory.MISC, CoreElements.VANADIUM_INGOT, 0.6f, DEFAULT_SMELTING_TIME)
                .unlockedBy("has_raw_vanadium", has(CoreElements.VANADIUM_INGOT.get().getRawItem().asItem()))
                .save(consumer, CoreRecipeProvider.createKey("smelting/vanadium_ingot_from_raw_vanadium_ore"));
    }

}
