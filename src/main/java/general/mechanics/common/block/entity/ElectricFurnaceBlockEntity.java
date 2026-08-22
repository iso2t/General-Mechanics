package general.mechanics.common.block.entity;

import general.api.block.entity.BaseBlockEntity;
import general.api.capabilities.GeneralCapabilities;
import general.api.crafting.MachineEnergyWorkRequirement;
import general.api.crafting.MachineRecipeProcessor;
import general.api.crafting.MachineRecipeSources;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfiguration;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import general.api.machine.power.MachinePowerProfile;
import general.api.network.INetworkInterface;
import general.api.network.NetworkNode;
import general.api.network.NetworkServices;
import general.api.transfer.ResourceAccess;
import general.api.transfer.ResourceAccessPolicy;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.RestrictedResourceHandler;
import general.api.transfer.SidedResourceHandlers;
import general.api.transfer.energy.SidedEnergyHandlers;
import general.api.transfer.energy.SidedEnergyResourceProvider;
import general.api.transfer.item.ItemInventoryDefinition;
import general.api.transfer.item.LockableItemResourceHandler;
import general.api.transfer.item.SidedItemResourceProvider;
import general.mechanics.common.block.machine.ElectricFurnaceBlock;
import general.mechanics.common.network.NetworkConnectorServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.LimitingEnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Persistent storage and dynamically configured external services for the
 * standalone Electric Furnace.
 */
public class ElectricFurnaceBlockEntity extends BaseBlockEntity implements SidedItemResourceProvider, SidedEnergyResourceProvider, INetworkInterface {

	private static final String ITEMS_TAG              = "items";
	private static final String ITEM_LOCK_TAG          = "item_lock";
	private static final String ENERGY_TAG             = "energy";
	private static final String RECIPE_PROCESSOR_TAG   = "recipe_processor";
	private static final String SIDE_CONFIGURATION_TAG = "side_configuration";

	public static final MachinePowerProfile POWER_PROFILE = MachinePowerProfile.base(100_000, 10_000, 20);

