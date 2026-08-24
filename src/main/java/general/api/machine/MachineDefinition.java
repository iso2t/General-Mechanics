package general.api.machine;

import general.api.crafting.MachineEnergyWorkRequirement;
import general.api.crafting.MachineRecipeDefinition;
import general.api.crafting.MachineRecipeSlot;
import general.api.crafting.MachineWorkRequirement;
import general.api.definitions.MultiblockDefinition;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.power.MachinePowerProfile;
import general.api.machine.upgrade.MachineUpgradeProfile;
import general.api.multiblock.MultiblockInstance;
import general.api.network.service.NetworkServiceContainer;
import general.api.transfer.ResourceAccessPolicy;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.ResourceSlotKey;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.item.ItemInventoryDefinition;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Immutable type-level declaration used to construct one machine's runtime.
 *
 * <p>The definition contains policy, not world state. Resource layouts, sided
 * access, power economics, recipes, networking, and optional multiblock behavior
 * are declared once and shared by every block entity of that machine type.</p>
 */
public final class MachineDefinition {

	private final @Nullable ItemSpec                           items;
	private final @Nullable FluidSpec                          fluids;
	private final @Nullable EnergySpec                         energy;
	private final @Nullable MachineSideConfigurationDefinition sideConfiguration;
	private final @Nullable RecipeSpec                         recipes;
	private final @Nullable NetworkSpec                        network;
	private final @Nullable MultiblockSpec                     multiblock;
	private final           Map<String, ToIntFunction<MachineRuntime>> itemRecipeOutputMultipliers;
	private final           Map<String, ToIntFunction<MachineRuntime>> fluidRecipeOutputMultipliers;
	private final           boolean                            litState;

	private MachineDefinition (Builder builder) {
		this.items = builder.items;
		this.fluids = builder.fluids;
		this.energy = builder.energy;
		this.sideConfiguration = builder.sideConfiguration;
		this.recipes = builder.recipes;
		this.network = builder.network;
		this.multiblock = builder.multiblock;
		this.itemRecipeOutputMultipliers = Map.copyOf(builder.itemRecipeOutputMultipliers);
		this.fluidRecipeOutputMultipliers = Map.copyOf(builder.fluidRecipeOutputMultipliers);
		this.litState = builder.litState;
	}

	public static Builder builder () {
		return new Builder();
	}

	public @Nullable ItemSpec items () {
		return items;
	}

	public @Nullable FluidSpec fluids () {
		return fluids;
	}

	public @Nullable EnergySpec energy () {
		return energy;
	}

	public @Nullable MachineSideConfigurationDefinition sideConfiguration () {
		return sideConfiguration;
	}

	public @Nullable RecipeSpec recipes () {
		return recipes;
	}

	public @Nullable NetworkSpec network () {
		return network;
	}

	public @Nullable MultiblockSpec multiblock () {
		return multiblock;
	}

	public Map<String, ToIntFunction<MachineRuntime>> itemRecipeOutputMultipliers () {
		return itemRecipeOutputMultipliers;
	}

	public Map<String, ToIntFunction<MachineRuntime>> fluidRecipeOutputMultipliers () {
		return fluidRecipeOutputMultipliers;
	}

	public boolean hasLitState () {
		return litState;
	}

	public static final class Builder {

		private @Nullable ItemSpec                           items;
		private @Nullable FluidSpec                          fluids;
		private @Nullable EnergySpec                         energy;
		private @Nullable MachineSideConfigurationDefinition sideConfiguration;
		private @Nullable RecipeSpec                         recipes;
		private @Nullable NetworkSpec                        network;
		private @Nullable MultiblockSpec                     multiblock;
		private final Map<String, ToIntFunction<MachineRuntime>> itemRecipeOutputMultipliers = new LinkedHashMap<>();
		private final Map<String, ToIntFunction<MachineRuntime>> fluidRecipeOutputMultipliers = new LinkedHashMap<>();
		private           boolean                            litState;

		private Builder () {
		}

		public Builder items (ItemInventoryDefinition definition, Consumer<ItemBuilder> configuration) {
			if (items != null) throw new IllegalStateException("Machine item storage is already defined");
			ItemBuilder builder = new ItemBuilder(definition);
			Objects.requireNonNull(configuration, "configuration").accept(builder);
			items = builder.build();
			return this;
		}

