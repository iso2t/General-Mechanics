package general.api.transfer.fluid;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

/**
 * Exact, transactional transfers between fluid-capable items and machine tanks.
 */
public final class FluidContainerTransfers {

	private FluidContainerTransfers () {
	}

	/**
	 * Returns whether an item exposes fluid storage and currently contains no fluid.
	 */
	public static boolean isEmptyContainer (ItemResource resource) {
		Objects.requireNonNull(resource, "resource");
		if (resource.isEmpty()) return false;
		ItemStack stack = resource.toStack(1);
		ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
		if (handler == null || handler.size() == 0) return false;
		for (int index = 0; index < handler.size(); index++) {
			if (!handler.getResource(index).isEmpty() || handler.getAmountAsLong(index) > 0) return false;
		}
		return true;
	}

	/**
	 * Fills one item from {@code inputSlot}, placing its transformed item directly
	 * into {@code outputSlot}. The complete amount must fit in both the container
	 * and destination slot, and must exist in the tank.
	 */
	public static boolean fillFromTank (ResourceHandler<ItemResource> items, int inputSlot, int outputSlot, ResourceHandler<FluidResource> fluids, int tank, int amount, TransactionContext transaction) {
		Objects.requireNonNull(items, "items");
		Objects.requireNonNull(fluids, "fluids");
		Objects.requireNonNull(transaction, "transaction");
		Objects.checkIndex(inputSlot, items.size());
		Objects.checkIndex(outputSlot, items.size());
		Objects.checkIndex(tank, fluids.size());
		if (inputSlot == outputSlot) throw new IllegalArgumentException("Fluid container input and output slots must be distinct");
		if (amount <= 0) throw new IllegalArgumentException("Fluid container transfer amount must be positive");

		FluidResource fluid = fluids.getResource(tank);
		if (fluid.isEmpty() || fluids.getAmountAsLong(tank) < amount) return false;

		ItemAccess containerAccess = new SlotExchangeItemAccess(items, inputSlot, outputSlot).oneByOne();
		ResourceHandler<FluidResource> container = containerAccess.getCapability(Capabilities.Fluid.ITEM);
		if (container == null || container.insert(fluid, amount, transaction) != amount) return false;
		return fluids.extract(tank, fluid, amount, transaction) == amount;
	}

	/**
	 * Drains an exact amount from the first compatible tank in an item access.
	 */
	public static boolean drainIntoTank (ItemAccess containerAccess, ResourceHandler<FluidResource> fluids, int tank, int amount, TransactionContext transaction) {
		Objects.requireNonNull(containerAccess, "containerAccess");
		Objects.requireNonNull(fluids, "fluids");
		Objects.requireNonNull(transaction, "transaction");
		Objects.checkIndex(tank, fluids.size());
		if (amount <= 0) throw new IllegalArgumentException("Fluid container transfer amount must be positive");

		ResourceHandler<FluidResource> container = containerAccess.oneByOne().getCapability(Capabilities.Fluid.ITEM);
		if (container == null) return false;
		for (int index = 0; index < container.size(); index++) {
			FluidResource resource = container.getResource(index);
			if (resource.isEmpty() || container.getAmountAsLong(index) < amount) continue;
			try (Transaction attempt = Transaction.open(transaction)) {
				if (container.extract(index, resource, amount, attempt) != amount) continue;
				if (fluids.insert(tank, resource, amount, attempt) != amount) continue;
				attempt.commit();
				return true;
			}
		}
		return false;
	}

	private record SlotExchangeItemAccess(ResourceHandler<ItemResource> items, int inputSlot, int outputSlot) implements ItemAccess {

		@Override
		public ItemResource getResource () {
			return items.getResource(inputSlot);
		}

		@Override
		public int getAmount () {
			return items.getAmountAsInt(inputSlot);
		}

		@Override
		public int insert (ItemResource resource, int amount, TransactionContext transaction) {
			return items.insert(outputSlot, resource, amount, transaction);
		}

		@Override
		public int extract (ItemResource resource, int amount, TransactionContext transaction) {
			return items.extract(inputSlot, resource, amount, transaction);
		}
	}
}
