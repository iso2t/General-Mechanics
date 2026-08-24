package general.api.crafting;

import java.util.*;

/**
 * Immutable declaration of the logical resource slots used by a machine recipe type.
 *
 * <p>Logical recipe slots are deliberately separate from physical handler slots. A
 * {@link MachineRecipeBinding} normally maps matching names automatically, while its
 * builder can map the same recipe type onto a differently named machine layout. This
 * lets recipe JSON remain stable when a machine's physical slot order changes.</p>
 */
public final class MachineRecipeSchema {

	private final List<Slot>  itemInputs;
	private final List<Slot>  itemOutputs;
	private final List<Slot>  fluidInputs;
	private final List<Slot>  fluidOutputs;
	private final Set<String> retainedItemInputs;
	private final Set<String> retainedFluidInputs;

	private MachineRecipeSchema (Builder builder) {
		this.itemInputs = validateGroup("item input", builder.itemInputs);
		this.itemOutputs = validateGroup("item output", builder.itemOutputs);
		this.fluidInputs = validateGroup("fluid input", builder.fluidInputs);
		this.fluidOutputs = validateGroup("fluid output", builder.fluidOutputs);
		this.retainedItemInputs = validateRetainedInputs("item", itemInputs, builder.retainedItemInputs);
		this.retainedFluidInputs = validateRetainedInputs("fluid", fluidInputs, builder.retainedFluidInputs);
		if (itemInputs.isEmpty() && itemOutputs.isEmpty() && fluidInputs.isEmpty() && fluidOutputs.isEmpty()) {
			throw new IllegalArgumentException("A machine recipe schema must declare at least one resource slot");
		}
	}

	public static Builder builder () {
		return new Builder();
	}

	public List<Slot> itemInputs () {
		return itemInputs;
	}

	public List<Slot> itemOutputs () {
		return itemOutputs;
	}

	public List<Slot> fluidInputs () {
		return fluidInputs;
	}

	public List<Slot> fluidOutputs () {
		return fluidOutputs;
	}

	/**
	 * Whether a matching item ingredient is removed when the recipe completes.
	 * Retained inputs are useful for dies, molds, and other reusable recipe tools.
	 */
	public boolean consumesItemInput (String name) {
		Objects.requireNonNull(name, "name");
		if (!contains(itemInputs, name)) throw new IllegalArgumentException("Unknown item input slot '" + name + "'");
		return !retainedItemInputs.contains(name);
	}

	public boolean consumesItemInput (MachineRecipeSlot.ItemInput slot) {
		return consumesItemInput(Objects.requireNonNull(slot, "slot").name());
	}

	/**
	 * Fluid equivalent of {@link #consumesItemInput(String)}.
	 */
	public boolean consumesFluidInput (String name) {
		Objects.requireNonNull(name, "name");
		if (!contains(fluidInputs, name)) throw new IllegalArgumentException("Unknown fluid input slot '" + name + "'");
		return !retainedFluidInputs.contains(name);
	}

	public boolean consumesFluidInput (MachineRecipeSlot.FluidInput slot) {
		return consumesFluidInput(Objects.requireNonNull(slot, "slot").name());
	}

	/**
	 * Unique item slot names in declaration order, inputs first.
	 */
	public List<String> itemSlots () {
		return combinedNames(itemInputs, itemOutputs);
	}

	/**
	 * Unique fluid slot names in declaration order, inputs first.
	 */
	public List<String> fluidSlots () {
		return combinedNames(fluidInputs, fluidOutputs);
	}

	public boolean hasItemSlot (String name) {
		return contains(itemInputs, name) || contains(itemOutputs, name);
	}

	public boolean hasItemSlot (MachineRecipeSlot.Item slot) {
		return hasItemSlot(Objects.requireNonNull(slot, "slot").name());
	}

	public boolean hasFluidSlot (String name) {
		return contains(fluidInputs, name) || contains(fluidOutputs, name);
	}

	public boolean hasFluidSlot (MachineRecipeSlot.Fluid slot) {
		return hasFluidSlot(Objects.requireNonNull(slot, "slot").name());
	}

