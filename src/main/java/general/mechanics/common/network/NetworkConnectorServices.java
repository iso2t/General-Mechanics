package general.mechanics.common.network;

import general.api.network.NetworkNode;
import general.api.network.NetworkServices;
import general.api.network.service.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
		register(capability(Capabilities.Item.BLOCK, NetworkServices.ITEM, handler -> handler.size() > 0, ItemService::new));
		register(capability(Capabilities.Fluid.BLOCK, NetworkServices.FLUID, handler -> handler.size() > 0, FluidService::new));
		register(capability(Capabilities.Energy.BLOCK, NetworkServices.ENERGY, EnergyService::new));
	}

	private NetworkConnectorServices () {
	}

	public static void register (NetworkConnectorServiceBridge bridge) {
		BRIDGES.add(Objects.requireNonNull(bridge, "bridge"));
	}

	/**
	 * @return whether the target exposed at least one registered network service.
	 */
	public static boolean refresh (NetworkNode node, Level level, BlockPos targetPos, Direction targetSide) {
		node.getServices().clear();
		for (NetworkConnectorServiceBridge bridge : BRIDGES) {
			bridge.register(node, level, targetPos, targetSide);
		}
		return !node.getServices().getAll().isEmpty();
	}

	public static <C, S extends NetworkService> NetworkConnectorServiceBridge capability (BlockCapability<C, Direction> capability, NetworkServiceType<S> type, Function<Supplier<C>, S> factory) {
		return capability(capability, type, value -> true, factory);
	}

	public static <C, S extends NetworkService> NetworkConnectorServiceBridge capability (BlockCapability<C, Direction> capability, NetworkServiceType<S> type, Predicate<? super C> isUsable, Function<Supplier<C>, S> factory) {
		Objects.requireNonNull(capability, "capability");
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(isUsable, "isUsable");
		Objects.requireNonNull(factory, "factory");
		return (node, level, pos, side) -> {
			C exposed = level.getCapability(capability, pos, side);
			if (exposed == null || !isUsable.test(exposed)) return;
			S service = Objects.requireNonNull(factory.apply(() -> level.getCapability(capability, pos, side)), "Capability service factory returned null");
			node.getServices().register(type, service);
		};
	}

	@FunctionalInterface
	public interface NetworkConnectorServiceBridge {
		void register (NetworkNode node, Level level, BlockPos targetPos, Direction targetSide);
	}

	private static final class ItemService implements ItemNetworkService {

		private static final StackAdapter<ItemStack, ItemResource> STACKS = new StackAdapter<>() {
			@Override
			public ItemStack create (ItemResource resource, int amount) {
				return resource.toStack(amount);
			}

			@Override
			public ItemStack empty () {
				return ItemStack.EMPTY;
			}

			@Override
			public int maximumResultAmount (ItemResource resource) {
				return resource.getMaxStackSize();
			}
		};

		private final HandlerTransfers<ItemStack, ItemResource> transfers;

		private ItemService (Supplier<ResourceHandler<ItemResource>> handlerSupplier) {
			this.transfers = new HandlerTransfers<>(handlerSupplier, STACKS);
		}

		@Override
		public ItemStack insert (ItemStack stack, boolean simulate) {
			Objects.requireNonNull(stack, "stack");
			if (stack.isEmpty()) return ItemStack.EMPTY;
			int inserted = transfers.insert(ItemResource.of(stack), stack.getCount(), simulate);
			return stack.copyWithCount(stack.getCount() - inserted);
		}

		@Override
		public ItemStack extract (Predicate<ItemStack> filter, int amount, boolean simulate) {
			return transfers.extract(filter, amount, simulate);
		}

		@Override
		public List<ItemStack> getAvailableItems () {
			return transfers.getAvailable();
		}
	}

	private static final class FluidService implements FluidNetworkService {

		private static final StackAdapter<FluidStack, FluidResource> STACKS = new StackAdapter<>() {
			@Override
			public FluidStack create (FluidResource resource, int amount) {
				return resource.toStack(amount);
			}

			@Override
			public FluidStack empty () {
				return FluidStack.EMPTY;
			}

			@Override
			public int maximumResultAmount (FluidResource resource) {
				return Integer.MAX_VALUE;
			}
		};

		private final HandlerTransfers<FluidStack, FluidResource> transfers;

		private FluidService (Supplier<ResourceHandler<FluidResource>> handlerSupplier) {
			this.transfers = new HandlerTransfers<>(handlerSupplier, STACKS);
		}

		@Override
		public int insert (FluidStack stack, boolean simulate) {
			Objects.requireNonNull(stack, "stack");
			if (stack.isEmpty()) return 0;
			return transfers.insert(FluidResource.of(stack), stack.getAmount(), simulate);
		}

		@Override
		public FluidStack extract (Predicate<FluidStack> filter, int amount, boolean simulate) {
			return transfers.extract(filter, amount, simulate);
		}

		@Override
		public List<FluidStack> getAvailableFluids () {
			return transfers.getAvailable();
		}
	}

	/**
	 * Shared item/fluid transfer implementation over the generic NeoForge contract.
	 */
	private static final class HandlerTransfers<S, R extends Resource> {

		private final Supplier<ResourceHandler<R>> handlerSupplier;
		private final StackAdapter<S, R>           stacks;

		private HandlerTransfers (Supplier<ResourceHandler<R>> handlerSupplier, StackAdapter<S, R> stacks) {
			this.handlerSupplier = Objects.requireNonNull(handlerSupplier, "handlerSupplier");
			this.stacks = Objects.requireNonNull(stacks, "stacks");
		}

		private int insert (R resource, int amount, boolean simulate) {
			ResourceHandler<R> handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return 0;
			try (var transaction = Transaction.openRoot()) {
				int inserted = handler.insert(resource, amount, transaction);
				if (!simulate && inserted > 0) transaction.commit();
				return inserted;
			}
		}

		private S extract (Predicate<S> filter, int amount, boolean simulate) {
			Objects.requireNonNull(filter, "filter");
			ResourceHandler<R> handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return stacks.empty();

			try (var transaction = Transaction.openRoot()) {
				R selected = null;
				int target = amount;
				int extracted = 0;

				for (int index = 0; index < handler.size() && extracted < target; index++) {
					R resource = handler.getResource(index);
					int available = handler.getAmountAsInt(index);
					if (resource.isEmpty() || available <= 0 || (selected != null && !selected.equals(resource))) continue;

					int candidateTarget = selected == null ? Math.min(amount, stacks.maximumResultAmount(resource)) : target;
					int requested = Math.min(candidateTarget - extracted, available);
					if (requested <= 0 || (selected == null && !filter.test(stacks.create(resource, requested)))) continue;

					int fromIndex = handler.extract(index, resource, requested, transaction);
					if (fromIndex <= 0) continue;
					if (selected == null) {
						selected = resource;
						target = candidateTarget;
					}
					extracted += fromIndex;
				}

				if (selected == null || extracted == 0) return stacks.empty();
				if (!simulate) transaction.commit();
				return stacks.create(selected, extracted);
			}
		}

		private List<S> getAvailable () {
			ResourceHandler<R> handler = handlerSupplier.get();
			if (handler == null) return List.of();

			var result = new ArrayList<S>();
			try (var transaction = Transaction.openRoot()) {
				for (int index = 0; index < handler.size(); index++) {
					R resource = handler.getResource(index);
					int stored = handler.getAmountAsInt(index);
					if (resource.isEmpty() || stored <= 0) continue;
					int extractable = handler.extract(index, resource, stored, transaction);
					if (extractable > 0) result.add(stacks.create(resource, extractable));
				}
			}
			return List.copyOf(result);
		}
	}

	private interface StackAdapter<S, R extends Resource> {
		S create (R resource, int amount);

		S empty ();

		int maximumResultAmount (R resource);
	}

	private record EnergyService(Supplier<EnergyHandler> handlerSupplier) implements EnergyNetworkService {
		@Override
		public long insert (long amount, boolean simulate) {
			var handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return 0;
			try (var transaction = Transaction.openRoot()) {
				int inserted = handler.insert((int) Math.min(amount, Integer.MAX_VALUE), transaction);
				if (!simulate && inserted > 0) transaction.commit();
				return inserted;
			}
		}

		@Override
		public long extract (long amount, boolean simulate) {
			var handler = handlerSupplier.get();
			if (handler == null || amount <= 0) return 0;
			try (var transaction = Transaction.openRoot()) {
				int extracted = handler.extract((int) Math.min(amount, Integer.MAX_VALUE), transaction);
				if (!simulate && extracted > 0) transaction.commit();
				return extracted;
			}
		}

		@Override
		public long getStored () {
			var handler = handlerSupplier.get();
			return handler == null ? 0 : handler.getAmountAsLong();
		}

		@Override
		public long getCapacity () {
			var handler = handlerSupplier.get();
			return handler == null ? 0 : handler.getCapacityAsLong();
		}
	}
}
