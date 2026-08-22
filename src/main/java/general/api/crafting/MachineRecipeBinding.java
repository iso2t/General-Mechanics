package general.api.crafting;

import general.api.transfer.ResourceInventoryDefinition;
import general.api.transfer.ResourceSlotKey;
import general.api.transfer.VersionedResourceHandler;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.FluidResourceHandler;
import general.api.transfer.item.ItemInventoryDefinition;
import general.api.transfer.item.ItemResourceHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Validated mapping from a logical recipe schema to a machine's physical item
 * and fluid handlers.
 *
 * <p>The binding always uses unrestricted internal handlers, never sided
 * capability views. A completion extracts every input and inserts every output
 * under one NeoForge root transaction. Any partial transfer aborts the entire
 * operation, including changes already made to another handler.</p>
 */
public final class MachineRecipeBinding {

	private final           MachineRecipeDefinition<?>     definition;
	private final @Nullable ResourceHandler<ItemResource>  items;
	private final @Nullable ResourceHandler<FluidResource> fluids;
	private final           Map<String, Integer>           itemSlots;
	private final           Map<String, Integer>           fluidSlots;
	private final @Nullable VersionedResourceHandler<?>    itemRevisionSource;
	private final @Nullable VersionedResourceHandler<?>    fluidRevisionSource;
	private final           boolean                        tracksContentRevisions;

	private MachineRecipeBinding (Builder builder) {
		this.definition = builder.definition;
		this.items = builder.items;
		this.fluids = builder.fluids;
		this.itemSlots = resolve("item", definition.schema().itemSlots(), builder.itemMappings, builder.itemDefinition, items);
		this.fluidSlots = resolve("fluid", definition.schema().fluidSlots(), builder.fluidMappings, builder.fluidDefinition, fluids);
		this.itemRevisionSource = items instanceof VersionedResourceHandler<?> handler ? handler : null;
		this.fluidRevisionSource = fluids instanceof VersionedResourceHandler<?> handler ? handler : null;
		this.tracksContentRevisions = (items == null || itemRevisionSource != null) && (fluids == null || fluidRevisionSource != null);
	}

	public MachineRecipeDefinition<?> definition () {
		return definition;
	}

	public int itemSlotIndex (String logicalName) {
		Integer index = itemSlots.get(Objects.requireNonNull(logicalName, "logicalName"));
		if (index == null) throw new IllegalArgumentException("Unknown bound item recipe slot '" + logicalName + "'");
		return index;
	}

	public int itemSlotIndex (MachineRecipeSlot.Item slot) {
		return itemSlotIndex(Objects.requireNonNull(slot, "slot").name());
	}

	public int fluidSlotIndex (String logicalName) {
		Integer index = fluidSlots.get(Objects.requireNonNull(logicalName, "logicalName"));
		if (index == null) throw new IllegalArgumentException("Unknown bound fluid recipe slot '" + logicalName + "'");
		return index;
	}

	public int fluidSlotIndex (MachineRecipeSlot.Fluid slot) {
		return fluidSlotIndex(Objects.requireNonNull(slot, "slot").name());
	}

	/**
	 * Captures only the physical locations declared as recipe inputs.
	 */
	public MachineRecipeInput captureInput () {
		var itemInputs = new LinkedHashMap<String, ItemStack>();
		if (items != null) {
			for (MachineRecipeSchema.Slot slot : definition.schema().itemInputs()) {
				int index = itemSlots.get(slot.name());
				itemInputs.put(slot.name(), items.getResource(index).toStack(items.getAmountAsInt(index)));
			}
		}

		var fluidInputs = new LinkedHashMap<String, FluidStack>();
		if (fluids != null) {
			for (MachineRecipeSchema.Slot slot : definition.schema().fluidInputs()) {
				int index = fluidSlots.get(slot.name());
				fluidInputs.put(slot.name(), fluids.getResource(index).toStack(fluids.getAmountAsInt(index)));
			}
		}
		return new MachineRecipeInput(definition, itemInputs, fluidInputs);
	}

	public boolean matches (MachineRecipe recipe, Level level) {
		return matches(recipe, captureInput(), level);
	}

	/**
	 * Tests a recipe against an input snapshot already captured this tick.
	 */
	public boolean matches (MachineRecipe recipe, MachineRecipeInput input, Level level) {
		Objects.requireNonNull(recipe, "recipe");
		Objects.requireNonNull(input, "input");
		Objects.requireNonNull(level, "level");
		return recipe.definition() == definition && recipe.matches(input, level);
	}

	/**
	 * Simulates the complete transaction. This accounts for output space made by
	 * consumed inputs when input and output mappings share a physical slot.
	 */
	public boolean canExecute (MachineRecipe recipe, Level level) {
		return canExecute(recipe, captureInput(), level);
	}

