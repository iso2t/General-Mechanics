package general.api.crafting;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent builder for code-created recipes and data generation. Final validation
 * is performed by the registered definition, so unknown, duplicate, missing,
 * empty, and wrong-category slots fail with contextual messages.
 */
public final class MachineRecipeBuilder<D> {

	private final MachineRecipeDefinition<D>        definition;
	private final Map<String, SizedIngredient>      itemInputs   = new LinkedHashMap<>();
	private final Map<String, ItemStackTemplate>    itemOutputs  = new LinkedHashMap<>();
	private final Map<String, SizedFluidIngredient> fluidInputs  = new LinkedHashMap<>();
	private final Map<String, FluidStackTemplate>   fluidOutputs = new LinkedHashMap<>();
	private       int                               duration;
	private       D                                 data;

	MachineRecipeBuilder (MachineRecipeDefinition<D> definition, D defaultData) {
		this.definition = Objects.requireNonNull(definition, "definition");
		this.data = defaultData;
	}

	public MachineRecipeBuilder<D> itemInput (String slot, SizedIngredient ingredient) {
		putUnique(itemInputs, slot, Objects.requireNonNull(ingredient, "ingredient"), "item input");
		return this;
	}

	public MachineRecipeBuilder<D> itemInput (String slot, Ingredient ingredient, int count) {
		return itemInput(slot, new SizedIngredient(Objects.requireNonNull(ingredient, "ingredient"), count));
	}

	public MachineRecipeBuilder<D> itemInput (String slot, ItemLike item, int count) {
		return itemInput(slot, SizedIngredient.of(Objects.requireNonNull(item, "item"), count));
	}

	public MachineRecipeBuilder<D> itemInput (String slot, HolderSet<Item> items, int count) {
		return itemInput(slot, Ingredient.of(Objects.requireNonNull(items, "items")), count);
	}

	/**
	 * Resolves an item tag against the registry lookup used by the current recipe provider.
	 * A tag key alone cannot construct an ingredient in Minecraft 26.1 because it does not
	 * contain the tag's holder set.
	 */
	public MachineRecipeBuilder<D> itemInput (String slot, TagKey<Item> tag, HolderGetter<Item> items, int count) {
		Objects.requireNonNull(tag, "tag");
		return itemInput(slot, Objects.requireNonNull(items, "items").getOrThrow(tag), count);
	}

	public MachineRecipeBuilder<D> itemOutput (String slot, ItemStackTemplate template) {
		putUnique(itemOutputs, slot, Objects.requireNonNull(template, "template"), "item output");
		return this;
	}

	public MachineRecipeBuilder<D> itemOutput (String slot, ItemStack stack) {
		return itemOutput(slot, ItemStackTemplate.fromNonEmptyStack(Objects.requireNonNull(stack, "stack")));
	}

	public MachineRecipeBuilder<D> itemOutput (String slot, ItemLike item, int count) {
		return itemOutput(slot, new ItemStackTemplate(Objects.requireNonNull(item, "item").asItem(), count));
	}

	public MachineRecipeBuilder<D> fluidInput (String slot, SizedFluidIngredient ingredient) {
		putUnique(fluidInputs, slot, Objects.requireNonNull(ingredient, "ingredient"), "fluid input");
		return this;
	}

	public MachineRecipeBuilder<D> fluidInput (String slot, FluidIngredient ingredient, int amount) {
		return fluidInput(slot, new SizedFluidIngredient(Objects.requireNonNull(ingredient, "ingredient"), amount));
	}

	public MachineRecipeBuilder<D> fluidInput (String slot, Fluid fluid, int amount) {
		return fluidInput(slot, SizedFluidIngredient.of(Objects.requireNonNull(fluid, "fluid"), amount));
	}

	public MachineRecipeBuilder<D> fluidInput (String slot, HolderSet<Fluid> fluids, int amount) {
		return fluidInput(slot, FluidIngredient.of(Objects.requireNonNull(fluids, "fluids")), amount);
	}

	/**
	 * Fluid equivalent of {@link #itemInput(String, TagKey, HolderGetter, int)}.
	 */
	public MachineRecipeBuilder<D> fluidInput (String slot, TagKey<Fluid> tag, HolderGetter<Fluid> fluids, int amount) {
		Objects.requireNonNull(tag, "tag");
		return fluidInput(slot, Objects.requireNonNull(fluids, "fluids").getOrThrow(tag), amount);
	}

	public MachineRecipeBuilder<D> fluidOutput (String slot, FluidStackTemplate template) {
		putUnique(fluidOutputs, slot, Objects.requireNonNull(template, "template"), "fluid output");
		return this;
	}

	public MachineRecipeBuilder<D> fluidOutput (String slot, FluidStack stack) {
		return fluidOutput(slot, FluidStackTemplate.fromNonEmptyStack(Objects.requireNonNull(stack, "stack")));
	}

	public MachineRecipeBuilder<D> fluidOutput (String slot, Fluid fluid, int amount) {
		return fluidOutput(slot, new FluidStackTemplate(Objects.requireNonNull(fluid, "fluid"), amount));
	}

	public MachineRecipeBuilder<D> duration (int ticks) {
		if (ticks <= 0) throw new IllegalArgumentException("Machine recipe duration must be positive: " + ticks);
		this.duration = ticks;
		return this;
	}

	public MachineRecipeBuilder<D> data (D data) {
		this.data = Objects.requireNonNull(data, "data");
		return this;
	}

	public MachineRecipe build () {
		return definition.create(itemInputs, itemOutputs, fluidInputs, fluidOutputs, duration, data);
	}

	public void save (RecipeOutput output, Identifier id) {
		save(output, ResourceKey.create(Registries.RECIPE, Objects.requireNonNull(id, "id")), null);
	}

	public void save (RecipeOutput output, ResourceKey<Recipe<?>> key) {
		save(output, key, null);
	}

	public void save (RecipeOutput output, ResourceKey<Recipe<?>> key, @Nullable AdvancementHolder advancement) {
		Objects.requireNonNull(output, "output").accept(Objects.requireNonNull(key, "key"), build(), advancement);
	}

	private static <V> void putUnique (Map<String, V> values, String slot, V value, String description) {
		Objects.requireNonNull(slot, "slot");
		if (slot.isBlank()) throw new IllegalArgumentException("Machine recipe " + description + " slot name must not be blank");
		if (values.putIfAbsent(slot, value) != null) throw new IllegalArgumentException("Duplicate machine recipe " + description + " slot '" + slot + "'");
	}
}
