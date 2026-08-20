package general.api.crafting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable declaration of the logical resource slots used by a machine recipe type.
 *
 * <p>Logical recipe slots are deliberately separate from physical handler slots. A
 * {@link MachineRecipeBinding} normally maps matching names automatically, while its
 * builder can map the same recipe type onto a differently named machine layout. This
 * lets recipe JSON remain stable when a machine's physical slot order changes.</p>
 */
public final class MachineRecipeSchema {

	private final List<Slot> itemInputs;
	private final List<Slot> itemOutputs;
	private final List<Slot> fluidInputs;
	private final List<Slot> fluidOutputs;

	private MachineRecipeSchema (Builder builder) {
		this.itemInputs = validateGroup("item input", builder.itemInputs);
		this.itemOutputs = validateGroup("item output", builder.itemOutputs);
		this.fluidInputs = validateGroup("fluid input", builder.fluidInputs);
		this.fluidOutputs = validateGroup("fluid output", builder.fluidOutputs);
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

	public boolean hasFluidSlot (String name) {
		return contains(fluidInputs, name) || contains(fluidOutputs, name);
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

	/**
	 * One logical field in a recipe. Required slots must be present in every
	 * recipe; optional slots may be omitted from that recipe's serialized maps.
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

		private final List<Slot> itemInputs  = new ArrayList<>();
		private final List<Slot> itemOutputs = new ArrayList<>();
		private final List<Slot> fluidInputs = new ArrayList<>();
		private final List<Slot> fluidOutputs = new ArrayList<>();

		public Builder itemInput (String name) {
			return itemInput(name, true);
		}

		public Builder optionalItemInput (String name) {
			return itemInput(name, false);
		}

		public Builder itemInput (String name, boolean required) {
			itemInputs.add(new Slot(name, required));
			return this;
		}

		public Builder itemOutput (String name) {
			return itemOutput(name, true);
		}

		public Builder optionalItemOutput (String name) {
			return itemOutput(name, false);
		}

		public Builder itemOutput (String name, boolean required) {
			itemOutputs.add(new Slot(name, required));
			return this;
		}

		public Builder fluidInput (String name) {
			return fluidInput(name, true);
		}

		public Builder optionalFluidInput (String name) {
			return fluidInput(name, false);
		}

		public Builder fluidInput (String name, boolean required) {
			fluidInputs.add(new Slot(name, required));
			return this;
		}

		public Builder fluidOutput (String name) {
			return fluidOutput(name, true);
		}

		public Builder optionalFluidOutput (String name) {
			return fluidOutput(name, false);
		}

		public Builder fluidOutput (String name, boolean required) {
			fluidOutputs.add(new Slot(name, required));
			return this;
		}

		public MachineRecipeSchema build () {
			return new MachineRecipeSchema(this);
		}
	}
}
