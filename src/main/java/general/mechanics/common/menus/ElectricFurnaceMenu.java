package general.mechanics.common.menus;

import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideMode;
import general.api.screens.menu.AbstractMenu;
import general.api.screens.menu.ContainerDataInts;
import general.api.screens.slot.MachineItemSlot;
import general.api.transfer.item.LockableItemResourceHandler;
import general.mechanics.common.block.entity.ElectricFurnaceBlockEntity;
import general.mechanics.common.block.machine.ElectricFurnaceBlock;
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

public class ElectricFurnaceMenu extends AbstractMenu<ElectricFurnaceBlock, ElectricFurnaceBlockEntity> {

	private static final int DATA_PROGRESS_LOW        = 0;
	private static final int DATA_PROGRESS_HIGH       = 1;
	private static final int DATA_MAX_PROGRESS_LOW    = 2;
	private static final int DATA_MAX_PROGRESS_HIGH   = 3;
	private static final int DATA_ENERGY_LOW          = 4;
	private static final int DATA_ENERGY_HIGH         = 5;
	private static final int DATA_ENERGY_CAPACITY_LOW = 6;
	private static final int DATA_ENERGY_CAPACITY_HIGH = 7;
	private static final int DATA_SIDE_MODE_START      = 8;
	private static final int DATA_COUNT           = DATA_SIDE_MODE_START + MachineFace.values().length;

	private static final int INPUT_X    = 56;
	private static final int INPUT_Y    = 35;
	private static final int CATALYST_X = 38; // Bottom
	private static final int CATALYST_Y = 53; // Bottom

	private static final int OUTPUT_LEFT_X  = 104;
	private static final int OUTPUT_RIGHT_X = 122;
	private static final int OUTPUT_TOP_Y   = 26;
	private static final int OUTPUT_BOTTOM_Y = 44;

	/** Client-side construction from the block position sent by the menu provider. */
	public ElectricFurnaceMenu (int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
		this(containerId, inventory, findBlockEntity(inventory, buffer), new SimpleContainerData(DATA_COUNT));
	}

	/** Server-side construction with live machine-backed synchronization data. */
	public ElectricFurnaceMenu (int containerId, Inventory inventory, ElectricFurnaceBlockEntity blockEntity) {
		this(containerId, inventory, blockEntity, createServerData(blockEntity));
	}

	private ElectricFurnaceMenu (int containerId, Inventory inventory, ElectricFurnaceBlockEntity blockEntity, ContainerData data) {
		super(GenMenus.ELECTRIC_FURNACE.get(), containerId, inventory, GenBlocks.ELECTRIC_FURNACE.get(), blockEntity, data, ElectricFurnaceBlock.getRecipeDefinition());
		checkContainerDataCount(data, DATA_COUNT);
		enableItemSlotLocking(blockEntity.getItemHandler());
		enableMachineSideConfiguration(ElectricFurnaceBlockEntity.SIDES, this::getSideMode, blockEntity::setSideMode);
	}

	@Override
	public int getSlotCount () {
		return ElectricFurnaceBlockEntity.ITEMS.size();
	}

	@Override
	public void addContainerSlots () {
		LockableItemResourceHandler handler = getBlockEntity().getItemHandler();
		addSlot(new MachineItemSlot(handler, handler::set, ElectricFurnaceBlockEntity.INPUT_SLOT, INPUT_X, INPUT_Y));
		addSlot(new MachineItemSlot(handler, handler::set, ElectricFurnaceBlockEntity.CATALYST_SLOT, CATALYST_X, CATALYST_Y));
		addOutputSlot(handler, ElectricFurnaceBlockEntity.OUTPUT_SLOT_1, OUTPUT_LEFT_X, OUTPUT_TOP_Y);
		addOutputSlot(handler, ElectricFurnaceBlockEntity.OUTPUT_SLOT_2, OUTPUT_RIGHT_X, OUTPUT_TOP_Y);
		addOutputSlot(handler, ElectricFurnaceBlockEntity.OUTPUT_SLOT_3, OUTPUT_LEFT_X, OUTPUT_BOTTOM_Y);
		addOutputSlot(handler, ElectricFurnaceBlockEntity.OUTPUT_SLOT_4, OUTPUT_RIGHT_X, OUTPUT_BOTTOM_Y);
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

	private void addOutputSlot (LockableItemResourceHandler handler, int handlerSlot, int x, int y) {
		addSlot(new ResourceHandlerSlot(handler, handler::set, handlerSlot, x, y) {
			@Override
			public boolean mayPlace (@NonNull ItemStack stack) {
				return false;
			}
		});
	}

	private int getInt (int lowIndex, int highIndex) {
		return ContainerDataInts.combineWords(getData().get(lowIndex), getData().get(highIndex));
	}

	private static ElectricFurnaceBlockEntity findBlockEntity (Inventory inventory, RegistryFriendlyByteBuf buffer) {
		Objects.requireNonNull(buffer, "Electric Furnace menu requires its block position");
		var pos = buffer.readBlockPos();
		var blockEntity = GenBlockEntities.ELECTRIC_FURNACE.getBlockEntity(inventory.player.level(), pos);
		if (blockEntity == null) throw new IllegalStateException("No Electric Furnace block entity at " + pos);
		return blockEntity;
	}

	private static ContainerData createServerData (ElectricFurnaceBlockEntity blockEntity) {
		return new ContainerData() {
			@Override
			public int get (int index) {
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
						MachineFace face = MachineFace.byId(faceId).orElseThrow(() -> new IndexOutOfBoundsException("Unknown Electric Furnace data index: " + index));
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
}
