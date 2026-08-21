package general.mechanics.common.menus;

import general.api.screens.menu.AbstractMenu;
import general.mechanics.common.block.CokeOvenController;
import general.mechanics.common.block.entity.CokeOvenControllerBlockEntity;
import general.mechanics.registries.GenBlockEntities;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenMenus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class CokeOvenMenu extends AbstractMenu<CokeOvenController, CokeOvenControllerBlockEntity> {

	private static final int DATA_PROGRESS     = 0;
	private static final int DATA_MAX_PROGRESS = 1;
	private static final int DATA_FLUID_ID     = 2;
	private static final int DATA_FLUID_AMOUNT = 3;
	private static final int DATA_COUNT        = 4;

	private static final int INPUT_X  = 46;
	private static final int INPUT_Y  = 35;
	private static final int OUTPUT_X = 80;
	private static final int OUTPUT_Y = 35;

	/**
	 * Client-side construction from the block position sent by the menu provider.
	 */
	public CokeOvenMenu (int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
		this(containerId, inventory, findBlockEntity(inventory, buffer), new SimpleContainerData(DATA_COUNT));
	}

	/**
	 * Server-side construction with live machine-backed synchronization data.
	 */
	public CokeOvenMenu (int containerId, Inventory inventory, CokeOvenControllerBlockEntity blockEntity) {
		this(containerId, inventory, blockEntity, createServerData(blockEntity));
	}

	private CokeOvenMenu (int containerId, Inventory inventory, CokeOvenControllerBlockEntity blockEntity, ContainerData data) {
		super(GenMenus.COKE_OVEN.get(), containerId, inventory, GenBlocks.COKE_OVEN_CONTROLLER.get(), blockEntity, data, CokeOvenController.getRecipeDefinition());
		checkContainerDataCount(data, DATA_COUNT);
		setFluidContainerSource(blockEntity.getFluidHandler(), CokeOvenControllerBlockEntity.CREOSOTE_TANK);
	}

	@Override
	public int getSlotCount () {
		return CokeOvenControllerBlockEntity.ITEMS.size();
	}

	@Override
	public void addContainerSlots () {
		var handler = getBlockEntity().getItemHandler();
		addSlot(new ResourceHandlerSlot(handler, handler::set, CokeOvenControllerBlockEntity.INPUT_SLOT, INPUT_X, INPUT_Y));
		addSlot(new ResourceHandlerSlot(handler, handler::set, CokeOvenControllerBlockEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
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

	@Override
	public boolean stillValid (@NonNull Player player) {
		return super.stillValid(player) && (player.level().isClientSide() || getBlockEntity().isMultiblockOperational());
	}

	public int getProgress () {
		return getData().get(DATA_PROGRESS);
	}

	public int getMaxProgress () {
		return getData().get(DATA_MAX_PROGRESS);
	}

	public FluidStack getFluidStack () {
		int amount = getData().get(DATA_FLUID_AMOUNT);
		if (amount <= 0) return FluidStack.EMPTY;
		var fluid = BuiltInRegistries.FLUID.byId(getData().get(DATA_FLUID_ID));
		return fluid == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(fluid, amount);
	}

	private static CokeOvenControllerBlockEntity findBlockEntity (Inventory inventory, RegistryFriendlyByteBuf buffer) {
		Objects.requireNonNull(buffer, "Coke Oven menu requires its controller block position");
		var pos = buffer.readBlockPos();
		var blockEntity = GenBlockEntities.COKE_OVEN_CONTROLLER.getBlockEntity(inventory.player.level(), pos);
		if (blockEntity == null) throw new IllegalStateException("No Coke Oven controller block entity at " + pos);
		return blockEntity;
	}

	private static ContainerData createServerData (CokeOvenControllerBlockEntity blockEntity) {
		return new ContainerData() {
			@Override
			public int get (int index) {
				return switch (index) {
					case DATA_PROGRESS -> blockEntity.getProgress();
					case DATA_MAX_PROGRESS -> blockEntity.getMaxProgress();
					case DATA_FLUID_ID -> {
						FluidStack fluid = blockEntity.getFluidStack();
						yield fluid.isEmpty() ? 0 : BuiltInRegistries.FLUID.getId(fluid.getFluid());
					}
					case DATA_FLUID_AMOUNT -> blockEntity.getFluidHandler().getAmountAsInt(CokeOvenControllerBlockEntity.CREOSOTE_TANK);
					default -> throw new IndexOutOfBoundsException("Unknown Coke Oven data index: " + index);
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
