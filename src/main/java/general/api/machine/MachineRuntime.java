package general.api.machine;

import general.api.block.util.ILitProvider;
import general.api.crafting.MachineRecipeBinding;
import general.api.crafting.MachineRecipeProcessor;
import general.api.definitions.MultiblockDefinition;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfiguration;
import general.api.machine.config.MachineSideMode;
import general.api.machine.power.MachinePowerProfile;
import general.api.machine.upgrade.MachineUpgradeProfile;
import general.api.model.ConfigurableMachineModelData;
import general.api.multiblock.MultiblockController;
import general.api.multiblock.MultiblockHandler;
import general.api.multiblock.MultiblockInstance;
import general.api.network.INetworkInterface;
import general.api.network.NetworkNode;
import general.api.transfer.ResourceAccess;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.RestrictedResourceHandler;
import general.api.transfer.SidedResourceHandlers;
import general.api.transfer.energy.ProfiledEnergyHandler;
import general.api.transfer.energy.SidedEnergyHandlers;
import general.api.transfer.energy.SupplierBackedEnergyHandler;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.ProfiledFluidResourceHandler;
import general.api.transfer.fluid.ProfiledFluidTransferHandler;
import general.api.transfer.item.ItemInventoryDefinition;
import general.api.transfer.item.ItemResourceHandler;
import general.api.transfer.item.LockableItemResourceHandler;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Per-block-entity state assembled from a shared {@link MachineDefinition}.
 *
 * <p>The runtime owns handlers and recurring lifecycle behavior. Concrete block
 * entities expose only the feature interfaces they actually support and delegate
 * those contracts to this object.</p>
 */
public final class MachineRuntime implements INetworkInterface {

	private static final String ITEMS_TAG              = "items";
	private static final String ITEM_LOCK_TAG          = "item_lock";
	private static final String FLUIDS_TAG             = "fluids";
	private static final String ENERGY_TAG             = "energy";
	private static final String RECIPE_PROCESSOR_TAG   = "recipe_processor";
	private static final String SIDE_CONFIGURATION_TAG = "side_configuration";
	private static final String MULTIBLOCK_PROFILE_TAG = "multiblock_upgrade_profile";
	private static final String OPERATING_PROFILE_TAG  = "operating_upgrade_profile";
	private static final String MULTIBLOCK_SPEED_TAG   = "multiblock_speed";
	private static final String OPERATING_SPEED_TAG    = "operating_speed";

	private final           MachineBlockEntity       owner;
	@Getter
	private final           MachineDefinition        definition;
	private final @Nullable MachineSideConfiguration sideConfiguration;
	private final @Nullable ItemResourceHandler                  items;
	private final @Nullable SidedResourceHandlers<ItemResource>  sidedItems;
	private final @Nullable ResourceHandler<ItemResource>        networkItems;
	private final @Nullable ProfiledFluidResourceHandler         fluidStorage;
	private final @Nullable ResourceHandler<FluidResource>       fluidTransfers;
	private final @Nullable SidedResourceHandlers<FluidResource> sidedFluids;
	private final @Nullable ResourceHandler<FluidResource>       networkFluids;
	private final @Nullable ProfiledEnergyHandler                energy;
	private final @Nullable SidedEnergyHandlers                  sidedEnergy;
	private final @Nullable EnergyHandler                        networkEnergy;
	private final @Nullable MachineRecipeProcessor               recipeProcessor;
	private final @Nullable NetworkNode                          networkNode;

	@Getter
	private boolean formed;
	private boolean multiblockProfileResolved = true;
	@Getter
	private           MachineUpgradeProfile multiblockUpgradeProfile = MachineUpgradeProfile.identity();
	@Getter
	private           MachineUpgradeProfile operatingUpgradeProfile  = MachineUpgradeProfile.identity();
	private @Nullable MachinePowerProfile   operatingPowerProfile;

