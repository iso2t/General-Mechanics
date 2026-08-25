package general.mechanics.common.menus;

import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideMode;
import general.api.screens.menu.AbstractMenu;
import general.api.screens.menu.ContainerDataInts;
import general.api.screens.slot.MachineItemSlot;
import general.api.transfer.item.LockableItemResourceHandler;
import general.mechanics.common.block.entity.FluidInfuserBlockEntity;
import general.mechanics.common.block.machine.FluidInfuserBlock;
import general.mechanics.registries.GenBlockEntities;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenMenus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class FluidInfuserMenu extends AbstractMenu<FluidInfuserBlock, FluidInfuserBlockEntity> {

	private static final int DATA_PROGRESS_LOW         = 0;
	private static final int DATA_PROGRESS_HIGH        = 1;
	private static final int DATA_MAX_PROGRESS_LOW     = 2;
	private static final int DATA_MAX_PROGRESS_HIGH    = 3;
	private static final int DATA_ENERGY_LOW           = 4;
	private static final int DATA_ENERGY_HIGH          = 5;
	private static final int DATA_ENERGY_CAPACITY_LOW  = 6;
	private static final int DATA_ENERGY_CAPACITY_HIGH = 7;
	private static final int DATA_FLUID_ID             = 8;
	private static final int DATA_FLUID_AMOUNT_LOW     = 9;
	private static final int DATA_FLUID_AMOUNT_HIGH    = 10;
	private static final int DATA_FLUID_CAPACITY_LOW   = 11;
	private static final int DATA_FLUID_CAPACITY_HIGH  = 12;
	private static final int DATA_SIDE_MODE_START      = 13;
	private static final int DATA_FORMED               = DATA_SIDE_MODE_START + MachineFace.values().length;
	private static final int DATA_COUNT                = DATA_FORMED + 1;

	private static final int INPUT_X  = 108;
	private static final int INPUT_Y  = 9;
	private static final int OUTPUT_X = 108;
	private static final int OUTPUT_Y = 57;

	public FluidInfuserMenu (int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
		this(containerId, inventory, findBlockEntity(inventory, buffer), true);
	}

	public FluidInfuserMenu (int containerId, Inventory inventory, FluidInfuserBlockEntity blockEntity) {
		this(containerId, inventory, blockEntity, createServerData(blockEntity));
	}

	private FluidInfuserMenu (int containerId, Inventory inventory, FluidInfuserBlockEntity blockEntity, boolean initializeClientData) {
		this(containerId, inventory, blockEntity, createClientData(blockEntity));
	}

	private FluidInfuserMenu (int containerId, Inventory inventory, FluidInfuserBlockEntity blockEntity, ContainerData data) {
		super(GenMenus.FLUID_INFUSER.get(), containerId, inventory, GenBlocks.FLUID_INFUSER.get(), blockEntity, data);
		checkContainerDataCount(data, DATA_COUNT);
		setFluidContainerTarget(blockEntity.getFluidHandler(), FluidInfuserBlockEntity.FLUID_TANK);
		enableItemSlotLocking(blockEntity.getItemHandler());
		enableMachineSideConfiguration(FluidInfuserBlockEntity.SIDES, this::getSideMode, blockEntity::setSideMode, () -> !isMultiblockFormed());
		enableFactoryPresentation(this::isMultiblockFormed);
	}

	@Override
	public int getSlotCount () {
		return FluidInfuserBlockEntity.ITEMS.size();
	}

	@Override
	public void addContainerSlots () {
		LockableItemResourceHandler handler = getBlockEntity().getItemHandler();
		addSlot(new MachineItemSlot(handler, handler::set, FluidInfuserBlockEntity.INPUT_SLOT, INPUT_X, INPUT_Y));
		addSlot(new ResourceHandlerSlot(handler, handler::set, FluidInfuserBlockEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
			@Override
			public boolean mayPlace (@NonNull ItemStack stack) {
				return false;
			}
		});
	}

	@Override
	protected boolean movePlayerStackToContainer (ItemStack stack) {
		return moveItemStackTo(stack, getContainerSlotStart(), getContainerSlotStart() + 1, false);
	}

	public int getProgress () {
		return getInt(DATA_PROGRESS_LOW, DATA_PROGRESS_HIGH);
	}

	public int getMaxProgress () {
		return getInt(DATA_MAX_PROGRESS_LOW, DATA_MAX_PROGRESS_HIGH);
	}

	public int getEnergyStored () {
		return getInt(DATA_ENERGY_LOW, DATA_ENERGY_HIGH);
	}

	public int getEnergyCapacity () {
		return getInt(DATA_ENERGY_CAPACITY_LOW, DATA_ENERGY_CAPACITY_HIGH);
	}

	public FluidStack getFluidStack () {
		int amount = getInt(DATA_FLUID_AMOUNT_LOW, DATA_FLUID_AMOUNT_HIGH);
		if (amount <= 0) return FluidStack.EMPTY;
		var fluid = BuiltInRegistries.FLUID.byId(getData().get(DATA_FLUID_ID));
		return fluid == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(fluid, amount);
	}

	public int getFluidCapacity () {
		return getInt(DATA_FLUID_CAPACITY_LOW, DATA_FLUID_CAPACITY_HIGH);
	}

	public MachineSideMode getSideMode (MachineFace face) {
		Objects.requireNonNull(face, "face");
		return MachineSideMode.byId(getData().get(DATA_SIDE_MODE_START + face.id())).orElse(MachineSideMode.NONE);
	}

	public boolean isMultiblockFormed () {
		return getData().get(DATA_FORMED) != 0;
	}

	private int getInt (int lowIndex, int highIndex) {
		return ContainerDataInts.combineWords(getData().get(lowIndex), getData().get(highIndex));
	}

	private static FluidInfuserBlockEntity findBlockEntity (Inventory inventory, RegistryFriendlyByteBuf buffer) {
		Objects.requireNonNull(buffer, "Fluid Infuser menu requires its block position");
		var pos = buffer.readBlockPos();
		var blockEntity = GenBlockEntities.FLUID_INFUSER.getBlockEntity(inventory.player.level(), pos);
		if (blockEntity == null) throw new IllegalStateException("No Fluid Infuser block entity at " + pos);
		return blockEntity;
	}

	private static ContainerData createServerData (FluidInfuserBlockEntity blockEntity) {
		return new ContainerData() {
			@Override
			public int get (int index) {
				if (index == DATA_FORMED) return blockEntity.isMultiblockFormed() ? 1 : 0;
				return switch (index) {
					case DATA_PROGRESS_LOW -> ContainerDataInts.lowWord(blockEntity.getInfusionProgress());
					case DATA_PROGRESS_HIGH -> ContainerDataInts.highWord(blockEntity.getInfusionProgress());
					case DATA_MAX_PROGRESS_LOW -> ContainerDataInts.lowWord(blockEntity.getInfusionDuration());
					case DATA_MAX_PROGRESS_HIGH -> ContainerDataInts.highWord(blockEntity.getInfusionDuration());
					case DATA_ENERGY_LOW -> ContainerDataInts.lowWord(blockEntity.getEnergyStored());
					case DATA_ENERGY_HIGH -> ContainerDataInts.highWord(blockEntity.getEnergyStored());
					case DATA_ENERGY_CAPACITY_LOW -> ContainerDataInts.lowWord(blockEntity.getEnergyCapacity());
					case DATA_ENERGY_CAPACITY_HIGH -> ContainerDataInts.highWord(blockEntity.getEnergyCapacity());
					case DATA_FLUID_ID -> {
						FluidStack fluid = blockEntity.getFluidStack();
						yield fluid.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluid.getFluid());
					}
					case DATA_FLUID_AMOUNT_LOW -> ContainerDataInts.lowWord(blockEntity.getFluidHandler().getAmountAsInt(FluidInfuserBlockEntity.FLUID_TANK));
					case DATA_FLUID_AMOUNT_HIGH -> ContainerDataInts.highWord(blockEntity.getFluidHandler().getAmountAsInt(FluidInfuserBlockEntity.FLUID_TANK));
					case DATA_FLUID_CAPACITY_LOW -> ContainerDataInts.lowWord(blockEntity.getFluidCapacity());
					case DATA_FLUID_CAPACITY_HIGH -> ContainerDataInts.highWord(blockEntity.getFluidCapacity());
					default -> {
						int faceId = index - DATA_SIDE_MODE_START;
						MachineFace face = MachineFace.byId(faceId).orElseThrow(() -> new IndexOutOfBoundsException("Unknown Fluid Infuser data index: " + index));
						yield blockEntity.getSideMode(face).id();
					}
				};
			}

			@Override
			public void set (int index, int value) {
				// Server values are authoritative and read directly from the block entity.
			}

			@Override
			public int getCount () {
				return DATA_COUNT;
			}
		};
	}

	private static ContainerData createClientData (FluidInfuserBlockEntity blockEntity) {
		SimpleContainerData data = new SimpleContainerData(DATA_COUNT);
		data.set(DATA_PROGRESS_LOW, ContainerDataInts.lowWord(blockEntity.getInfusionProgress()));
		data.set(DATA_PROGRESS_HIGH, ContainerDataInts.highWord(blockEntity.getInfusionProgress()));
		data.set(DATA_MAX_PROGRESS_LOW, ContainerDataInts.lowWord(blockEntity.getInfusionDuration()));
		data.set(DATA_MAX_PROGRESS_HIGH, ContainerDataInts.highWord(blockEntity.getInfusionDuration()));
		data.set(DATA_ENERGY_LOW, ContainerDataInts.lowWord(blockEntity.getEnergyStored()));
		data.set(DATA_ENERGY_HIGH, ContainerDataInts.highWord(blockEntity.getEnergyStored()));
		data.set(DATA_ENERGY_CAPACITY_LOW, ContainerDataInts.lowWord(blockEntity.getEnergyCapacity()));
		data.set(DATA_ENERGY_CAPACITY_HIGH, ContainerDataInts.highWord(blockEntity.getEnergyCapacity()));
		FluidStack fluid = blockEntity.getFluidStack();
		data.set(DATA_FLUID_ID, fluid.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluid.getFluid()));
		data.set(DATA_FLUID_AMOUNT_LOW, ContainerDataInts.lowWord(fluid.getAmount()));
		data.set(DATA_FLUID_AMOUNT_HIGH, ContainerDataInts.highWord(fluid.getAmount()));
		data.set(DATA_FLUID_CAPACITY_LOW, ContainerDataInts.lowWord(blockEntity.getFluidCapacity()));
		data.set(DATA_FLUID_CAPACITY_HIGH, ContainerDataInts.highWord(blockEntity.getFluidCapacity()));
		for (MachineFace face : MachineFace.values()) data.set(DATA_SIDE_MODE_START + face.id(), blockEntity.getSideMode(face).id());
		data.set(DATA_FORMED, blockEntity.isMultiblockFormed() ? 1 : 0);
		return data;
	}
}