	public static final ItemInventoryDefinition ITEMS = ItemInventoryDefinition.builder()
			.input(ElectricFurnaceBlock.RecipeSlots.INPUT)
			.input(ElectricFurnaceBlock.RecipeSlots.CATALYST)
			.output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1)
			.output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_2)
			.output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_3)
			.output(ElectricFurnaceBlock.RecipeSlots.OUTPUT_4)
			.build();

	public static final int INPUT_SLOT    = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.INPUT);
	public static final int CATALYST_SLOT = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.CATALYST);
	public static final int OUTPUT_SLOT_1 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1);
	public static final int OUTPUT_SLOT_2 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_2);
	public static final int OUTPUT_SLOT_3 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_3);
	public static final int OUTPUT_SLOT_4 = ITEMS.index(ElectricFurnaceBlock.RecipeSlots.OUTPUT_4);

	/**
	 * Shared by every Electric Furnace. Only its relevant machine modes are supported,
	 * and the machine front is permanently disabled.
	 */
	public static final MachineSideConfigurationDefinition SIDES = MachineSideConfigurationDefinition.builder()
			.allow(MachineSideMode.ITEM_INPUT, MachineSideMode.ITEM_OUTPUT, MachineSideMode.ENERGY_INPUT, MachineSideMode.NETWORK)
			.lock(MachineFace.FRONT)
			.defaultMode(MachineFace.LEFT, MachineSideMode.ITEM_INPUT)
			.defaultMode(MachineFace.RIGHT, MachineSideMode.ITEM_OUTPUT)
			.defaultMode(MachineFace.BACK, MachineSideMode.ENERGY_INPUT)
			.defaultMode(MachineFace.TOP, MachineSideMode.NETWORK)
			.build();

	private static final ResourceAccessPolicy<ItemResource> ITEM_INPUT_ACCESS = ITEMS.access()
			.insert(ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.CATALYST)
			.build();

	private static final ResourceAccessPolicy<ItemResource> ITEM_OUTPUT_ACCESS = ITEMS.access()
			.extract(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, ElectricFurnaceBlock.RecipeSlots.OUTPUT_2, ElectricFurnaceBlock.RecipeSlots.OUTPUT_3, ElectricFurnaceBlock.RecipeSlots.OUTPUT_4)
			.build();

	private static final ResourceAccessPolicy<ItemResource> NETWORK_ITEM_ACCESS = ITEMS.access()
			.insert(ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.CATALYST)
			.extract(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, ElectricFurnaceBlock.RecipeSlots.OUTPUT_2, ElectricFurnaceBlock.RecipeSlots.OUTPUT_3, ElectricFurnaceBlock.RecipeSlots.OUTPUT_4)
			.build();

	private final MachineSideConfiguration            sideConfiguration;
	private final LockableItemResourceHandler         items;
	private final SidedResourceHandlers<ItemResource> sidedItems;
	private final ResourceHandler<ItemResource>       networkItems;
	private final SimpleEnergyHandler                 energy;
	private final EnergyHandler                       networkEnergyInput;
	private final SidedEnergyHandlers                 sidedEnergy;
	private final NetworkNode                         networkNode;
	private final MachineRecipeProcessor              recipeProcessor;

	public ElectricFurnaceBlockEntity (BlockEntityType<ElectricFurnaceBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.sideConfiguration = SIDES.createConfiguration(this::onSideConfigurationChanged);
		this.items = ITEMS.createLockableHandler(this::setChanged, INPUT_SLOT, CATALYST_SLOT);
		this.networkItems = new RestrictedResourceHandler<>(items, NETWORK_ITEM_ACCESS);

		var itemViews = SidedResourceHandlers.builder(items);
		for (Direction side : Direction.values()) {
			itemViews.side(side, ResourceAccess.switching(ITEM_INPUT_ACCESS, ITEM_OUTPUT_ACCESS, () -> itemIoMode(side)));
		}
		this.sidedItems = itemViews.build();

		this.energy = new SimpleEnergyHandler(POWER_PROFILE.capacity(), POWER_PROFILE.maxInput(), POWER_PROFILE.capacity()) {
			@Override
			protected void onEnergyChanged (int amount) {
				setChanged();
			}
		};
		this.networkEnergyInput = new LimitingEnergyHandler(energy, POWER_PROFILE.maxInput(), 0);

		var energyViews = SidedEnergyHandlers.builder(energy);
		for (Direction side : Direction.values()) {
			energyViews.side(side, () -> energyIoMode(side));
		}
		this.sidedEnergy = energyViews.build();

		var energyWork = MachineEnergyWorkRequirement.fromProfile(energy, this::getPowerProfile);
		this.recipeProcessor = ElectricFurnaceBlock.getRecipeDefinition().bind(items).processor(List.of(
				MachineRecipeSources.registered(ElectricFurnaceBlock.getRecipeDefinition()),
				MachineRecipeSources.cooking(ElectricFurnaceBlock.getRecipeDefinition(), RecipeType.SMELTING, ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, input -> input.item(ElectricFurnaceBlock.RecipeSlots.CATALYST).isEmpty())
		), energyWork, this::setChanged);

		this.networkNode = new NetworkNode("ElectricFurnace");
		registerNetworkServices();
	}

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<ElectricFurnaceBlockEntity> type) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, type, ElectricFurnaceBlockEntity::getItemCapability);
		event.registerBlockEntity(Capabilities.Energy.BLOCK, type, ElectricFurnaceBlockEntity::getEnergyCapability);
		event.registerBlockEntity(GeneralCapabilities.NETWORK_HANDLER_BLOCK, type, ElectricFurnaceBlockEntity::getNetworkCapability);
	}

	public MachineSideConfiguration getSideConfiguration () {
		return sideConfiguration;
	}

	public MachineSideMode getSideMode (MachineFace face) {
		return sideConfiguration.getMode(face);
	}

	public MachineSideMode getSideMode (@Nullable Direction side) {
		return side == null ? MachineSideMode.NONE : sideConfiguration.getMode(getMachineFront(), side);
	}

	/**
	 * Server-authoritative side mutation used by the future configuration menu.
	 */
	public boolean setSideMode (MachineFace face, MachineSideMode mode) {
		if (level != null && level.isClientSide()) return false;
		return sideConfiguration.setMode(face, mode);
	}

	public Direction getMachineFront () {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	public MachinePowerProfile getPowerProfile () {
		return POWER_PROFILE;
	}

	public MachineRecipeProcessor getRecipeProcessor () {
		return recipeProcessor;
	}

	public @Nullable ResourceHandler<ItemResource> getItemCapability (@Nullable Direction side) {
		return getSideMode(side).isItem() ? getItemHandler(side) : null;
	}

	@Override
	public LockableItemResourceHandler getItemHandler () {
		return items;
	}

	@Override
	public ItemInventoryDefinition getItemDefinition () {
		return ITEMS;
	}

	@Override
	public SidedResourceHandlers<ItemResource> getSidedItemHandlers () {
		return sidedItems;
	}

	public @Nullable EnergyHandler getEnergyCapability (@Nullable Direction side) {
		return getEnergyHandler(side);
	}

	@Override
	public SimpleEnergyHandler getEnergyHandler () {
		return energy;
	}

	@Override
	public SidedEnergyHandlers getSidedEnergyHandlers () {
		return sidedEnergy;
	}

	public @Nullable INetworkInterface getNetworkCapability (@Nullable Direction side) {
		return getSideMode(side) == MachineSideMode.NETWORK ? this : null;
	}

	@Override
	public NetworkNode getNetworkNode () {
		registerNetworkServices();
		return networkNode;
	}

	@Override
	public boolean isNetworkEnabled () {
		for (MachineFace face : MachineFace.values()) {
			if (sideConfiguration.getMode(face) == MachineSideMode.NETWORK) return true;
		}
		return false;
	}

	@Override
	public void onLoad () {
		super.onLoad();
		registerNetworkServices();
		if (level != null && !level.isClientSide()) {
			level.invalidateCapabilities(worldPosition);
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
	}

	@Override
	public void setRemoved () {
		networkNode.getServices().clear();
		super.setRemoved();
	}

	@Override
	protected void saveAdditional (@NonNull ValueOutput output) {
		super.saveAdditional(output);
		items.serialize(output.child(ITEMS_TAG));
		items.serializeLockState(output.child(ITEM_LOCK_TAG));
		energy.serialize(output.child(ENERGY_TAG));
		recipeProcessor.save(output.child(RECIPE_PROCESSOR_TAG));
		sideConfiguration.save(output.child(SIDE_CONFIGURATION_TAG));
	}

	@Override
	protected void loadAdditional (@NonNull ValueInput input) {
		super.loadAdditional(input);
		items.deserialize(input.childOrEmpty(ITEMS_TAG));
		items.deserializeLockState(input.childOrEmpty(ITEM_LOCK_TAG));
		energy.deserialize(input.childOrEmpty(ENERGY_TAG));
		recipeProcessor.load(input.childOrEmpty(RECIPE_PROCESSOR_TAG));
		sideConfiguration.load(input.childOrEmpty(SIDE_CONFIGURATION_TAG));
	}

	private ResourceIoMode itemIoMode (Direction side) {
		return switch (getSideMode(side)) {
			case ITEM_INPUT -> ResourceIoMode.INSERT;
			case ITEM_OUTPUT -> ResourceIoMode.EXTRACT;
			default -> ResourceIoMode.NONE;
		};
	}

	private ResourceIoMode energyIoMode (Direction side) {
		return getSideMode(side) == MachineSideMode.ENERGY_INPUT ? ResourceIoMode.INSERT : ResourceIoMode.NONE;
	}

	private void onSideConfigurationChanged (MachineFace face, MachineSideMode previous, MachineSideMode current) {
		setChanged();
		if (level == null) return;

		level.invalidateCapabilities(worldPosition);
		level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		BlockState state = getBlockState();
		level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
	}

	private void registerNetworkServices () {
		networkNode.getServices().register(NetworkServices.ITEM, NetworkConnectorServices.itemService(() -> networkItems));
		networkNode.getServices().register(NetworkServices.ENERGY, NetworkConnectorServices.energyService(() -> networkEnergyInput));
	}
}