	private static boolean contains (List<Slot> slots, String name) {
		Objects.requireNonNull(name, "name");
		for (Slot slot : slots) {
			if (slot.name().equals(name)) return true;
		}
		return false;
	}

	private static List<String> combinedNames (List<Slot> first, List<Slot> second) {
		Set<String> names = new LinkedHashSet<>();
		for (Slot slot : first) names.add(slot.name());
		for (Slot slot : second) names.add(slot.name());
		return List.copyOf(names);
	}

	private static List<Slot> validateGroup (String description, List<Slot> source) {
		var slots = List.copyOf(source);
		var names = new HashSet<String>();
		for (Slot slot : slots) {
			if (!names.add(slot.name())) {
				throw new IllegalArgumentException("Duplicate machine recipe " + description + " slot '" + slot.name() + "'");
			}
		}
		return slots;
	}

	private static Set<String> validateRetainedInputs (String resourceName, List<Slot> inputs, Set<String> source) {
		Set<String> retained = Set.copyOf(source);
		for (String name : retained) {
			if (!contains(inputs, name)) throw new IllegalArgumentException("Retained " + resourceName + " input '" + name + "' is not declared by the schema");
		}
		return retained;
	}

	/**
	 * One logical field in a recipe. Required slots must be present in every
	 * recipe; optional slots may be omitted from that recipe's serialized maps.
	 * An omitted optional input only matches when its bound machine slot is empty.
	 */
	public record Slot(String name, boolean required) {

		public Slot {
			Objects.requireNonNull(name, "name");
			if (name.isBlank()) throw new IllegalArgumentException("Machine recipe slot name must not be blank");
		}
	}

	/**
	 * Declarative schema builder. Methods without an {@code optional} prefix add
	 * required fields, making missing recipe data a load-time error.
	 */
	public static final class Builder {

		private final List<Slot>  itemInputs          = new ArrayList<>();
		private final List<Slot>  itemOutputs         = new ArrayList<>();
		private final List<Slot>  fluidInputs         = new ArrayList<>();
		private final List<Slot>  fluidOutputs        = new ArrayList<>();
		private final Set<String> retainedItemInputs  = new LinkedHashSet<>();
		private final Set<String> retainedFluidInputs = new LinkedHashSet<>();

		public Builder itemInput (String name) {
			return itemInput(name, true);
		}

		public Builder itemInput (MachineRecipeSlot.ItemInput slot) {
			return itemInput(Objects.requireNonNull(slot, "slot").name());
		}

		/**
		 * Adds the requested number of standard {@code item_input} slots.
		 */
		public Builder itemInputs (int count) {
			MachineRecipeSlots.itemInputs(count).forEach(this::itemInput);
			return this;
		}

		public Builder optionalItemInput (String name) {
			return itemInput(name, false);
		}

		public Builder optionalItemInput (MachineRecipeSlot.ItemInput slot) {
			return itemInput(Objects.requireNonNull(slot, "slot").name(), false);
		}

		/**
		 * Adds a required reusable item input. The ingredient must match but is not
		 * extracted when processing completes.
		 */
		public Builder itemCatalyst (String name) {
			return itemCatalyst(name, true);
		}

		public Builder itemCatalyst (MachineRecipeSlot.ItemInput slot) {
			return itemCatalyst(Objects.requireNonNull(slot, "slot").name());
		}

		public Builder optionalItemCatalyst (String name) {
			return itemCatalyst(name, false);
		}

		public Builder optionalItemCatalyst (MachineRecipeSlot.ItemInput slot) {
			return itemCatalyst(Objects.requireNonNull(slot, "slot").name(), false);
		}

		public Builder itemInput (String name, boolean required) {
			itemInputs.add(new Slot(name, required));
			return this;
		}

		public Builder itemInput (MachineRecipeSlot.ItemInput slot, boolean required) {
			return itemInput(Objects.requireNonNull(slot, "slot").name(), required);
		}

		private Builder itemCatalyst (String name, boolean required) {
			itemInput(name, required);
			retainedItemInputs.add(name);
			return this;
		}

