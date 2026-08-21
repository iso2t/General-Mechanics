package general.api.crafting;

import general.api.transfer.ResourceSlotKey;

/**
 * Typed key for one logical field in a machine recipe.
 *
 * <p>The resource kind and recipe direction are part of the Java type, keeping
 * an item input from accidentally being used as a fluid output while retaining
 * a stable serialized {@link #name()}.</p>
 */
public sealed interface MachineRecipeSlot extends ResourceSlotKey permits MachineRecipeSlot.Item, MachineRecipeSlot.Fluid {

	sealed interface Item extends MachineRecipeSlot permits ItemInput, ItemOutput {
	}

	sealed interface Fluid extends MachineRecipeSlot permits FluidInput, FluidOutput {
	}

	record ItemInput(String name) implements Item {

		public ItemInput {
			name = MachineRecipeSlots.requireName(name);
		}
	}

	record ItemOutput(String name) implements Item {

		public ItemOutput {
			name = MachineRecipeSlots.requireName(name);
		}
	}

	record FluidInput(String name) implements Fluid {

		public FluidInput {
			name = MachineRecipeSlots.requireName(name);
		}
	}

	record FluidOutput(String name) implements Fluid {

		public FluidOutput {
			name = MachineRecipeSlots.requireName(name);
		}
	}
}