	/**
	 * Simulates execution using an input snapshot already captured this tick.
	 */
	public boolean canExecute (MachineRecipe recipe, MachineRecipeInput input, Level level) {
		return matches(recipe, input, level) && canTransfer(recipe);
	}

	/**
	 * Attempts one atomic recipe completion.
	 *
	 * @return {@code true} only when all inputs were consumed and all outputs were
	 * committed
	 */
	public boolean tryExecute (MachineRecipe recipe, Level level) {
		return tryExecute(recipe, captureInput(), level);
	}

	/**
	 * Attempts execution using an input snapshot already captured this tick.
	 */
	public boolean tryExecute (MachineRecipe recipe, MachineRecipeInput input, Level level) {
		if (!matches(recipe, input, level)) return false;
		try (Transaction transaction = Transaction.openRoot()) {
			if (!transfer(recipe, transaction)) return false;
			transaction.commit();
			return true;
		}
	}

	/**
	 * Simulates only resource transfer. The caller must separately establish that
	 * the recipe matches the current input and any level-dependent conditions.
	 */
	boolean canTransfer (MachineRecipe recipe) {
		requireRecipe(recipe);
		try (Transaction transaction = Transaction.openRoot()) {
			return transfer(recipe, transaction);
		}
	}

	boolean tracksContentRevisions () {
		return tracksContentRevisions;
	}

	long itemContentRevision () {
		return itemRevisionSource == null ? 0 : itemRevisionSource.contentRevision();
	}

	long fluidContentRevision () {
		return fluidRevisionSource == null ? 0 : fluidRevisionSource.contentRevision();
	}

	public MachineRecipeProcessor processor (Runnable changeCallback) {
		return new MachineRecipeProcessor(this, changeCallback);
	}

	/**
	 * Creates a processor with a transactional per-tick work requirement such as
	 * machine energy.
	 */
	public MachineRecipeProcessor processor (MachineWorkRequirement workRequirement, Runnable changeCallback) {
		return new MachineRecipeProcessor(this, workRequirement, changeCallback);
	}

	/**
	 * Creates a processor whose recipe sources are evaluated in strict declaration
	 * order.
	 */
	public MachineRecipeProcessor processor (List<MachineRecipeSource> sources, MachineWorkRequirement workRequirement, Runnable changeCallback) {
		return new MachineRecipeProcessor(this, sources, workRequirement, changeCallback);
	}

	boolean transfer (MachineRecipe recipe, TransactionContext transaction) {
		requireRecipe(recipe);
		if (items != null) {
			for (MachineRecipeSchema.Slot slot : definition.schema().itemInputs()) {
				var ingredient = recipe.itemInputs().get(slot.name());
				if (ingredient == null) continue;
				int index = itemSlots.get(slot.name());
				ItemResource resource = items.getResource(index);
				if (items.extract(index, resource, ingredient.count(), transaction) != ingredient.count()) return false;
			}
		}
		if (fluids != null) {
			for (MachineRecipeSchema.Slot slot : definition.schema().fluidInputs()) {
				var ingredient = recipe.fluidInputs().get(slot.name());
				if (ingredient == null) continue;
				int index = fluidSlots.get(slot.name());
				FluidResource resource = fluids.getResource(index);
				if (fluids.extract(index, resource, ingredient.amount(), transaction) != ingredient.amount()) return false;
			}
		}
		if (items != null) {
			for (MachineRecipeSchema.Slot slot : definition.schema().itemOutputs()) {
				ItemStack stack = recipe.internalItemOutput(slot.name());
				if (stack == null) continue;
				int index = itemSlots.get(slot.name());
				if (items.insert(index, ItemResource.of(stack), stack.getCount(), transaction) != stack.getCount()) return false;
			}
		}
		if (fluids != null) {
			for (MachineRecipeSchema.Slot slot : definition.schema().fluidOutputs()) {
				FluidStack stack = recipe.internalFluidOutput(slot.name());
				if (stack == null) continue;
				int index = fluidSlots.get(slot.name());
				if (fluids.insert(index, FluidResource.of(stack), stack.getAmount(), transaction) != stack.getAmount()) return false;
			}
		}
		return true;
	}

	private void requireRecipe (MachineRecipe recipe) {
		Objects.requireNonNull(recipe, "recipe");
		if (recipe.definition() != definition) {
			throw new IllegalArgumentException("Recipe belongs to " + recipe.definition().id() + ", not binding " + definition.id());
		}
	}