		public Builder itemOutput (String name) {
			return itemOutput(name, true);
		}

		public Builder itemOutput (MachineRecipeSlot.ItemOutput slot) {
			return itemOutput(Objects.requireNonNull(slot, "slot").name());
		}

		/**
		 * Adds the requested number of standard {@code item_output} slots.
		 */
		public Builder itemOutputs (int count) {
			MachineRecipeSlots.itemOutputs(count).forEach(this::itemOutput);
			return this;
		}

		public Builder optionalItemOutput (String name) {
			return itemOutput(name, false);
		}

		public Builder optionalItemOutput (MachineRecipeSlot.ItemOutput slot) {
			return itemOutput(Objects.requireNonNull(slot, "slot").name(), false);
		}

		public Builder itemOutput (String name, boolean required) {
			itemOutputs.add(new Slot(name, required));
			return this;
		}

		public Builder itemOutput (MachineRecipeSlot.ItemOutput slot, boolean required) {
			return itemOutput(Objects.requireNonNull(slot, "slot").name(), required);
		}

		public Builder fluidInput (String name) {
			return fluidInput(name, true);
		}

		public Builder fluidInput (MachineRecipeSlot.FluidInput slot) {
			return fluidInput(Objects.requireNonNull(slot, "slot").name());
		}

		/**
		 * Adds the requested number of standard {@code fluid_input} slots.
		 */
		public Builder fluidInputs (int count) {
			MachineRecipeSlots.fluidInputs(count).forEach(this::fluidInput);
			return this;
		}

		public Builder optionalFluidInput (String name) {
			return fluidInput(name, false);
		}

		public Builder optionalFluidInput (MachineRecipeSlot.FluidInput slot) {
			return fluidInput(Objects.requireNonNull(slot, "slot").name(), false);
		}

		public Builder fluidCatalyst (String name) {
			return fluidCatalyst(name, true);
		}

		public Builder fluidCatalyst (MachineRecipeSlot.FluidInput slot) {
			return fluidCatalyst(Objects.requireNonNull(slot, "slot").name());
		}

		public Builder optionalFluidCatalyst (String name) {
			return fluidCatalyst(name, false);
		}

		public Builder optionalFluidCatalyst (MachineRecipeSlot.FluidInput slot) {
			return fluidCatalyst(Objects.requireNonNull(slot, "slot").name(), false);
		}

		public Builder fluidInput (String name, boolean required) {
			fluidInputs.add(new Slot(name, required));
			return this;
		}

		public Builder fluidInput (MachineRecipeSlot.FluidInput slot, boolean required) {
			return fluidInput(Objects.requireNonNull(slot, "slot").name(), required);
		}

		private Builder fluidCatalyst (String name, boolean required) {
			fluidInput(name, required);
			retainedFluidInputs.add(name);
			return this;
		}

		public Builder fluidOutput (String name) {
			return fluidOutput(name, true);
		}

		public Builder fluidOutput (MachineRecipeSlot.FluidOutput slot) {
			return fluidOutput(Objects.requireNonNull(slot, "slot").name());
		}

		/**
		 * Adds the requested number of standard {@code fluid_output} slots.
		 */
		public Builder fluidOutputs (int count) {
			MachineRecipeSlots.fluidOutputs(count).forEach(this::fluidOutput);
			return this;
		}

		public Builder optionalFluidOutput (String name) {
			return fluidOutput(name, false);
		}

		public Builder optionalFluidOutput (MachineRecipeSlot.FluidOutput slot) {
			return fluidOutput(Objects.requireNonNull(slot, "slot").name(), false);
		}

		public Builder fluidOutput (String name, boolean required) {
			fluidOutputs.add(new Slot(name, required));
			return this;
		}

		public Builder fluidOutput (MachineRecipeSlot.FluidOutput slot, boolean required) {
			return fluidOutput(Objects.requireNonNull(slot, "slot").name(), required);
		}

		public MachineRecipeSchema build () {
			return new MachineRecipeSchema(this);
		}
	}
}
