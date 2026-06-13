package general.mechanics.recipes.builder;

import general.mechanics.recipes.FluidMixingRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public final class FluidMixingRecipeBuilder {

    private FluidMixingRecipeBuilder() {
    }

    public static void build(RecipeOutput out, Identifier id, SizedFluidIngredient input, FluidStack result) {
        List<SizedFluidIngredient> list = new ArrayList<>();
        list.add(input);
        out.accept(id, new FluidMixingRecipe(list, result), null);
    }

    public static void build(RecipeOutput out, Identifier id, SizedFluidIngredient inputA, SizedFluidIngredient inputB, FluidStack result) {
        List<SizedFluidIngredient> list = new ArrayList<>();
        list.add(inputA);
        list.add(inputB);
        out.accept(id, new FluidMixingRecipe(list, result), null);
    }

    public static void build(RecipeOutput out, Identifier id, SizedFluidIngredient inputA, SizedFluidIngredient inputB, SizedFluidIngredient inputC, FluidStack result) {
        List<SizedFluidIngredient> list = new ArrayList<>();
        list.add(inputA);
        list.add(inputB);
        list.add(inputC);
        out.accept(id, new FluidMixingRecipe(list, result), null);
    }

    public static void build(RecipeOutput out, Identifier id, List<SizedFluidIngredient> inputs, FluidStack result) {
        if (inputs.isEmpty() || inputs.size() > 3) {
            throw new IllegalArgumentException("FluidMixingRecipe must have between 1 and 3 inputs");
        }
        out.accept(id, new FluidMixingRecipe(inputs, result), null);
    }

	private static <E> void build (RecipeOutput consumer, Identifier resource, List<E> of, SizedFluidIngredient of1) {

	}
}
