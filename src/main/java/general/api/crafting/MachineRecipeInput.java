package general.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Point-in-time resource view passed to a {@link MachineRecipe} during lookup.
 *
 * <p>The snapshot contains only logical input slots. Stack values are copied
 * while capturing the bound handlers, so recipe predicates cannot mutate the
 * machine's storage or observe half of a later transaction.</p>
 */
public final class MachineRecipeInput implements RecipeInput {

	private final MachineRecipeDefinition<?> definition;
	private final List<String>               itemOrder;
	private final Map<String, ItemStack>     items;
	private final Map<String, FluidStack>    fluids;

	MachineRecipeInput (MachineRecipeDefinition<?> definition, Map<String, ItemStack> items, Map<String, FluidStack> fluids) {
		this.definition = Objects.requireNonNull(definition, "definition");
		this.itemOrder = definition.schema().itemInputs().stream().map(MachineRecipeSchema.Slot::name).toList();
		this.items = copyItems(items);
		this.fluids = copyFluids(fluids);
	}

	MachineRecipeDefinition<?> definition () {
		return definition;
	}

	@Override
	public @NonNull ItemStack getItem (int index) {
		if (index < 0 || index >= itemOrder.size()) {
			throw new IndexOutOfBoundsException("Machine recipe item input index " + index + " is outside [0, " + itemOrder.size() + ")");
		}
		return items.get(itemOrder.get(index));
	}

	@Override
	public int size () {
		return itemOrder.size();
	}

	/**
	 * Returns the captured stack for a logical item input. The returned value must
	 * be treated as read-only, matching vanilla {@link RecipeInput} conventions.
	 */
	public ItemStack item (String name) {
		ItemStack stack = items.get(Objects.requireNonNull(name, "name"));
		if (stack == null) throw new IllegalArgumentException("Unknown machine recipe item input '" + name + "'");
		return stack;
	}

	public ItemStack item (MachineRecipeSlot.ItemInput slot) {
		return item(Objects.requireNonNull(slot, "slot").name());
	}

	/**
	 * Returns the captured stack for a logical fluid input. The returned value must
	 * be treated as read-only.
	 */
	public FluidStack fluid (String name) {
		FluidStack stack = fluids.get(Objects.requireNonNull(name, "name"));
		if (stack == null) throw new IllegalArgumentException("Unknown machine recipe fluid input '" + name + "'");
		return stack;
	}

	public FluidStack fluid (MachineRecipeSlot.FluidInput slot) {
		return fluid(Objects.requireNonNull(slot, "slot").name());
	}

	private static Map<String, ItemStack> copyItems (Map<String, ItemStack> source) {
		Objects.requireNonNull(source, "items");
		var result = new LinkedHashMap<String, ItemStack>(source.size());
		source.forEach((name, stack) -> result.put(Objects.requireNonNull(name, "item input name"), Objects.requireNonNull(stack, "item input stack").copy()));
		return Map.copyOf(result);
	}

	private static Map<String, FluidStack> copyFluids (Map<String, FluidStack> source) {
		Objects.requireNonNull(source, "fluids");
		var result = new LinkedHashMap<String, FluidStack>(source.size());
		source.forEach((name, stack) -> result.put(Objects.requireNonNull(name, "fluid input name"), Objects.requireNonNull(stack, "fluid input stack").copy()));
		return Map.copyOf(result);
	}
}
