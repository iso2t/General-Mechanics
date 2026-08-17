package general.mechanics.common.network;

import general.api.network.NetworkNode;
import general.api.network.NetworkServices;
import general.api.network.service.EnergyNetworkService;
import general.api.network.service.FluidNetworkService;
import general.api.network.service.ItemNetworkService;
import general.api.network.service.NetworkService;
import general.api.network.service.NetworkServiceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Capability-to-network-service adapters used by network connectors.
 *
 * <p>The connector gets capabilities on its target face. Other services can
 * add an adapter here without changing the connector block entity.</p>
 */
public final class NetworkConnectorServices {

	private static final List<NetworkConnectorServiceBridge> BRIDGES = new CopyOnWriteArrayList<>();

	static {
		register(capability(Capabilities.Item.BLOCK, NetworkServices.ITEM, ItemService::new));
		register(capability(Capabilities.Fluid.BLOCK, NetworkServices.FLUID, FluidService::new));
		register(capability(Capabilities.Energy.BLOCK, NetworkServices.ENERGY, EnergyService::new));
	}

	private NetworkConnectorServices () {
	}

	public static void register (NetworkConnectorServiceBridge bridge) {
		BRIDGES.add(bridge);
	}

	/** @return whether the target exposed at least one registered network service. */
	public static boolean refresh (NetworkNode node, Level level, BlockPos targetPos, Direction targetSide) {
		node.getServices().clear();
		for (NetworkConnectorServiceBridge bridge : BRIDGES) {
			bridge.register(node, level, targetPos, targetSide);
		}
		// Vanilla containers are expected to expose the item capability, but retain
		// this direct fallback for containers without a registered capability.
		if (!node.hasService(NetworkServices.ITEM) && level.getBlockEntity(targetPos) instanceof Container container) {
			node.getServices().register(NetworkServices.ITEM, new ItemService(() -> VanillaContainerWrapper.of(container)));
		}
		return !node.getServices().getAll().isEmpty();
	}

	public static <C, S extends NetworkService> NetworkConnectorServiceBridge capability (BlockCapability<C, Direction> capability, NetworkServiceType<S> type, Function<Supplier<C>, S> factory) {
		return (node, level, pos, side) -> {
			if (level.getCapability(capability, pos, side) == null) {
				return;
			}
			node.getServices().register(type, factory.apply(() -> level.getCapability(capability, pos, side)));
		};
	}

	@FunctionalInterface
	public interface NetworkConnectorServiceBridge {
		void register (NetworkNode node, Level level, BlockPos targetPos, Direction targetSide);
	}

	private record ItemService(Supplier<ResourceHandler<ItemResource>> handlerSupplier) implements ItemNetworkService {
		@Override
		public ItemStack insert (ItemStack stack, boolean simulate) {
			if (stack.isEmpty()) return ItemStack.EMPTY;
			var handler = handlerSupplier.get();
			if (handler == null) return stack.copy();

			try (var transaction = Transaction.openRoot()) {
				int inserted = handler.insert(ItemResource.of(stack), stack.getCount(), transaction);
				if (!simulate) transaction.commit();
				return stack.copyWithCount(stack.getCount() - inserted);
			}
		}

		@Override
		public ItemStack extract (Predicate<ItemStack> filter, int amount, boolean simulate) {
			var handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return ItemStack.EMPTY;

			try (var transaction = Transaction.openRoot()) {
				for (int slot = 0; slot < handler.size(); slot++) {
					var resource = handler.getResource(slot);
					int available = handler.getAmountAsInt(slot);
					if (resource.isEmpty() || available <= 0 || !filter.test(resource.toStack(Math.min(amount, available)))) continue;
					int extracted = handler.extract(slot, resource, Math.min(amount, available), transaction);
					if (extracted > 0 && !simulate) transaction.commit();
					return extracted > 0 ? resource.toStack(extracted) : ItemStack.EMPTY;
				}
			}
			return ItemStack.EMPTY;
		}

		@Override
		public List<ItemStack> getAvailableItems () {
			var handler = handlerSupplier.get();
			if (handler == null) return List.of();
			var result = new ArrayList<ItemStack>();
			for (int slot = 0; slot < handler.size(); slot++) {
				var resource = handler.getResource(slot);
				int amount = handler.getAmountAsInt(slot);
				if (!resource.isEmpty() && amount > 0) result.add(resource.toStack(amount));
			}
			return List.copyOf(result);
		}
	}

	private record FluidService(Supplier<ResourceHandler<FluidResource>> handlerSupplier) implements FluidNetworkService {
		@Override
		public int insert (FluidStack stack, boolean simulate) {
			if (stack.isEmpty()) return 0;
			var handler = handlerSupplier.get();
			if (handler == null) return 0;
			try (var transaction = Transaction.openRoot()) {
				int inserted = handler.insert(FluidResource.of(stack), stack.getAmount(), transaction);
				if (!simulate) transaction.commit();
				return inserted;
			}
		}

		@Override
		public FluidStack extract (Predicate<FluidStack> filter, int amount, boolean simulate) {
			var handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return FluidStack.EMPTY;
			try (var transaction = Transaction.openRoot()) {
				for (int tank = 0; tank < handler.size(); tank++) {
					var resource = handler.getResource(tank);
					int available = handler.getAmountAsInt(tank);
					if (resource.isEmpty() || available <= 0 || !filter.test(resource.toStack(Math.min(amount, available)))) continue;
					int extracted = handler.extract(tank, resource, Math.min(amount, available), transaction);
					if (extracted > 0 && !simulate) transaction.commit();
					return extracted > 0 ? resource.toStack(extracted) : FluidStack.EMPTY;
				}
			}
			return FluidStack.EMPTY;
		}

		@Override
		public List<FluidStack> getAvailableFluids () {
			var handler = handlerSupplier.get();
			if (handler == null) return List.of();
			var result = new ArrayList<FluidStack>();
			for (int tank = 0; tank < handler.size(); tank++) {
				var resource = handler.getResource(tank);
				int amount = handler.getAmountAsInt(tank);
				if (!resource.isEmpty() && amount > 0) result.add(resource.toStack(amount));
			}
			return List.copyOf(result);
		}
	}

	private record EnergyService(Supplier<EnergyHandler> handlerSupplier) implements EnergyNetworkService {
		@Override
		public long insert (long amount, boolean simulate) {
			var handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return 0;
			try (var transaction = Transaction.openRoot()) {
				int inserted = handler.insert((int) Math.min(amount, Integer.MAX_VALUE), transaction);
				if (!simulate) transaction.commit();
				return inserted;
			}
		}

		@Override
		public long extract (long amount, boolean simulate) {
			var handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return 0;
			try (var transaction = Transaction.openRoot()) {
				int extracted = handler.extract((int) Math.min(amount, Integer.MAX_VALUE), transaction);
				if (!simulate) transaction.commit();
				return extracted;
			}
		}

		@Override public long getStored () { var handler = handlerSupplier.get(); return handler == null ? 0 : handler.getAmountAsLong(); }
		@Override public long getCapacity () { var handler = handlerSupplier.get(); return handler == null ? 0 : handler.getCapacityAsLong(); }
	}
}
