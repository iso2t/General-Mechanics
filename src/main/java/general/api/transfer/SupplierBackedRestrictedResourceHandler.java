package general.api.transfer;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stable capability view whose backing handler and access policy are resolved for
 * each operation. This is intended for controller-bound attachments and never
 * retains the controller or its handler across chunk unloads.
 */
public final class SupplierBackedRestrictedResourceHandler<R extends Resource> implements ResourceHandler<R> {

	private final Supplier<? extends ResourceHandler<R>>      handlerSupplier;
	private final Supplier<? extends ResourceAccessPolicy<R>> policySupplier;

	public SupplierBackedRestrictedResourceHandler (Supplier<? extends ResourceHandler<R>> handlerSupplier, Supplier<? extends ResourceAccessPolicy<R>> policySupplier) {
		this.handlerSupplier = Objects.requireNonNull(handlerSupplier, "handlerSupplier");
		this.policySupplier = Objects.requireNonNull(policySupplier, "policySupplier");
	}

	@Override
	public int size () {
		ResourceHandler<R> handler = handlerSupplier.get();
		ResourceAccessPolicy<R> policy = policySupplier.get();
		if (handler == null || policy == null) return 0;
		int visible = 0;
		for (int index = 0; index < handler.size(); index++) {
			if (policy.canAccess(index)) visible++;
		}
		return visible;
	}

	@Override
	public R getResource (int index) {
		Resolved<R> resolved = resolve(index);
		return resolved.handler.getResource(resolved.backingIndex);
	}

	@Override
	public long getAmountAsLong (int index) {
		Resolved<R> resolved = resolve(index);
		return resolved.handler.getAmountAsLong(resolved.backingIndex);
	}

	@Override
	public long getCapacityAsLong (int index, R resource) {
		Objects.requireNonNull(resource, "resource");
		Resolved<R> resolved = resolve(index);
		if (!resource.isEmpty() && !resolved.policy.canInsert(resolved.backingIndex, resource)) return 0;
		return resolved.handler.getCapacityAsLong(resolved.backingIndex, resource);
	}

	@Override
	public boolean isValid (int index, R resource) {
		Objects.requireNonNull(resource, "resource");
		Resolved<R> resolved = resolve(index);
		return !resource.isEmpty() && resolved.policy.canInsert(resolved.backingIndex, resource) && resolved.handler.isValid(resolved.backingIndex, resource);
	}

	@Override
	public int insert (int index, R resource, int amount, TransactionContext transaction) {
		Objects.requireNonNull(resource, "resource");
		Objects.requireNonNull(transaction, "transaction");
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		Resolved<R> resolved = resolve(index);
		if (!resolved.policy.canInsert(resolved.backingIndex, resource)) return 0;
		return resolved.handler.insert(resolved.backingIndex, resource, amount, transaction);
	}

	@Override
	public int extract (int index, R resource, int amount, TransactionContext transaction) {
		Objects.requireNonNull(resource, "resource");
		Objects.requireNonNull(transaction, "transaction");
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		Resolved<R> resolved = resolve(index);
		if (!resolved.policy.canExtract(resolved.backingIndex, resource)) return 0;
		return resolved.handler.extract(resolved.backingIndex, resource, amount, transaction);
	}

	private Resolved<R> resolve (int viewIndex) {
		ResourceHandler<R> handler = handlerSupplier.get();
		ResourceAccessPolicy<R> policy = policySupplier.get();
		if (handler == null || policy == null) throw new IndexOutOfBoundsException("Attached resource handler is unavailable");

		int visible = 0;
		for (int backingIndex = 0; backingIndex < handler.size(); backingIndex++) {
			if (!policy.canAccess(backingIndex)) continue;
			if (visible++ == viewIndex) return new Resolved<>(handler, policy, backingIndex);
		}
		throw new IndexOutOfBoundsException("Attached resource handler index " + viewIndex + " is outside [0, " + visible + ")");
	}

	private record Resolved<R extends Resource>(ResourceHandler<R> handler, ResourceAccessPolicy<R> policy, int backingIndex) {
	}
}