	MachineRuntime (MachineBlockEntity owner, MachineDefinition definition) {
		this.owner = Objects.requireNonNull(owner, "owner");
		this.definition = Objects.requireNonNull(definition, "definition");
		this.sideConfiguration = definition.sideConfiguration() == null ? null : definition.sideConfiguration().createConfiguration(this::onSideConfigurationChanged);

		MachineDefinition.ItemSpec itemSpec = definition.items();
		if (itemSpec == null) {
			this.items = null;
			this.sidedItems = null;
			this.networkItems = null;
		} else {
			int[] lockableSlots = itemSpec.lockableSlots();
			this.items = lockableSlots.length == 0 ? itemSpec.definition().createHandler(owner::setChanged) : itemSpec.definition().createLockableHandler(owner::setChanged, lockableSlots);
			var sided = SidedResourceHandlers.builder(items);
			for (Direction side : Direction.values()) {
				sided.side(side, ResourceAccess.switching(itemSpec.inputAccess(), itemSpec.outputAccess(), () -> itemIoMode(side)));
			}
			this.sidedItems = sided.build();
			this.networkItems = new RestrictedResourceHandler<>(items, itemSpec.networkAccess());
		}

		MachineDefinition.FluidSpec fluidSpec = definition.fluids();
		if (fluidSpec == null) {
			this.fluidStorage = null;
			this.fluidTransfers = null;
			this.sidedFluids = null;
			this.networkFluids = null;
		} else {
			this.fluidStorage = fluidSpec.definition().createProfiledHandler(this::getOperatingUpgradeProfile, owner::setChanged);
			this.fluidTransfers = new ProfiledFluidTransferHandler(fluidStorage, fluidSpec.baseTransfer(), this::getOperatingUpgradeProfile);
			var sided = SidedResourceHandlers.builder(fluidTransfers);
			for (Direction side : Direction.values()) {
				sided.side(side, ResourceAccess.switching(fluidSpec.inputAccess(), fluidSpec.outputAccess(), () -> fluidIoMode(side)));
			}
			this.sidedFluids = sided.build();
			this.networkFluids = new RestrictedResourceHandler<>(fluidTransfers, fluidSpec.networkAccess());
		}

		MachineDefinition.EnergySpec energySpec = definition.energy();
		if (energySpec == null) {
			this.energy = null;
			this.sidedEnergy = null;
			this.networkEnergy = null;
			this.operatingPowerProfile = null;
		} else {
			MachinePowerProfile base = energySpec.baseProfile();
			this.operatingPowerProfile = base;
			this.energy = new ProfiledEnergyHandler(base.capacity(), base.maxInput(), energySpec.baseMaxExtract(), this::getOperatingUpgradeProfile, owner::setChanged);
			var sided = SidedEnergyHandlers.builder(energy);
			for (Direction side : Direction.values()) sided.side(side, () -> energyIoMode(side));
			this.sidedEnergy = sided.build();
			this.networkEnergy = new SupplierBackedEnergyHandler(() -> energy, () -> directCapabilitiesEnabled() ? energySpec.networkMode() : ResourceIoMode.NONE);
		}

		MachineDefinition.RecipeSpec recipeSpec = definition.recipes();
		if (recipeSpec == null) {
			this.recipeProcessor = null;
		} else {
			MachineRecipeBinding.Builder binding = recipeSpec.definition().bindingBuilder();
			if (items != null) binding.items(items, requireItemDefinition());
			if (fluidStorage != null) binding.fluids(fluidStorage, requireFluidDefinition());
			this.recipeProcessor = binding.build().processor(recipeSpec.workFactory().create(this), owner::setChanged);
		}

		MachineDefinition.NetworkSpec networkSpec = definition.network();
		this.networkNode = networkSpec == null ? null : new NetworkNode(networkSpec.nodeName());
		refreshNetworkServices();
	}

	public boolean hasItems () {
		return items != null;
	}

	public ItemResourceHandler requireItemHandler () {
		return Objects.requireNonNull(items, "Machine does not define item storage");
	}

	public LockableItemResourceHandler requireLockableItemHandler () {
		if (items instanceof LockableItemResourceHandler lockable) return lockable;
		throw new IllegalStateException("Machine does not define lockable item storage");
	}

	public ItemInventoryDefinition requireItemDefinition () {
		MachineDefinition.ItemSpec spec = definition.items();
		if (spec == null) throw new IllegalStateException("Machine does not define item storage");
		return spec.definition();
	}

	public SidedResourceHandlers<ItemResource> requireSidedItemHandlers () {
		return Objects.requireNonNull(sidedItems, "Machine does not define sided item storage");
	}

