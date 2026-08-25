package general.mechanics.common.menus;

import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideMode;
import general.api.screens.menu.AbstractMenu;
import general.api.screens.menu.ContainerDataInts;
import general.api.screens.slot.MachineItemSlot;
import general.api.transfer.item.LockableItemResourceHandler;
import general.mechanics.common.block.entity.StampingPressBlockEntity;
import general.mechanics.common.block.machine.StampingPressBlock;
import general.mechanics.registries.GenBlockEntities;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class StampingPressMenu extends AbstractMenu<StampingPressBlock, StampingPressBlockEntity> {

	private static final int DATA_PROGRESS_LOW         = 0;
	private static final int DATA_PROGRESS_HIGH        = 1;
	private static final int DATA_MAX_PROGRESS_LOW     = 2;
	private static final int DATA_MAX_PROGRESS_HIGH    = 3;
	private static final int DATA_ENERGY_LOW           = 4;
	private static final int DATA_ENERGY_HIGH          = 5;
	private static final int DATA_ENERGY_CAPACITY_LOW  = 6;
	private static final int DATA_ENERGY_CAPACITY_HIGH = 7;
	private static final int DATA_SIDE_MODE_START      = 8;
	private static final int DATA_FORMED               = DATA_SIDE_MODE_START + MachineFace.values().length;
	private static final int DATA_COUNT                = DATA_FORMED + 1;

	private static final int INPUT_X  = 56;
	private static final int INPUT_Y  = 35;
	private static final int DIE_X    = 80;
	private static final int DIE_Y    = 56;
	private static final int OUTPUT_X = 108;
	private static final int OUTPUT_Y = 35;

	public StampingPressMenu (int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
		this(containerId, inventory, findBlockEntity(inventory, buffer), true);
	}

	public StampingPressMenu (int containerId, Inventory inventory, StampingPressBlockEntity blockEntity) {
		this(containerId, inventory, blockEntity, createServerData(blockEntity));
	}

	private StampingPressMenu (int containerId, Inventory inventory, StampingPressBlockEntity blockEntity, boolean initializeClientData) {
		this(containerId, inventory, blockEntity, createClientData(blockEntity));
	}

	private StampingPressMenu (int containerId, Inventory inventory, StampingPressBlockEntity blockEntity, ContainerData data) {
		super(GenMenus.STAMPING_PRESS.get(), containerId, inventory, GenBlocks.STAMPING_PRESS.get(), blockEntity, data, StampingPressBlock.getRecipeDefinition());
		checkContainerDataCount(data, DATA_COUNT);
		enableItemSlotLocking(blockEntity.getItemHandler());
		enableMachineSideConfiguration(StampingPressBlockEntity.SIDES, this::getSideMode, blockEntity::setSideMode, () -> !isMultiblockFormed());
		enableFactoryPresentation(this::isMultiblockFormed);
	}

	@Override
	public int getSlotCount () {
		return StampingPressBlockEntity.ITEMS.size();
	}

	@Override
	public void addContainerSlots () {
		LockableItemResourceHandler handler = getBlockEntity().getItemHandler();
		addSlot(new MachineItemSlot(handler, handler::set, StampingPressBlockEntity.INPUT_SLOT, INPUT_X, INPUT_Y));
		addSlot(new MachineItemSlot(handler, handler::set, StampingPressBlockEntity.DIE_SLOT, DIE_X, DIE_Y));
		addSlot(new ResourceHandlerSlot(handler, handler::set, StampingPressBlockEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
			@Override
			public boolean mayPlace (@NonNull ItemStack stack) {
				return false;
			}
		});
	}

	@Override
	protected boolean movePlayerStackToContainer (ItemStack stack) {
		return moveItemStackTo(stack, getContainerSlotStart(), getContainerSlotStart() + 2, false);
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

	private static StampingPressBlockEntity findBlockEntity (Inventory inventory, RegistryFriendlyByteBuf buffer) {
		Objects.requireNonNull(buffer, "Stamping Press menu requires its block position");
		var pos = buffer.readBlockPos();
		var blockEntity = GenBlockEntities.STAMPING_PRESS.getBlockEntity(inventory.player.level(), pos);
		if (blockEntity == null) throw new IllegalStateException("No Stamping Press block entity at " + pos);
		return blockEntity;
	}

	private static ContainerData createServerData (StampingPressBlockEntity blockEntity) {
		return new ContainerData() {
			@Override
			public int get (int index) {
				if (index == DATA_FORMED) return blockEntity.isMultiblockFormed() ? 1 : 0;
				return switch (index) {
					case DATA_PROGRESS_LOW -> ContainerDataInts.lowWord(blockEntity.getProgress());
					case DATA_PROGRESS_HIGH -> ContainerDataInts.highWord(blockEntity.getProgress());
					case DATA_MAX_PROGRESS_LOW -> ContainerDataInts.lowWord(blockEntity.getMaxProgress());
					case DATA_MAX_PROGRESS_HIGH -> ContainerDataInts.highWord(blockEntity.getMaxProgress());
					case DATA_ENERGY_LOW -> ContainerDataInts.lowWord(blockEntity.getEnergyStored());
					case DATA_ENERGY_HIGH -> ContainerDataInts.highWord(blockEntity.getEnergyStored());
					case DATA_ENERGY_CAPACITY_LOW -> ContainerDataInts.lowWord(blockEntity.getEnergyCapacity());
					case DATA_ENERGY_CAPACITY_HIGH -> ContainerDataInts.highWord(blockEntity.getEnergyCapacity());
					default -> {
						int faceId = index - DATA_SIDE_MODE_START;
						MachineFace face = MachineFace.byId(faceId).orElseThrow(() -> new IndexOutOfBoundsException("Unknown Stamping Press data index: " + index));
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

	private static ContainerData createClientData (StampingPressBlockEntity blockEntity) {
		SimpleContainerData data = new SimpleContainerData(DATA_COUNT);
		data.set(DATA_PROGRESS_LOW, ContainerDataInts.lowWord(blockEntity.getProgress()));
		data.set(DATA_PROGRESS_HIGH, ContainerDataInts.highWord(blockEntity.getProgress()));
		data.set(DATA_MAX_PROGRESS_LOW, ContainerDataInts.lowWord(blockEntity.getMaxProgress()));
		data.set(DATA_MAX_PROGRESS_HIGH, ContainerDataInts.highWord(blockEntity.getMaxProgress()));
		data.set(DATA_ENERGY_LOW, ContainerDataInts.lowWord(blockEntity.getEnergyStored()));
		data.set(DATA_ENERGY_HIGH, ContainerDataInts.highWord(blockEntity.getEnergyStored()));
		data.set(DATA_ENERGY_CAPACITY_LOW, ContainerDataInts.lowWord(blockEntity.getEnergyCapacity()));
		data.set(DATA_ENERGY_CAPACITY_HIGH, ContainerDataInts.highWord(blockEntity.getEnergyCapacity()));
		for (MachineFace face : MachineFace.values()) data.set(DATA_SIDE_MODE_START + face.id(), blockEntity.getSideMode(face).id());
		data.set(DATA_FORMED, blockEntity.isMultiblockFormed() ? 1 : 0);
		return data;
	}
}
