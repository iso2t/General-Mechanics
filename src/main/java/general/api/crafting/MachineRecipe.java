package general.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Data-driven item/fluid machine recipe backed by a registered
 * {@link MachineRecipeDefinition}.
 *
 * <p>Vanilla's {@link #assemble(MachineRecipeInput)} method can expose only one
 * item stack. It therefore returns the first declared item output as a
 * compatibility view. Machines must use {@link MachineRecipeBinding}, which
 * handles every declared item and fluid result in one transaction.</p>
 */
public final class MachineRecipe implements Recipe<MachineRecipeInput> {

	private final MachineRecipeDefinition<?>             definition;
	private final Map<String, SizedIngredient>           itemInputs;
	private final Map<String, ItemStackTemplate>         itemOutputs;
	private final Map<String, SizedFluidIngredient>      fluidInputs;
	private final Map<String, FluidStackTemplate>        fluidOutputs;
	private final int                                    duration;
	private final Object                                 data;

	MachineRecipe (MachineRecipeDefinition<?> definition, Map<String, SizedIngredient> itemInputs, Map<String, ItemStackTemplate> itemOutputs, Map<String, SizedFluidIngredient> fluidInputs, Map<String, FluidStackTemplate> fluidOutputs, int duration, Object data) {
		this.definition = Objects.requireNonNull(definition, "definition");
		this.itemInputs = Map.copyOf(new LinkedHashMap<>(Objects.requireNonNull(itemInputs, "itemInputs")));
		this.itemOutputs = copyTemplates(itemOutputs, "itemOutputs");
		this.fluidInputs = Map.copyOf(new LinkedHashMap<>(Objects.requireNonNull(fluidInputs, "fluidInputs")));
		this.fluidOutputs = copyTemplates(fluidOutputs, "fluidOutputs");
		this.duration = duration;
		this.data = Objects.requireNonNull(data, "data");
	}

	public MachineRecipeDefinition<?> definition () {
		return definition;
	}

	public Map<String, SizedIngredient> itemInputs () {
		return itemInputs;
	}

	/**
	 * Defensive copies of all named item results.
	 */
	public Map<String, ItemStack> itemOutputs () {
		var result = new LinkedHashMap<String, ItemStack>(itemOutputs.size());
		itemOutputs.forEach((name, template) -> result.put(name, template.create()));
		return Map.copyOf(result);
	}

	public Map<String, SizedFluidIngredient> fluidInputs () {
		return fluidInputs;
	}

	/**
	 * Defensive copies of all named fluid results.
	 */
	public Map<String, FluidStack> fluidOutputs () {
		var result = new LinkedHashMap<String, FluidStack>(fluidOutputs.size());
		fluidOutputs.forEach((name, template) -> result.put(name, template.create()));
		return Map.copyOf(result);
	}

	public int duration () {
		return duration;
	}

	Object dataValue () {
		return data;
	}

	ItemStack internalItemOutput (String name) {
		ItemStackTemplate template = itemOutputs.get(name);
		return template == null ? null : template.create();
	}

	FluidStack internalFluidOutput (String name) {
		FluidStackTemplate template = fluidOutputs.get(name);
		return template == null ? null : template.create();
	}

	ItemStackTemplate internalItemOutputTemplate (String name) {
		return itemOutputs.get(name);
	}

	FluidStackTemplate internalFluidOutputTemplate (String name) {
		return fluidOutputs.get(name);
	}

	@Override
	public boolean matches (MachineRecipeInput input, Level level) {
		if (input.definition() != definition) return false;
		for (var entry : itemInputs.entrySet()) {
			if (!entry.getValue().test(input.item(entry.getKey()))) return false;
		}
		for (var entry : fluidInputs.entrySet()) {
			if (!entry.getValue().test(input.fluid(entry.getKey()))) return false;
		}
		return definition.matchesAdditional(this, input, level);
	}

	@Override
	public @NonNull ItemStack assemble (@NonNull MachineRecipeInput input) {
		for (MachineRecipeSchema.Slot slot : definition.schema().itemOutputs()) {
			ItemStackTemplate output = itemOutputs.get(slot.name());
			if (output != null) return output.create();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial () {
		return true;
	}

	@Override
	public boolean showNotification () {
		return false;
	}

	@Override
	public @NonNull String group () {
		return "";
	}

	@Override
	public @NonNull RecipeSerializer<MachineRecipe> getSerializer () {
		return definition.serializer();
	}

	@Override
	public @NonNull RecipeType<MachineRecipe> getType () {
		return definition.type();
	}

	@Override
	public @NonNull PlacementInfo placementInfo () {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public @NonNull RecipeBookCategory recipeBookCategory () {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	private static <T> Map<String, T> copyTemplates (Map<String, T> source, String description) {
		Objects.requireNonNull(source, description);
		var result = new LinkedHashMap<String, T>(source.size());
		source.forEach((name, template) -> result.put(Objects.requireNonNull(name, description + " name"), Objects.requireNonNull(template, description + " template")));
		return Map.copyOf(result);
	}
}