	public @Nullable ResourceHandler<ItemResource> getItemCapability (@Nullable Direction side) {
		if (!directCapabilitiesEnabled() || items == null || sideConfiguration != null && !getSideMode(side).isItem()) return null;
		return requireSidedItemHandlers().forSide(side);
	}

	public @Nullable ResourceHandler<ItemResource> getNetworkItemHandler () {
		return directCapabilitiesEnabled() ? networkItems : null;
	}

	public boolean hasFluids () {
		return fluidStorage != null;
	}

	public ResourceHandler<FluidResource> requireFluidHandler () {
		return Objects.requireNonNull(fluidTransfers, "Machine does not define fluid storage");
	}

	public ProfiledFluidResourceHandler requireFluidStorage () {
		return Objects.requireNonNull(fluidStorage, "Machine does not define fluid storage");
	}

	public FluidInventoryDefinition requireFluidDefinition () {
		MachineDefinition.FluidSpec spec = definition.fluids();
		if (spec == null) throw new IllegalStateException("Machine does not define fluid storage");
		return spec.definition();
	}

	public SidedResourceHandlers<FluidResource> requireSidedFluidHandlers () {
		return Objects.requireNonNull(sidedFluids, "Machine does not define sided fluid storage");
	}

	public @Nullable ResourceHandler<FluidResource> getFluidCapability (@Nullable Direction side) {
		if (!directCapabilitiesEnabled() || fluidStorage == null || sideConfiguration != null && !getSideMode(side).isFluid()) return null;
		return requireSidedFluidHandlers().forSide(side);
	}

	public @Nullable ResourceHandler<FluidResource> getNetworkFluidHandler () {
		return directCapabilitiesEnabled() ? networkFluids : null;
	}

	public boolean hasEnergy () {
		return energy != null;
	}

	public ProfiledEnergyHandler requireEnergyHandler () {
		return Objects.requireNonNull(energy, "Machine does not define energy storage");
	}

	public SidedEnergyHandlers requireSidedEnergyHandlers () {
		return Objects.requireNonNull(sidedEnergy, "Machine does not define sided energy storage");
	}

	public @Nullable EnergyHandler getEnergyCapability (@Nullable Direction side) {
		return directCapabilitiesEnabled() && energy != null ? requireSidedEnergyHandlers().forSide(side) : null;
	}

	public @Nullable EnergyHandler getNetworkEnergyHandler () {
		if (!directCapabilitiesEnabled() || definition.energy() == null || definition.energy().networkMode() == ResourceIoMode.NONE) return null;
		return networkEnergy;
	}

	public MachineRecipeProcessor requireRecipeProcessor () {
		return Objects.requireNonNull(recipeProcessor, "Machine does not define recipe processing");
	}

	public int getProgress () {
		return recipeProcessor == null ? 0 : recipeProcessor.progress();
	}

	public int getMaxProgress () {
		return recipeProcessor == null ? 0 : recipeProcessor.maxProgress();
	}

	public MachineRecipeProcessor.Status getProcessingStatus () {
		return recipeProcessor == null ? MachineRecipeProcessor.Status.IDLE : recipeProcessor.status();
	}

	public MachinePowerProfile getPowerProfile () {
		return Objects.requireNonNull(operatingPowerProfile, "Machine does not define a power profile");
	}

	public double getProcessingSpeedMultiplier () {
		return operatingPowerProfile == null ? operatingUpgradeProfile.processingSpeedMultiplier() : operatingPowerProfile.speedMultiplier();
	}

	public int getEnergyStored () {
		return energy == null ? 0 : energy.getAmountAsInt();
	}

	public int getEnergyCapacity () {
		return energy == null ? 0 : energy.getCapacityAsInt();
	}

	public int getMaxEnergyInput () {
		return energy == null ? 0 : energy.getMaxInsert();
	}

	public ItemStack getItemStack (int slot) {
		ItemResourceHandler handler = requireItemHandler();
		return handler.getResource(slot).toStack(handler.getAmountAsInt(slot));
	}

	public @Nullable MachineSideConfiguration getSideConfiguration () {
		return sideConfiguration;
	}

	public MachineSideConfiguration requireSideConfiguration () {
		return Objects.requireNonNull(sideConfiguration, "Machine does not define side configuration");
	}

