package general.api.screens.menu;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractMenu<B extends EntityBlock, T extends BlockEntity> extends AbstractContainerMenu {
	/** Reserved menu-button id used by {@link general.api.screens.screen.AbstractScreen}. */
	public static final int FILL_FLUID_CONTAINER_BUTTON = 0x47464C44; // "GFLD"

	@Getter
	private final B block;

	@Getter
	private final T blockEntity;

	@Getter
	private final ContainerData data;

	@Nullable
	private FluidContainerSource fluidContainerSource;

	public AbstractMenu (MenuType<?> type, int containerId, Inventory inventory, B block, T blockEntity) {
		this(type, containerId, inventory, block, blockEntity, new SimpleContainerData(0));
	}

	public AbstractMenu (MenuType<?> type, int containerId, Inventory inventory, B block, T blockEntity, ContainerData data) {
		super(type, containerId);
		this.block = Objects.requireNonNull(block, "block");
		this.blockEntity = Objects.requireNonNull(blockEntity, "blockEntity");
		this.data = Objects.requireNonNull(data, "data");

		addPlayerInventory(inventory);
		addPlayerHotbar(inventory);
		addContainerSlots();
		addDataSlots(data);
	}

	/**
	 * Returns the number of slots in the inventory.
	 * @return the number of slots in the inventory.
	 */
	public abstract int getSlotCount ();

	public abstract void addContainerSlots ();

	@Override
	public @NonNull ItemStack quickMoveStack (@NonNull Player player, int index) {
		if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
		Slot sourceSlot = slots.get(index);
		if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack original = sourceStack.copy();
		boolean moved = index < getContainerSlotStart()
				? movePlayerStackToContainer(sourceStack)
				: index < getContainerSlotEnd() && moveItemStackTo(sourceStack, 0, getContainerSlotStart(), true);
		if (!moved) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
		else sourceSlot.setChanged();
		if (sourceStack.getCount() == original.getCount()) return ItemStack.EMPTY;
		sourceSlot.onTake(player, sourceStack);
		return original;
	}

	/** First machine slot; player inventory and hotbar occupy the preceding 36 slots. */
	protected final int getContainerSlotStart () {
		return 36;
	}

	protected final int getContainerSlotEnd () {
		return getContainerSlotStart() + getSlotCount();
	}

	/**
	 * Shift-click target for player items. Machines with output-only or filtered
	 * slots should override this and target only their valid input ranges.
	 */
	protected boolean movePlayerStackToContainer (ItemStack stack) {
		return moveItemStackTo(stack, getContainerSlotStart(), getContainerSlotEnd(), false);
	}

	/**
	 * Enables the standard fluid-renderer interaction for this menu. A primary
	 * click fills one cursor-held fluid container with at most one bucket.
	 */
	protected final void setFluidContainerSource (ResourceHandler<FluidResource> handler, int tank) {
		setFluidContainerSource(handler, tank, FluidType.BUCKET_VOLUME);
	}

	/**
	 * Enables the standard fluid-renderer interaction with a custom per-click
	 * transfer limit. The destination must accept, and the source must provide,
	 * the complete selected amount or the transaction is rolled back.
	 */
	protected final void setFluidContainerSource (ResourceHandler<FluidResource> handler, int tank, int transferLimit) {
		Objects.requireNonNull(handler, "handler");
		Objects.checkIndex(tank, handler.size());
		if (transferLimit <= 0) throw new IllegalArgumentException("Fluid container transfer limit must be greater than zero");
		this.fluidContainerSource = new FluidContainerSource(handler, tank, transferLimit);
	}

	/** @return whether this menu has opted into fluid-renderer container filling. */
	public final boolean hasFluidContainerSource () {
		return fluidContainerSource != null;
	}

	@Override
	public boolean clickMenuButton (@NonNull Player player, int buttonId) {
		if (buttonId == FILL_FLUID_CONTAINER_BUTTON) return fillCarriedFluidContainer(player);
		return super.clickMenuButton(player, buttonId);
	}

	private boolean fillCarriedFluidContainer (Player player) {
		FluidContainerSource source = fluidContainerSource;
		if (source == null || player.level().isClientSide() || !stillValid(player) || getCarried().isEmpty()) return false;

		FluidResource resource = source.handler().getResource(source.tank());
		if (resource.isEmpty()) return false;

		var itemAccess = ItemAccess.forPlayerCursor(player, this).oneByOne();
		var destination = itemAccess.getCapability(Capabilities.Fluid.ITEM);
		if (destination == null) return false;

		try (Transaction transaction = Transaction.openRoot()) {
			int inserted = destination.insert(resource, source.transferLimit(), transaction);
			if (inserted <= 0) return false;

			int extracted = source.handler().extract(source.tank(), resource, inserted, transaction);
			if (extracted != inserted) return false;

			transaction.commit();
			return true;
		}
	}

	@Override
	public boolean stillValid (@NonNull Player player) {
		return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, blockEntity.getBlockState().getBlock());
	}

	/**
	 * Adds the player inventory.
	 * @param playerInventory the player inventory.
	 */
	public void addPlayerInventory (Inventory playerInventory) {
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, (8 + col * 18), 84 + row * 18));
			}
		}
	}

	/**
	 * Adds the player hotbar.
	 * @param playerInventory the player inventory.
	 */
	public void addPlayerHotbar (Inventory playerInventory) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, (8 + i * 18), 142));
		}
	}

	public @Nullable SimpleEnergyHandler getEnergyStorage () {
		return null;
	}

	private record FluidContainerSource(ResourceHandler<FluidResource> handler, int tank, int transferLimit) {
	}

	public static class QuickMoveStack {

		// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
		private static final int HOTBAR_SLOT_COUNT = 9;
		private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
		private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
		private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
		private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
		private static final int VANILLA_FIRST_SLOT_INDEX = 0;
		private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
		private final int slotCount;

		private final AbstractContainerMenu menu;
		private final Player                player;
		private final int                   index;

		public QuickMoveStack (AbstractContainerMenu menu, int slotCount, Player player, int index) {
			this.slotCount = slotCount;
			this.menu = menu;
			this.player = player;
			this.index = index;
		}

		public ItemStack move () {
			return move(player, index);
		}

		public ItemStack move (Player playerIn, int pIndex) {
			Slot sourceSlot = menu.slots.get(pIndex);
			if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
			ItemStack sourceStack = sourceSlot.getItem();
			ItemStack copyOfSourceStack = sourceStack.copy();

			if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
				int inputSlotsEnd = TE_INVENTORY_FIRST_SLOT_INDEX + slotCount;
				if (!menu.moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, inputSlotsEnd, false)) {
					return ItemStack.EMPTY;  // EMPTY_ITEM
				}
			} else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + slotCount) {
				if (!menu.moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
					return ItemStack.EMPTY;
				}
			} else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + slotCount + 4) {
				if (!menu.moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}

			if (sourceStack.getCount() == 0) {
				sourceSlot.set(ItemStack.EMPTY);
			} else {
				sourceSlot.setChanged();
			}
			sourceSlot.onTake(playerIn, sourceStack);
			return copyOfSourceStack;
		}

	}

}