	private static <R extends net.neoforged.neoforge.transfer.resource.Resource> Map<String, Integer> resolve (String resourceName, java.util.List<String> logicalSlots, Map<String, String> mappings, @Nullable ResourceInventoryDefinition<R> physicalDefinition, @Nullable ResourceHandler<R> handler) {
		if (logicalSlots.isEmpty()) return Map.of();
		if (handler == null || physicalDefinition == null) {
			throw new IllegalArgumentException("Recipe type requires " + resourceName + " slots, but no " + resourceName + " handler and definition were supplied");
		}
		if (handler.size() != physicalDefinition.size()) {
			throw new IllegalArgumentException("Bound " + resourceName + " handler has " + handler.size() + " slots, but its physical definition has " + physicalDefinition.size());
		}
		var resolved = new LinkedHashMap<String, Integer>();
		for (String logical : logicalSlots) {
			String physical = mappings.getOrDefault(logical, logical);
			try {
				resolved.put(logical, physicalDefinition.index(physical));
			} catch (IllegalArgumentException exception) {
				throw new IllegalArgumentException("Cannot bind recipe " + resourceName + " slot '" + logical + "' to physical slot '" + physical + "': " + exception.getMessage(), exception);
			}
		}
		return Map.copyOf(resolved);
	}

	/**
	 * Builder for machines whose physical slot names differ from recipe field names.
	 */
	public static final class Builder {

		private final     MachineRecipeDefinition<?>                 definition;
		private final     Map<String, String>                        itemMappings  = new LinkedHashMap<>();
		private final     Map<String, String>                        fluidMappings = new LinkedHashMap<>();
		private @Nullable ResourceHandler<ItemResource>              items;
		private @Nullable ResourceInventoryDefinition<ItemResource>  itemDefinition;
		private @Nullable ResourceHandler<FluidResource>             fluids;
		private @Nullable ResourceInventoryDefinition<FluidResource> fluidDefinition;

		Builder (MachineRecipeDefinition<?> definition) {
			this.definition = Objects.requireNonNull(definition, "definition");
		}

		public Builder items (ItemResourceHandler handler) {
			return items(handler, handler.getDefinition());
		}

		public Builder items (ResourceHandler<ItemResource> handler, ItemInventoryDefinition definition) {
			return items(handler, Objects.requireNonNull(definition, "definition").genericDefinition());
		}

		public Builder items (ResourceHandler<ItemResource> handler, ResourceInventoryDefinition<ItemResource> definition) {
			if (items != null) throw new IllegalStateException("Item handler is already bound");
			this.items = Objects.requireNonNull(handler, "handler");
			this.itemDefinition = Objects.requireNonNull(definition, "definition");
			return this;
		}

		public Builder fluids (FluidResourceHandler handler) {
			return fluids(handler, handler.getDefinition());
		}

		public Builder fluids (ResourceHandler<FluidResource> handler, FluidInventoryDefinition definition) {
			return fluids(handler, Objects.requireNonNull(definition, "definition").genericDefinition());
		}

		public Builder fluids (ResourceHandler<FluidResource> handler, ResourceInventoryDefinition<FluidResource> definition) {
			if (fluids != null) throw new IllegalStateException("Fluid handler is already bound");
			this.fluids = Objects.requireNonNull(handler, "handler");
			this.fluidDefinition = Objects.requireNonNull(definition, "definition");
			return this;
		}

		public Builder mapItem (String recipeSlot, String physicalSlot) {
			if (!definition.schema().hasItemSlot(recipeSlot)) throw new IllegalArgumentException("Unknown item recipe slot '" + recipeSlot + "'");
			putMapping(itemMappings, recipeSlot, physicalSlot, "item");
			return this;
		}

		public Builder mapItem (MachineRecipeSlot.Item recipeSlot, ResourceSlotKey physicalSlot) {
			return mapItem(Objects.requireNonNull(recipeSlot, "recipeSlot").name(), Objects.requireNonNull(physicalSlot, "physicalSlot").name());
		}

		public Builder mapFluid (String recipeSlot, String physicalSlot) {
			if (!definition.schema().hasFluidSlot(recipeSlot)) throw new IllegalArgumentException("Unknown fluid recipe slot '" + recipeSlot + "'");
			putMapping(fluidMappings, recipeSlot, physicalSlot, "fluid");
			return this;
		}

		public Builder mapFluid (MachineRecipeSlot.Fluid recipeSlot, ResourceSlotKey physicalSlot) {
			return mapFluid(Objects.requireNonNull(recipeSlot, "recipeSlot").name(), Objects.requireNonNull(physicalSlot, "physicalSlot").name());
		}

		public MachineRecipeBinding build () {
			return new MachineRecipeBinding(this);
		}

		private static void putMapping (Map<String, String> mappings, String recipeSlot, String physicalSlot, String resourceName) {
			Objects.requireNonNull(recipeSlot, "recipeSlot");
			Objects.requireNonNull(physicalSlot, "physicalSlot");
			if (physicalSlot.isBlank()) throw new IllegalArgumentException("Physical " + resourceName + " slot name must not be blank");
			if (mappings.putIfAbsent(recipeSlot, physicalSlot) != null) throw new IllegalArgumentException("Recipe " + resourceName + " slot '" + recipeSlot + "' is already mapped");
		}
	}
}