	public MachineSideMode getSideMode (MachineFace face) {
		return sideConfiguration == null ? MachineSideMode.NONE : sideConfiguration.getMode(face);
	}

	public MachineSideMode getSideMode (@Nullable Direction side) {
		return side == null || sideConfiguration == null ? MachineSideMode.NONE : sideConfiguration.getMode(getMachineFront(), side);
	}

	public boolean setSideMode (MachineFace face, MachineSideMode mode) {
		if (sideConfiguration == null || formed || owner.getLevel() != null && owner.getLevel().isClientSide()) return false;
		return sideConfiguration.setMode(face, mode);
	}

	public Direction getMachineFront () {
		BlockState state = owner.getBlockState();
		if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			throw new IllegalStateException("Configurable machine block does not define horizontal facing: " + state);
		}
		return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	public MultiblockDefinition requireMultiblockDefinition () {
		MachineDefinition.MultiblockSpec spec = definition.multiblock();
		if (spec == null) throw new IllegalStateException("Machine does not define multiblock behavior");
		return spec.definition();
	}

	public Direction getMultiblockFacing () {
		return getMachineFront().getOpposite();
	}

	public void setFormed (boolean formed) {
		if (this.formed == formed) return;
		this.formed = formed;
		if (!formed) {
			multiblockProfileResolved = true;
			multiblockUpgradeProfile = MachineUpgradeProfile.identity();
		}
		operatingUpgradeProfile = MachineUpgradeProfile.identity();
		operatingPowerProfile = definition.energy() == null ? null : definition.energy().baseProfile();
		refreshNetworkServices();
		resetProcessing();
		owner.setChanged();
		notifyExternalStateChanged();
	}

	public void onMultiblockValidated (MultiblockInstance instance) {
		MachineDefinition.MultiblockSpec spec = requireMultiblockSpec();
		if (owner.getLevel() == null) return;
		MachineUpgradeProfile resolved = Objects.requireNonNull(spec.upgradeResolver().resolve(owner.getLevel(), instance), "Machine upgrade resolver returned null");
		boolean changed = !multiblockProfileResolved || !resolved.equals(multiblockUpgradeProfile);
		multiblockUpgradeProfile = resolved;
		multiblockProfileResolved = true;
		if (changed) owner.setChanged();
		if (owner.getLevel() instanceof ServerLevel serverLevel && formed) refreshOperatingProfile(serverLevel, isMultiblockOperational());
	}

	public void onMultiblockFormed (MultiblockInstance instance) {
		if (owner.getLevel() instanceof ServerLevel serverLevel) MultiblockHandler.spawnFormationParticles(serverLevel, instance);
	}

	public boolean isMultiblockOperational () {
		return owner instanceof MultiblockController controller && controller.isMultiblockOperational();
	}

	public MachineRecipeProcessor.TickResult serverTick (ServerLevel level) {
		if (recipeProcessor == null) return new MachineRecipeProcessor.TickResult(MachineRecipeProcessor.Status.IDLE, false);

		MachineDefinition.MultiblockSpec multiblock = definition.multiblock();
		if (multiblock != null) {
			if (formed && !multiblockProfileResolved) return haltProcessing(level, false);
			boolean operational = formed && isMultiblockOperational();
			refreshOperatingProfile(level, operational);
			if (formed && !operational) return haltProcessing(level, !multiblock.allowStandaloneOperation());
			if (!formed && !multiblock.allowStandaloneOperation()) return haltProcessing(level, true);
		}

		MachineRecipeProcessor.TickResult result = recipeProcessor.tick(level);
		setLit(level, result.status() == MachineRecipeProcessor.Status.RUNNING || result.crafted());
		return result;
	}

	public void resetProcessing () {
		if (recipeProcessor != null) recipeProcessor.reset();
		if (owner.getLevel() instanceof ServerLevel serverLevel) setLit(serverLevel, false);
	}

	public void onLoad () {
		refreshNetworkServices();
		if (owner.getLevel() != null && !owner.getLevel().isClientSide()) {
			if (definition.multiblock() != null && owner.getLevel() instanceof ServerLevel serverLevel && owner instanceof MultiblockController controller) {
				MultiblockHandler.onControllerLoaded(serverLevel, controller);
			}
			notifyExternalStateChanged();
		}
	}