		public Builder fluids (FluidInventoryDefinition definition, int baseTransfer, Consumer<FluidBuilder> configuration) {
			if (fluids != null) throw new IllegalStateException("Machine fluid storage is already defined");
			FluidBuilder builder = new FluidBuilder(definition, baseTransfer);
			Objects.requireNonNull(configuration, "configuration").accept(builder);
			fluids = builder.build();
			return this;
		}

		public Builder energy (MachinePowerProfile baseProfile) {
			return energy(baseProfile, baseProfile.capacity(), ignored -> {
			});
		}

		public Builder energy (MachinePowerProfile baseProfile, Consumer<EnergyBuilder> configuration) {
			return energy(baseProfile, baseProfile.capacity(), configuration);
		}

		public Builder energy (MachinePowerProfile baseProfile, int baseMaxExtract, Consumer<EnergyBuilder> configuration) {
			if (energy != null) throw new IllegalStateException("Machine energy storage is already defined");
			EnergyBuilder builder = new EnergyBuilder(baseProfile, baseMaxExtract);
			Objects.requireNonNull(configuration, "configuration").accept(builder);
			energy = builder.build();
			return this;
		}

		public Builder sideConfiguration (MachineSideConfigurationDefinition definition) {
			if (sideConfiguration != null) throw new IllegalStateException("Machine side configuration is already defined");
			sideConfiguration = Objects.requireNonNull(definition, "definition");
			return this;
		}

		public Builder recipes (Supplier<? extends MachineRecipeDefinition<?>> definition) {
			return recipes(definition, runtime -> MachineWorkRequirement.free());
		}

		public Builder recipes (Supplier<? extends MachineRecipeDefinition<?>> definition, WorkFactory workFactory) {
			if (recipes != null) throw new IllegalStateException("Machine recipe processing is already defined");
			recipes = new RecipeSpec(definition, workFactory);
			return this;
		}

		public Builder poweredRecipes (Supplier<? extends MachineRecipeDefinition<?>> definition) {
			if (energy == null) throw new IllegalStateException("Powered machine recipes require an energy definition first");
			return recipes(definition, runtime -> MachineEnergyWorkRequirement.fromProfile(runtime.requireEnergyHandler(), runtime::getPowerProfile));
		}

		/**
		 * Scales one logical item result at execution time. The multiplier participates
		 * in output-space simulation and the final atomic transfer.
		 */
		public Builder recipeItemOutputMultiplier (MachineRecipeSlot.ItemOutput slot, ToIntFunction<MachineRuntime> multiplier) {
			Objects.requireNonNull(slot, "slot");
			if (itemRecipeOutputMultipliers.putIfAbsent(slot.name(), Objects.requireNonNull(multiplier, "multiplier")) != null) throw new IllegalArgumentException("Recipe item output multiplier for '" + slot.name() + "' is already defined");
			return this;
		}

		public Builder recipeFluidOutputMultiplier (MachineRecipeSlot.FluidOutput slot, ToIntFunction<MachineRuntime> multiplier) {
			Objects.requireNonNull(slot, "slot");
			if (fluidRecipeOutputMultipliers.putIfAbsent(slot.name(), Objects.requireNonNull(multiplier, "multiplier")) != null) throw new IllegalArgumentException("Recipe fluid output multiplier for '" + slot.name() + "' is already defined");
			return this;
		}

		public Builder network (String nodeName, NetworkRegistrar registrar) {
			if (network != null) throw new IllegalStateException("Machine network behavior is already defined");
			network = new NetworkSpec(nodeName, registrar);
			return this;
		}

		public Builder multiblock (Supplier<MultiblockDefinition> definition, boolean allowStandaloneOperation, UpgradeResolver upgradeResolver) {
			if (multiblock != null) throw new IllegalStateException("Machine multiblock behavior is already defined");
			multiblock = new MultiblockSpec(definition, allowStandaloneOperation, upgradeResolver);
			return this;
		}

		public Builder multiblock (Supplier<MultiblockDefinition> definition, boolean allowStandaloneOperation) {
			return multiblock(definition, allowStandaloneOperation, (level, instance) -> MachineUpgradeProfile.identity());
		}

