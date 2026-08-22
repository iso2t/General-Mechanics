package general.api.crafting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
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
		return cookingSpec(definition, type, inputSlot, outputSlot, predicate, dataFactory).create();
	}

	static <D, T extends AbstractCookingRecipe> SourceSpec cookingSpec (MachineRecipeDefinition<D> definition, RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate, Function<? super T, ? extends D> dataFactory) {
		Objects.requireNonNull(definition, "definition");
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(inputSlot, "inputSlot");
		Objects.requireNonNull(outputSlot, "outputSlot");
		Objects.requireNonNull(predicate, "predicate");
		Objects.requireNonNull(dataFactory, "dataFactory");
		if (!definition.schema().hasItemSlot(inputSlot)) throw new IllegalArgumentException("Cooking input slot '" + inputSlot.name() + "' is not declared by " + definition.id());
		if (!definition.schema().hasItemSlot(outputSlot)) throw new IllegalArgumentException("Cooking output slot '" + outputSlot.name() + "' is not declared by " + definition.id());

		return new CookingSpec<>(definition, type, inputSlot, outputSlot, predicate, dataFactory);
	}

	interface SourceSpec {

		MachineRecipeSource create ();

		RecipeType<?> nativeType ();

		List<RecipeHolder<MachineRecipe>> recipes (RecipeMap recipes);
	}

	private static final class CookingSpec<D, T extends AbstractCookingRecipe> implements SourceSpec {

		private final MachineRecipeDefinition<D>       definition;
		private final RecipeType<T>                    type;
		private final MachineRecipeSlot.ItemInput      inputSlot;
		private final MachineRecipeSlot.ItemOutput     outputSlot;
		private final Predicate<MachineRecipeInput>    predicate;
		private final Function<? super T, ? extends D> dataFactory;

		private CookingSpec (MachineRecipeDefinition<D> definition, RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate, Function<? super T, ? extends D> dataFactory) {
			this.definition = definition;
			this.type = type;
			this.inputSlot = inputSlot;
			this.outputSlot = outputSlot;
			this.predicate = predicate;
			this.dataFactory = dataFactory;
		}

		@Override
		public MachineRecipeSource create () {
			return new CookingSource<>(this);
		}

		@Override
		public RecipeType<?> nativeType () {
			return type;
		}

		@Override
		public List<RecipeHolder<MachineRecipe>> recipes (RecipeMap recipes) {
			var result = new ArrayList<RecipeHolder<MachineRecipe>>();
			for (RecipeHolder<T> holder : recipes.byType(type)) {
				ItemStack sampleInput = holder.value().input().items().findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
				RecipeHolder<MachineRecipe> adapted = sampleInput.isEmpty() ? null : adapt(holder, sampleInput);
				if (adapted != null) result.add(adapted);
			}
			return List.copyOf(result);
		}

		private @Nullable RecipeHolder<MachineRecipe> adapt (RecipeHolder<T> holder, ItemStack input) {
			T recipe = holder.value();
			ItemStack result = recipe.assemble(new SingleRecipeInput(input));
			if (result.isEmpty()) return null;

			MachineRecipe adapted = definition.recipeBuilder().itemInput(inputSlot, recipe.input(), 1).itemOutput(outputSlot, result).duration(recipe.cookingTime()).data(Objects.requireNonNull(dataFactory.apply(recipe), "Cooking recipe data factory returned null")).build();
			return new RecipeHolder<>(holder.id(), adapted);
		}
	}

	private static final class CookingSource<D, T extends AbstractCookingRecipe> implements MachineRecipeSource {

		private final CookingSpec<D, T> spec;

		private @Nullable Object                            cachedRecipeMap;
		private           ItemStack                         cachedInput  = ItemStack.EMPTY;
		private           List<RecipeHolder<MachineRecipe>> cachedResult = List.of();

		private CookingSource (CookingSpec<D, T> spec) {
			this.spec = spec;
		}

		@Override
		public List<RecipeHolder<MachineRecipe>> findMatching (MachineRecipeInput input, ServerLevel level) {
			if (input.definition() != spec.definition || !spec.predicate.test(input)) return List.of();
			ItemStack inputStack = input.item(spec.inputSlot);
			if (inputStack.isEmpty()) return List.of();

			Object recipeMap = level.recipeAccess().recipeMap();
			if (recipeMap == cachedRecipeMap && ItemStack.matches(inputStack, cachedInput)) return cachedResult;

			var nativeInput = new SingleRecipeInput(inputStack);
			var nativeHolder = level.recipeAccess().getRecipeFor(spec.type, nativeInput, level).orElse(null);
			RecipeHolder<MachineRecipe> adapted = nativeHolder == null ? null : spec.adapt(nativeHolder, inputStack);
			List<RecipeHolder<MachineRecipe>> result = adapted == null ? List.of() : List.of(adapted);
			cache(recipeMap, inputStack, result);
			return result;
		}

		private void cache (Object recipeMap, ItemStack input, List<RecipeHolder<MachineRecipe>> result) {
			cachedRecipeMap = recipeMap;
			cachedInput = input.copy();
			cachedResult = result;
		}
	}
}