	public void onRemoved () {
		if (networkNode != null) networkNode.getServices().clear();
	}

	public void save (ValueOutput output) {
		if (items != null) {
			items.serialize(output.child(ITEMS_TAG));
			if (items instanceof LockableItemResourceHandler lockable) lockable.serializeLockState(output.child(ITEM_LOCK_TAG));
		}
		if (fluidStorage != null) fluidStorage.serialize(output.child(FLUIDS_TAG));
		if (energy != null) energy.serialize(output.child(ENERGY_TAG));
		if (recipeProcessor != null) recipeProcessor.save(output.child(RECIPE_PROCESSOR_TAG));
		if (sideConfiguration != null) sideConfiguration.save(output.child(SIDE_CONFIGURATION_TAG));
		if (definition.multiblock() != null) {
			output.putBoolean(MultiblockController.FORMED_TAG, formed);
			multiblockUpgradeProfile.save(output.child(MULTIBLOCK_PROFILE_TAG));
			operatingUpgradeProfile.save(output.child(OPERATING_PROFILE_TAG));
			output.putDouble(MULTIBLOCK_SPEED_TAG, multiblockUpgradeProfile.processingSpeedMultiplier());
			output.putDouble(OPERATING_SPEED_TAG, getProcessingSpeedMultiplier());
		}
	}

	public void load (ValueInput input) {
		if (items != null) {
			items.deserialize(input.childOrEmpty(ITEMS_TAG));
			if (items instanceof LockableItemResourceHandler lockable) lockable.deserializeLockState(input.childOrEmpty(ITEM_LOCK_TAG));
		}
		if (fluidStorage != null) fluidStorage.deserialize(input.childOrEmpty(FLUIDS_TAG));
		if (energy != null) energy.deserialize(input.childOrEmpty(ENERGY_TAG));
		if (recipeProcessor != null) recipeProcessor.load(input.childOrEmpty(RECIPE_PROCESSOR_TAG));
		if (sideConfiguration != null) sideConfiguration.load(input.childOrEmpty(SIDE_CONFIGURATION_TAG));

		if (definition.multiblock() == null) return;
		formed = input.getBooleanOr(MultiblockController.FORMED_TAG, false);
		multiblockProfileResolved = !formed;
		MachineUpgradeProfile legacyMultiblock = legacyUpgradeProfile(input.getDoubleOr(MULTIBLOCK_SPEED_TAG, 1.0D));
		MachineUpgradeProfile legacyOperating = legacyUpgradeProfile(input.getDoubleOr(OPERATING_SPEED_TAG, 1.0D));
		multiblockUpgradeProfile = MachineUpgradeProfile.load(input.childOrEmpty(MULTIBLOCK_PROFILE_TAG), legacyMultiblock);
		operatingUpgradeProfile = MachineUpgradeProfile.load(input.childOrEmpty(OPERATING_PROFILE_TAG), legacyOperating);
		operatingPowerProfile = definition.energy() == null ? null : definition.energy().baseProfile().upgradedBy(operatingUpgradeProfile);
	}

	public ModelData getModelData () {
		return sideConfiguration == null ? ModelData.EMPTY : ConfigurableMachineModelData.create(sideConfiguration);
	}

	public void onClientDataLoaded () {
		owner.requestModelDataUpdate();
	}

	@Override
	public NetworkNode getNetworkNode () {
		if (networkNode == null) throw new IllegalStateException("Machine does not define network behavior");
		refreshNetworkServices();
		return networkNode;
	}

	@Override
	public boolean isNetworkEnabled () {
		if (networkNode == null || !directCapabilitiesEnabled()) return false;
		if (sideConfiguration == null) return true;
		for (MachineFace face : MachineFace.values()) {
			if (sideConfiguration.getMode(face) == MachineSideMode.NETWORK) return true;
		}
		return false;
	}

	public @Nullable INetworkInterface getNetworkCapability (@Nullable Direction side) {
		if (!isNetworkEnabled()) return null;
		return sideConfiguration == null || getSideMode(side) == MachineSideMode.NETWORK ? this : null;
	}

	private MachineDefinition.MultiblockSpec requireMultiblockSpec () {
		MachineDefinition.MultiblockSpec spec = definition.multiblock();
		if (spec == null) throw new IllegalStateException("Machine does not define multiblock behavior");
		return spec;
	}

