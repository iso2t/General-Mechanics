package general.api.crafting;

import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Standard machine-recipe slot keys and factories for custom layouts.
 *
 * <p>The first numbered slot retains the compact historical name (for example,
 * {@code item_input}); later slots append their one-based number (for example,
 * {@code item_input_2}). This keeps existing recipe JSON compatible while giving
 * every machine type the same convention for multiple resources.</p>
 */
public final class MachineRecipeSlots {

	public static final MachineRecipeSlot.ItemInput   ITEM_INPUT   = itemInput(1);
	public static final MachineRecipeSlot.ItemOutput  ITEM_OUTPUT  = itemOutput(1);
	public static final MachineRecipeSlot.FluidInput  FLUID_INPUT  = fluidInput(1);
	public static final MachineRecipeSlot.FluidOutput FLUID_OUTPUT = fluidOutput(1);

	private MachineRecipeSlots () {
	}

	public static MachineRecipeSlot.ItemInput itemInput (int number) {
		return itemInput(numbered("item_input", number));
	}

	public static MachineRecipeSlot.ItemInput itemInput (String name) {
		return new MachineRecipeSlot.ItemInput(name);
	}

	public static List<MachineRecipeSlot.ItemInput> itemInputs (int count) {
		return numberedSlots(count, MachineRecipeSlots::itemInput);
	}

	public static MachineRecipeSlot.ItemOutput itemOutput (int number) {
		return itemOutput(numbered("item_output", number));
	}

	public static MachineRecipeSlot.ItemOutput itemOutput (String name) {
		return new MachineRecipeSlot.ItemOutput(name);
	}

	public static List<MachineRecipeSlot.ItemOutput> itemOutputs (int count) {
		return numberedSlots(count, MachineRecipeSlots::itemOutput);
	}

	public static MachineRecipeSlot.FluidInput fluidInput (int number) {
		return fluidInput(numbered("fluid_input", number));
	}

	public static MachineRecipeSlot.FluidInput fluidInput (String name) {
		return new MachineRecipeSlot.FluidInput(name);
	}

	public static List<MachineRecipeSlot.FluidInput> fluidInputs (int count) {
		return numberedSlots(count, MachineRecipeSlots::fluidInput);
	}

	public static MachineRecipeSlot.FluidOutput fluidOutput (int number) {
		return fluidOutput(numbered("fluid_output", number));
	}

	public static MachineRecipeSlot.FluidOutput fluidOutput (String name) {
		return new MachineRecipeSlot.FluidOutput(name);
	}

	public static List<MachineRecipeSlot.FluidOutput> fluidOutputs (int count) {
		return numberedSlots(count, MachineRecipeSlots::fluidOutput);
	}

	static String requireName (String name) {
		Objects.requireNonNull(name, "name");
		if (name.isBlank()) throw new IllegalArgumentException("Machine recipe slot name must not be blank");
		return name;
	}

	private static String numbered (String baseName, int number) {
		if (number <= 0) throw new IllegalArgumentException("Machine recipe slot number must be positive: " + number);
		return number == 1 ? baseName : baseName + "_" + number;
	}

	private static <S extends MachineRecipeSlot> List<S> numberedSlots (int count, IntFunction<S> factory) {
		if (count < 0) throw new IllegalArgumentException("Machine recipe slot count must be non-negative: " + count);
		return IntStream.rangeClosed(1, count).mapToObj(factory).toList();
	}
}