		public Builder litState () {
			litState = true;
			return this;
		}

		public MachineDefinition build () {
			if (recipes != null && items == null && fluids == null) {
				throw new IllegalStateException("Machine recipe processing requires item or fluid storage");
			}
			if (recipes != null) litState = true;
			if (recipes == null && (!itemRecipeOutputMultipliers.isEmpty() || !fluidRecipeOutputMultipliers.isEmpty())) throw new IllegalStateException("Recipe output multipliers require recipe processing");
			return new MachineDefinition(this);
		}
	}

	public record ItemSpec(ItemInventoryDefinition definition, int[] lockableSlots, ResourceAccessPolicy<ItemResource> inputAccess, ResourceAccessPolicy<ItemResource> outputAccess, ResourceAccessPolicy<ItemResource> networkAccess) {

		public ItemSpec {
			Objects.requireNonNull(definition, "definition");
			lockableSlots = Objects.requireNonNull(lockableSlots, "lockableSlots").clone();
			Objects.requireNonNull(inputAccess, "inputAccess");
			Objects.requireNonNull(outputAccess, "outputAccess");
			Objects.requireNonNull(networkAccess, "networkAccess");
		}

		@Override
		public int[] lockableSlots () {
			return lockableSlots.clone();
		}
	}

	public static final class ItemBuilder {

		private final ItemInventoryDefinition definition;
		private final Set<Integer>            lockable = new LinkedHashSet<>();
		private final Set<Integer>            inputs   = new LinkedHashSet<>();
		private final Set<Integer>            outputs  = new LinkedHashSet<>();

		private ItemBuilder (ItemInventoryDefinition definition) {
			this.definition = Objects.requireNonNull(definition, "definition");
		}

		public ItemBuilder lockable (ResourceSlotKey... slots) {
			add(lockable, slots);
			return this;
		}

		public ItemBuilder input (ResourceSlotKey... slots) {
			add(inputs, slots);
			return this;
		}

		public ItemBuilder output (ResourceSlotKey... slots) {
			add(outputs, slots);
			return this;
		}

		private ItemSpec build () {
			int[] inputSlots = toArray(inputs);
			int[] outputSlots = toArray(outputs);
			var input = definition.access().insert(inputSlots).build();
			var output = definition.access().extract(outputSlots).build();
			var network = definition.access().insert(inputSlots).extract(outputSlots).build();
			return new ItemSpec(definition, toArray(lockable), input, output, network);
		}

		private void add (Set<Integer> target, ResourceSlotKey[] slots) {
			Objects.requireNonNull(slots, "slots");
			for (int index = 0; index < slots.length; index++) target.add(definition.index(Objects.requireNonNull(slots[index], "slot at index " + index)));
		}
	}

	public record FluidSpec(FluidInventoryDefinition definition, int baseTransfer, ResourceAccessPolicy<FluidResource> inputAccess, ResourceAccessPolicy<FluidResource> outputAccess, ResourceAccessPolicy<FluidResource> networkAccess) {

		public FluidSpec {
			Objects.requireNonNull(definition, "definition");
			if (baseTransfer <= 0) throw new IllegalArgumentException("Base fluid transfer must be positive: " + baseTransfer);
			Objects.requireNonNull(inputAccess, "inputAccess");
			Objects.requireNonNull(outputAccess, "outputAccess");
			Objects.requireNonNull(networkAccess, "networkAccess");
		}
	}

	public static final class FluidBuilder {

		private final FluidInventoryDefinition definition;
		private final int                      baseTransfer;
		private final Set<Integer>             inputs  = new LinkedHashSet<>();
		private final Set<Integer>             outputs = new LinkedHashSet<>();

		private FluidBuilder (FluidInventoryDefinition definition, int baseTransfer) {
			this.definition = Objects.requireNonNull(definition, "definition");
			if (baseTransfer <= 0) throw new IllegalArgumentException("Base fluid transfer must be positive: " + baseTransfer);
			this.baseTransfer = baseTransfer;
		}

		public FluidBuilder input (ResourceSlotKey... slots) {
			add(inputs, slots);
			return this;
		}

		public FluidBuilder output (ResourceSlotKey... slots) {
			add(outputs, slots);
			return this;
		}