	private boolean directCapabilitiesEnabled () {
		MachineDefinition.MultiblockSpec multiblock = definition.multiblock();
		return multiblock == null || !formed && multiblock.allowStandaloneOperation();
	}

	private ResourceIoMode itemIoMode (Direction side) {
		if (!directCapabilitiesEnabled()) return ResourceIoMode.NONE;
		if (sideConfiguration == null) return ResourceIoMode.BOTH;
		return switch (getSideMode(side)) {
			case ITEM_INPUT -> ResourceIoMode.INSERT;
			case ITEM_OUTPUT -> ResourceIoMode.EXTRACT;
			default -> ResourceIoMode.NONE;
		};
	}

	private ResourceIoMode fluidIoMode (Direction side) {
		if (!directCapabilitiesEnabled()) return ResourceIoMode.NONE;
		if (sideConfiguration == null) return ResourceIoMode.BOTH;
		return switch (getSideMode(side)) {
			case FLUID_INPUT -> ResourceIoMode.INSERT;
			case FLUID_OUTPUT -> ResourceIoMode.EXTRACT;
			default -> ResourceIoMode.NONE;
		};
	}

	private ResourceIoMode energyIoMode (Direction side) {
		if (!directCapabilitiesEnabled() || definition.energy() == null) return ResourceIoMode.NONE;
		if (sideConfiguration == null) return definition.energy().directMode();
		return getSideMode(side) == MachineSideMode.ENERGY_INPUT ? ResourceIoMode.INSERT : ResourceIoMode.NONE;
	}

	private void onSideConfigurationChanged (MachineFace face, MachineSideMode previous, MachineSideMode current) {
		owner.setChanged();
		owner.requestModelDataUpdate();
		if (owner.getLevel() == null || owner.getLevel().isClientSide()) return;
		refreshNetworkServices();
		notifyExternalStateChanged();
	}

	private void refreshNetworkServices () {
		if (networkNode == null) return;
		networkNode.getServices().clear();
		MachineDefinition.NetworkSpec spec = definition.network();
		if (spec != null && directCapabilitiesEnabled()) spec.registrar().register(this, networkNode.getServices());
	}

	private MachineRecipeProcessor.TickResult haltProcessing (ServerLevel level, boolean reset) {
		if (reset && recipeProcessor != null) recipeProcessor.reset();
		setLit(level, false);
		return new MachineRecipeProcessor.TickResult(getProcessingStatus(), false);
	}

	private void refreshOperatingProfile (ServerLevel level, boolean multiblockOperational) {
		MachineUpgradeProfile next = multiblockOperational ? multiblockUpgradeProfile : MachineUpgradeProfile.identity();
		if (next.equals(operatingUpgradeProfile)) return;
		operatingUpgradeProfile = next;
		operatingPowerProfile = definition.energy() == null ? null : definition.energy().baseProfile().upgradedBy(next);
		if (recipeProcessor != null) recipeProcessor.reset();
		setLit(level, false);
		owner.setChanged();
		refreshNetworkServices();
		notifyExternalStateChanged();
	}

	private void setLit (ServerLevel level, boolean lit) {
		if (!definition.hasLitState()) return;
		BlockState state = owner.getBlockState();
		if (!state.hasProperty(ILitProvider.LIT) || state.getValue(ILitProvider.LIT) == lit) return;
		level.setBlock(owner.getBlockPos(), state.setValue(ILitProvider.LIT, lit), Block.UPDATE_CLIENTS);
	}

	private void notifyExternalStateChanged () {
		if (owner.getLevel() == null) return;
		owner.getLevel().invalidateCapabilities(owner.getBlockPos());
		owner.getLevel().updateNeighborsAt(owner.getBlockPos(), owner.getBlockState().getBlock());
		BlockState state = owner.getBlockState();
		owner.getLevel().sendBlockUpdated(owner.getBlockPos(), state, state, Block.UPDATE_CLIENTS);
	}

	private static MachineUpgradeProfile legacyUpgradeProfile (double speed) {
		return Double.isFinite(speed) && speed > 0.0D ? MachineUpgradeProfile.speed(speed) : MachineUpgradeProfile.identity();
	}
}
