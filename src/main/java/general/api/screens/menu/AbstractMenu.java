package general.api.screens.menu;

import general.api.crafting.MachineRecipeDefinition;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.machine.config.MachineSideMode;
import general.api.screens.screen.AbstractScreen;
import general.api.screens.slot.MachineItemSlot;
import general.api.transfer.fluid.FluidContainerTransfers;
import general.api.transfer.item.LockableItemResourceHandler;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.world.SimpleContainer;
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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractMenu<B extends EntityBlock, T extends BlockEntity> extends AbstractContainerMenu {
	/**
	 * Reserved menu-button id used by {@link AbstractScreen}.
	 */
	public static final  int TRANSFER_FLUID_CONTAINER_BUTTON = 0x47464C44; // "GFLD"
	/**
	 * @deprecated Use {@link #TRANSFER_FLUID_CONTAINER_BUTTON}; fluid-bar interactions
	 * may now move fluid in either direction.
	 */
	@Deprecated(forRemoval = false)
	public static final  int FILL_FLUID_CONTAINER_BUTTON     = TRANSFER_FLUID_CONTAINER_BUTTON;
	public static final  int TOGGLE_ITEM_LOCK_BUTTON         = 0x474C4F43; // "GLOC"
	private static final int CONFIGURE_SIDE_BUTTON_PREFIX    = 0x47530000; // "GS"
	private static final int CONFIGURE_SIDE_BUTTON_MASK      = 0xFFFF0000;

	@Getter
	private final B block;

	@Getter
	private final T blockEntity;

	@Getter
	private final ContainerData data;

	private final List<MachineRecipeDefinition<?>> recipeDefinitions;

	@Nullable
	private FluidContainerSource fluidContainerSource;

	@Nullable
	private FluidContainerTarget fluidContainerTarget;

	@Nullable
	private LockableItemResourceHandler lockableItemHandler;

	@Nullable
	private MachineSideConfigurationSource sideConfigurationSource;
	@Nullable
	private BooleanSupplier                factoryPresentationState;

	private boolean itemSlotsLocked;

	public AbstractMenu (MenuType<?> type, int containerId, Inventory inventory, B block, T blockEntity) {
		this(type, containerId, inventory, block, blockEntity, new SimpleContainerData(0));
	}

	public AbstractMenu (MenuType<?> type, int containerId, Inventory inventory, B block, T blockEntity, ContainerData data) {
		this(type, containerId, inventory, block, blockEntity, data, new MachineRecipeDefinition<?>[0]);
	}

	/**
	 * Creates a menu with recipe types exposed to compatible recipe viewers.
	 * Definitions are retained in declaration order and duplicate definitions are
	 * rejected. Supplying no definitions disables the shared recipe-viewer button.
	 */
	public AbstractMenu (MenuType<?> type, int containerId, Inventory inventory, B block, T blockEntity, ContainerData data, MachineRecipeDefinition<?>... recipeDefinitions) {
		super(type, containerId);
		this.block = Objects.requireNonNull(block, "block");
		this.blockEntity = Objects.requireNonNull(blockEntity, "blockEntity");
		this.data = Objects.requireNonNull(data, "data");
		Objects.requireNonNull(recipeDefinitions, "recipeDefinitions");
		var uniqueDefinitions = new LinkedHashSet<MachineRecipeDefinition<?>>();
		for (MachineRecipeDefinition<?> definition : recipeDefinitions) {
			if (!uniqueDefinitions.add(Objects.requireNonNull(definition, "recipeDefinition"))) {
				throw new IllegalArgumentException("Duplicate menu recipe definition '" + definition.id() + "'");
			}
		}
		this.recipeDefinitions = List.copyOf(uniqueDefinitions);

		addPlayerInventory(inventory);
		addPlayerHotbar(inventory);
		addContainerSlots();
		addDataSlots(data);
	}

	/**
	 * Recipe definitions associated with this menu, in recipe-viewer display order.
	 */
	public final List<MachineRecipeDefinition<?>> getRecipeDefinitions () {
		return recipeDefinitions;
	}

	public final boolean hasRecipeDefinitions () {
		return !recipeDefinitions.isEmpty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 *
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
		boolean moved = index < getContainerSlotStart() ? movePlayerStackToContainer(sourceStack) : index < getContainerSlotEnd() && moveItemStackTo(sourceStack, 0, getContainerSlotStart(), true);
		if (!moved) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
		else sourceSlot.setChanged();
		if (sourceStack.getCount() == original.getCount()) return ItemStack.EMPTY;
		sourceSlot.onTake(player, sourceStack);
		return original;
	}

	/**
	 * First machine slot; player inventory and hotbar occupy the preceding 36 slots.
	 */
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

	/**
	 * Enables depositing fluid from the cursor-held container into this menu's
	 * tank. A successful click transfers exactly one bucket.
	 */
	protected final void setFluidContainerTarget (ResourceHandler<FluidResource> handler, int tank) {
		setFluidContainerTarget(handler, tank, FluidType.BUCKET_VOLUME);
	}

	/**
	 * Enables depositing an exact custom amount from the cursor-held container.
	 * Both the item extraction and tank insertion share one root transaction, so
	 * incompatible fluids, insufficient contents, or insufficient tank space leave
	 * both sides unchanged.
	 */
	protected final void setFluidContainerTarget (ResourceHandler<FluidResource> handler, int tank, int transferAmount) {
		Objects.requireNonNull(handler, "handler");
		Objects.checkIndex(tank, handler.size());
		if (transferAmount <= 0) throw new IllegalArgumentException("Fluid container transfer amount must be greater than zero");
		this.fluidContainerTarget = new FluidContainerTarget(handler, tank, transferAmount);
	}

	/**
	 * Enables the shared machine input lock for every {@link MachineItemSlot} in
	 * this menu. The handler is authoritative on the server; hidden inactive slots
	 * synchronize full ghost item identities to the client without adding custom
	 * packets.
	 */
	protected final void enableItemSlotLocking (LockableItemResourceHandler handler) {
		Objects.requireNonNull(handler, "handler");
		if (lockableItemHandler != null) throw new IllegalStateException("Item slot locking is already enabled for this menu");

		List<MachineItemSlot> machineSlots = slots.subList(getContainerSlotStart(), getContainerSlotEnd()).stream().filter(MachineItemSlot.class::isInstance).map(MachineItemSlot.class::cast).toList();
		if (machineSlots.isEmpty()) throw new IllegalStateException("Item slot locking requires at least one MachineItemSlot");
		Set<Integer> configuredSlots = new LinkedHashSet<>();
		for (MachineItemSlot slot : machineSlots) {
			if (slot.getResourceHandler() != handler) throw new IllegalArgumentException("MachineItemSlot must use the lockable handler supplied to its menu");
			int handlerSlot = slot.getSlotIndex();
			if (!handler.isLockableSlot(handlerSlot)) throw new IllegalArgumentException("MachineItemSlot references non-lockable handler slot " + handlerSlot);
			if (!configuredSlots.add(handlerSlot)) throw new IllegalArgumentException("Duplicate MachineItemSlot for lockable handler slot " + handlerSlot);
		}
		if (configuredSlots.size() != handler.getLockableSlotCount()) {
			throw new IllegalArgumentException("Menu defines " + configuredSlots.size() + " lockable item slots, but its handler defines " + handler.getLockableSlotCount());
		}

		this.lockableItemHandler = handler;
		this.itemSlotsLocked = handler.isLocked();
		addDataSlot(new DataSlot() {
			@Override
			public int get () {
				return handler.isLocked() ? 1 : 0;
			}

			@Override
			public void set (int value) {
				itemSlotsLocked = value != 0;
			}
		});

		boolean clientSide = blockEntity.getLevel() != null && blockEntity.getLevel().isClientSide();
		for (MachineItemSlot slot : machineSlots) {
			int handlerSlot = slot.getSlotIndex();
			SynchronizedGhostSlot ghostSlot = new SynchronizedGhostSlot(clientSide ? null : () -> handler.getGhostStack(handlerSlot));
			addSlot(ghostSlot);
			slot.bindLockState(this::areItemSlotsLocked, ghostSlot::getItem);
		}
	}

	public final boolean hasItemSlotLocking () {
		return lockableItemHandler != null;
	}

	public final boolean areItemSlotsLocked () {
		return itemSlotsLocked;
	}

	/**
	 * Enables the shared factory title presentation using synchronized menu state.
	 * The supplier is evaluated while rendering, allowing a screen to transition
	 * between standalone and formed-factory presentation without being reopened.
	 */
	protected final void enableFactoryPresentation (BooleanSupplier formedState) {
		if (factoryPresentationState != null) throw new IllegalStateException("Factory presentation is already enabled for this menu");
		this.factoryPresentationState = Objects.requireNonNull(formedState, "formedState");
	}

	/**
	 * Whether this menu should currently use the formed-factory screen title.
	 */
	public final boolean isFactoryPresentationActive () {
		return factoryPresentationState != null && factoryPresentationState.getAsBoolean();
	}

	/**
	 * Enables the shared machine-side configuration UI and server action protocol.
	 * The mode provider should expose synchronized menu state; the setter is only
	 * invoked after the server validates the requested face and mode.
	 */
	protected final void enableMachineSideConfiguration (MachineSideConfigurationDefinition definition, Function<MachineFace, MachineSideMode> modeProvider, BiPredicate<MachineFace, MachineSideMode> modeSetter) {
		enableMachineSideConfiguration(definition, modeProvider, modeSetter, () -> true);
	}

	/**
	 * Enables side configuration with a live availability rule. Existing menus can
	 * continue using the three-argument overload for an always-available widget.
	 */
	protected final void enableMachineSideConfiguration (MachineSideConfigurationDefinition definition, Function<MachineFace, MachineSideMode> modeProvider, BiPredicate<MachineFace, MachineSideMode> modeSetter, BooleanSupplier availability) {
		if (sideConfigurationSource != null) throw new IllegalStateException("Machine side configuration is already enabled for this menu");
		this.sideConfigurationSource = new MachineSideConfigurationSource(Objects.requireNonNull(definition, "definition"), Objects.requireNonNull(modeProvider, "modeProvider"), Objects.requireNonNull(modeSetter, "modeSetter"), Objects.requireNonNull(availability, "availability"));
	}

	public final boolean hasMachineSideConfiguration () {
		return sideConfigurationSource != null;
	}

	public final boolean isMachineSideConfigurationAvailable () {
		MachineSideConfigurationSource source = sideConfigurationSource;
		return source != null && source.availability().getAsBoolean();
	}

	public final MachineSideConfigurationDefinition getMachineSideConfigurationDefinition () {
		MachineSideConfigurationSource source = sideConfigurationSource;
		if (source == null) throw new IllegalStateException("Machine side configuration is not enabled for this menu");
		return source.definition();
	}

	public final MachineSideMode getMachineSideMode (MachineFace face) {
		MachineSideConfigurationSource source = sideConfigurationSource;
		if (source == null) throw new IllegalStateException("Machine side configuration is not enabled for this menu");
		return Objects.requireNonNull(source.modeProvider().apply(Objects.requireNonNull(face, "face")), "Machine side mode provider returned null");
	}

	public static int machineSideConfigurationButton (MachineFace face, MachineSideMode mode) {
		Objects.requireNonNull(face, "face");
		Objects.requireNonNull(mode, "mode");
		return CONFIGURE_SIDE_BUTTON_PREFIX | face.id() << Byte.SIZE | mode.id();
	}

	/**
	 * @return whether this menu has opted into fluid-renderer container filling.
	 */
	public final boolean hasFluidContainerSource () {
		return fluidContainerSource != null;
	}

	/**
	 * @return whether clicking the fluid renderer can move fluid in either direction.
	 */
	public final boolean hasFluidContainerInteraction () {
		return fluidContainerSource != null || fluidContainerTarget != null;
	}

	@Override
	public boolean clickMenuButton (@NonNull Player player, int buttonId) {
		if (buttonId == TRANSFER_FLUID_CONTAINER_BUTTON) return transferCarriedFluidContainer(player);
		if (buttonId == TOGGLE_ITEM_LOCK_BUTTON) return toggleItemSlotLock(player);
		if ((buttonId & CONFIGURE_SIDE_BUTTON_MASK) == CONFIGURE_SIDE_BUTTON_PREFIX) return configureMachineSide(player, buttonId);
		return super.clickMenuButton(player, buttonId);
	}

	private boolean configureMachineSide (Player player, int buttonId) {
		MachineSideConfigurationSource source = sideConfigurationSource;
		if (source == null || player.level().isClientSide() || !stillValid(player) || !source.availability().getAsBoolean()) return false;
		MachineFace face = MachineFace.byId(buttonId >>> Byte.SIZE & 0xFF).orElse(null);
		MachineSideMode mode = MachineSideMode.byId(buttonId & 0xFF).orElse(null);
		if (face == null || mode == null || !source.definition().isConfigurable(face) || !source.definition().supports(face, mode)) return false;
		return source.modeSetter().test(face, mode);
	}

	private boolean toggleItemSlotLock (Player player) {
		LockableItemResourceHandler handler = lockableItemHandler;
		if (handler == null || player.level().isClientSide() || !stillValid(player)) return false;
		handler.toggleLocked();
		itemSlotsLocked = handler.isLocked();
		broadcastChanges();
		return true;
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

	private boolean transferCarriedFluidContainer (Player player) {
		if (player.level().isClientSide() || !stillValid(player) || getCarried().isEmpty()) return false;
		return fillCarriedFluidContainer(player) || drainCarriedFluidContainer(player);
	}

	private boolean drainCarriedFluidContainer (Player player) {
		FluidContainerTarget target = fluidContainerTarget;
		if (target == null) return false;

		var itemAccess = ItemAccess.forPlayerCursor(player, this);
		try (Transaction transaction = Transaction.openRoot()) {
			if (!FluidContainerTransfers.drainIntoTank(itemAccess, target.handler(), target.tank(), target.transferAmount(), transaction)) return false;
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
	 *
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
	 *
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

	private record FluidContainerTarget(ResourceHandler<FluidResource> handler, int tank, int transferAmount) {
	}

	private record MachineSideConfigurationSource(MachineSideConfigurationDefinition definition, Function<MachineFace, MachineSideMode> modeProvider, BiPredicate<MachineFace, MachineSideMode> modeSetter, BooleanSupplier availability) {
	}

	/**
	 * Inactive synchronization-only slot. The server reads its stack from the lock
	 * handler, while the client accepts normal menu slot updates into a local copy.
	 */
	private static final class SynchronizedGhostSlot extends Slot {

		@Nullable
		private final Supplier<ItemStack> authoritativeStack;
		private       ItemStack           remoteStack = ItemStack.EMPTY;

		private SynchronizedGhostSlot (@Nullable Supplier<ItemStack> authoritativeStack) {
			super(new SimpleContainer(1), 0, -10_000, -10_000);
			this.authoritativeStack = authoritativeStack;
		}

		@Override
		public ItemStack getItem () {
			return authoritativeStack == null ? remoteStack : Objects.requireNonNull(authoritativeStack.get(), "Authoritative ghost item stack");
		}

		@Override
		public void set (ItemStack stack) {
			if (authoritativeStack == null) remoteStack = Objects.requireNonNull(stack, "stack").copy();
		}

		@Override
		public boolean mayPlace (ItemStack stack) {
			return false;
		}

		@Override
		public boolean mayPickup (Player player) {
			return false;
		}

		@Override
		public boolean isActive () {
			return false;
		}

		@Override
		public boolean isHighlightable () {
			return false;
		}
	}

	public static class QuickMoveStack {

		// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
		private static final int HOTBAR_SLOT_COUNT             = 9;
		private static final int PLAYER_INVENTORY_ROW_COUNT    = 3;
		private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
		private static final int PLAYER_INVENTORY_SLOT_COUNT   = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
		private static final int VANILLA_SLOT_COUNT            = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
		private static final int VANILLA_FIRST_SLOT_INDEX      = 0;
		private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
		private final        int slotCount;

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