		private FluidSpec build () {
			int[] inputSlots = toArray(inputs);
			int[] outputSlots = toArray(outputs);
			var input = definition.access().insert(inputSlots).build();
			var output = definition.access().extract(outputSlots).build();
			var network = definition.access().insert(inputSlots).extract(outputSlots).build();
			return new FluidSpec(definition, baseTransfer, input, output, network);
		}

		private void add (Set<Integer> target, ResourceSlotKey[] slots) {
			Objects.requireNonNull(slots, "slots");
			for (int index = 0; index < slots.length; index++) target.add(definition.index(Objects.requireNonNull(slots[index], "slot at index " + index)));
		}
	}

	public record EnergySpec(MachinePowerProfile baseProfile, int baseMaxExtract, ResourceIoMode directMode, ResourceIoMode networkMode) {

		public EnergySpec {
			Objects.requireNonNull(baseProfile, "baseProfile");
			if (baseMaxExtract < 0) throw new IllegalArgumentException("Base maximum energy extraction cannot be negative: " + baseMaxExtract);
			Objects.requireNonNull(directMode, "directMode");
			Objects.requireNonNull(networkMode, "networkMode");
		}
	}

	public static final class EnergyBuilder {

		private final MachinePowerProfile baseProfile;
		private final int                 baseMaxExtract;
		private       ResourceIoMode      directMode  = ResourceIoMode.INSERT;
		private       ResourceIoMode      networkMode = ResourceIoMode.NONE;

		private EnergyBuilder (MachinePowerProfile baseProfile, int baseMaxExtract) {
			this.baseProfile = Objects.requireNonNull(baseProfile, "baseProfile");
			if (baseMaxExtract < 0) throw new IllegalArgumentException("Base maximum energy extraction cannot be negative: " + baseMaxExtract);
			this.baseMaxExtract = baseMaxExtract;
		}

		public EnergyBuilder direct (ResourceIoMode mode) {
			directMode = Objects.requireNonNull(mode, "mode");
			return this;
		}

		public EnergyBuilder network (ResourceIoMode mode) {
			networkMode = Objects.requireNonNull(mode, "mode");
			return this;
		}

		private EnergySpec build () {
			return new EnergySpec(baseProfile, baseMaxExtract, directMode, networkMode);
		}
	}

	public record RecipeSpec(Supplier<? extends MachineRecipeDefinition<?>> definitionSupplier, WorkFactory workFactory) {

		public RecipeSpec {
			Objects.requireNonNull(definitionSupplier, "definitionSupplier");
			Objects.requireNonNull(workFactory, "workFactory");
		}

		public MachineRecipeDefinition<?> definition () {
			return Objects.requireNonNull(definitionSupplier.get(), "Machine recipe definition supplier returned null");
		}
	}

	public record NetworkSpec(String nodeName, NetworkRegistrar registrar) {

		public NetworkSpec {
			Objects.requireNonNull(nodeName, "nodeName");
			if (nodeName.isBlank()) throw new IllegalArgumentException("Machine network node name must not be blank");
			Objects.requireNonNull(registrar, "registrar");
		}
	}

	public record MultiblockSpec(Supplier<MultiblockDefinition> definitionSupplier, boolean allowStandaloneOperation, UpgradeResolver upgradeResolver) {

		public MultiblockSpec {
			Objects.requireNonNull(definitionSupplier, "definitionSupplier");
			Objects.requireNonNull(upgradeResolver, "upgradeResolver");
		}

		public MultiblockDefinition definition () {
			return Objects.requireNonNull(definitionSupplier.get(), "Machine multiblock definition supplier returned null");
		}
	}

	@FunctionalInterface
	public interface WorkFactory {

		MachineWorkRequirement create (MachineRuntime runtime);
	}

	@FunctionalInterface
	public interface NetworkRegistrar {

		void register (MachineRuntime runtime, NetworkServiceContainer services);
	}

	@FunctionalInterface
	public interface UpgradeResolver {

		MachineUpgradeProfile resolve (Level level, MultiblockInstance instance);
	}

	private static int[] toArray (Set<Integer> values) {
		return values.stream().mapToInt(Integer::intValue).toArray();
	}
}
