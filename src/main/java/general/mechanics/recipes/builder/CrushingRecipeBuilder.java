package general.mechanics.recipes.builder;

import general.mechanics.recipes.CrushingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class CrushingRecipeBuilder {

    public static void build (RecipeOutput out, Identifier id, Ingredient input, ItemLike result) {
        out.accept(ResourceKey.create(Registries.RECIPE, id), new CrushingRecipe(input, new ItemStack(result.asItem())), null);
    }

    public static void build(RecipeOutput out, Identifier id, Ingredient inputs, ItemStack result) {
        out.accept(ResourceKey.create(Registries.RECIPE, id), new CrushingRecipe(inputs, result), null);
    }

    /*public static void build(RecipeOutput out, Identifier id, TagKey<Item> tag, ItemLike result) {
        out.accept(ResourceKey.create(Registries.RECIPE, id), new CrushingRecipe(Ingredient.of(tag), new ItemStack(result.asItem())), null);
    }*/

}
