package general.api.crafting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Standard ordered recipe sources and adapters for machine processors.
 */
public final class MachineRecipeSources {

	private MachineRecipeSources () {
	}

	/**
	 * Reads registered recipes belonging to a machine definition.
	 */
	public static MachineRecipeSource registered (MachineRecipeDefinition<?> definition) {
		Objects.requireNonNull(definition, "definition");
		return (input, level) -> {
			var matches = new ArrayList<RecipeHolder<MachineRecipe>>();
			for (RecipeHolder<MachineRecipe> holder : level.recipeAccess().recipeMap().byType(definition.type())) {
				if (holder.value().matches(input, level)) matches.add(holder);
			}
			return List.copyOf(matches);
		};
	}

	public static <T extends AbstractCookingRecipe> MachineRecipeSource cooking (MachineRecipeDefinition<NoRecipeData> definition, RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot) {
		return cooking(definition, type, inputSlot, outputSlot, input -> true);
	}

	/**
	 * Adapts live cooking recipes into a no-payload machine definition. The
	 * predicate can reserve additional machine inputs, such as requiring an empty
	 * catalyst slot for vanilla fallback.
	 */
	public static <T extends AbstractCookingRecipe> MachineRecipeSource cooking (MachineRecipeDefinition<NoRecipeData> definition, RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate) {
		return cooking(definition, type, inputSlot, outputSlot, predicate, recipe -> NoRecipeData.INSTANCE);
	}

	/**
	 * Adapts cooking recipes while deriving machine-specific payload data from the
	 * native recipe.
	 */
	public static <D, T extends AbstractCookingRecipe> MachineRecipeSource cooking (MachineRecipeDefinition<D> definition, RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate, Function<? super T, ? extends D> dataFactory) {
		Objects.requireNonNull(definition, "definition");
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(inputSlot, "inputSlot");
		Objects.requireNonNull(outputSlot, "outputSlot");
		Objects.requireNonNull(predicate, "predicate");
		Objects.requireNonNull(dataFactory, "dataFactory");
		if (!definition.schema().hasItemSlot(inputSlot)) throw new IllegalArgumentException("Cooking input slot '" + inputSlot.name() + "' is not declared by " + definition.id());
		if (!definition.schema().hasItemSlot(outputSlot)) throw new IllegalArgumentException("Cooking output slot '" + outputSlot.name() + "' is not declared by " + definition.id());

		return new CookingSource<>(definition, type, inputSlot, outputSlot, predicate, dataFactory);
	}

	private static final class CookingSource<D, T extends AbstractCookingRecipe> implements MachineRecipeSource {

		private final MachineRecipeDefinition<D>             definition;
		private final RecipeType<T>                          type;
		private final MachineRecipeSlot.ItemInput            inputSlot;
		private final MachineRecipeSlot.ItemOutput           outputSlot;
		private final Predicate<MachineRecipeInput>          predicate;
		private final Function<? super T, ? extends D>       dataFactory;

		private @Nullable Object                              cachedRecipeMap;
		private           ItemStack                          cachedInput   = ItemStack.EMPTY;
		private           List<RecipeHolder<MachineRecipe>> cachedResult  = List.of();

		private CookingSource (MachineRecipeDefinition<D> definition, RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate, Function<? super T, ? extends D> dataFactory) {
			this.definition = definition;
			this.type = type;
			this.inputSlot = inputSlot;
			this.outputSlot = outputSlot;
			this.predicate = predicate;
			this.dataFactory = dataFactory;
		}

		@Override
		public List<RecipeHolder<MachineRecipe>> findMatching (MachineRecipeInput input, ServerLevel level) {
			if (input.definition() != definition || !predicate.test(input)) return List.of();
			ItemStack inputStack = input.item(inputSlot);
			if (inputStack.isEmpty()) return List.of();

			Object recipeMap = level.recipeAccess().recipeMap();
			if (recipeMap == cachedRecipeMap && ItemStack.matches(inputStack, cachedInput)) return cachedResult;

			var nativeInput = new SingleRecipeInput(inputStack);
			var nativeHolder = level.recipeAccess().getRecipeFor(type, nativeInput, level).orElse(null);
			if (nativeHolder == null) {
				cache(recipeMap, inputStack, List.of());
				return List.of();
			}

			T recipe = nativeHolder.value();
			ItemStack result = recipe.assemble(nativeInput);
			if (result.isEmpty()) {
				cache(recipeMap, inputStack, List.of());
				return List.of();
			}

			MachineRecipe adapted = definition.recipeBuilder()
					.itemInput(inputSlot, recipe.input(), 1)
					.itemOutput(outputSlot, result)
					.duration(recipe.cookingTime())
					.data(Objects.requireNonNull(dataFactory.apply(recipe), "Cooking recipe data factory returned null"))
					.build();
			List<RecipeHolder<MachineRecipe>> adaptedResult = List.of(new RecipeHolder<>(nativeHolder.id(), adapted));
			cache(recipeMap, inputStack, adaptedResult);
			return adaptedResult;
		}

		private void cache (Object recipeMap, ItemStack input, List<RecipeHolder<MachineRecipe>> result) {
			cachedRecipeMap = recipeMap;
			cachedInput = input.copy();
			cachedResult = result;
		}
	}
}
