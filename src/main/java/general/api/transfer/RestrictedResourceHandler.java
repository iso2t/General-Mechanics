package general.api.transfer;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Arrays;
import java.util.Objects;

/**
 * Transaction-preserving external view over a backing handler.
 *
 * <p>The view exposes a compact immutable index set: view index {@code 0}
 * maps to the first visible backing index, and hidden backing indices do not
 * appear at all. This is less surprising to generic capability consumers than
 * advertising inaccessible holes. {@link #backingIndex(int)} is available for
 * diagnostics and adapters that need the original index.</p>
 *
 * <p>The wrapper never opens, commits, or closes a caller transaction. Queries
 * and allowed mutations are delegated directly. The backing handler remains
 * the unrestricted interface for internal machine logic.</p>
 */
public final class RestrictedResourceHandler<R extends Resource> implements ResourceHandler<R> {

	private final ResourceHandler<R>      delegate;
	private final ResourceAccessPolicy<R> policy;
	private final int[]                   backingIndices;

	public RestrictedResourceHandler (ResourceHandler<R> delegate, ResourceAccessPolicy<R> policy) {
		this.delegate = Objects.requireNonNull(delegate, "delegate");
		this.policy = Objects.requireNonNull(policy, "policy");
		int delegateSize = delegate.size();
		int[] visible = new int[delegateSize];
		int count = 0;
		for (int index = 0; index < delegateSize; index++) {
			if (policy.canAccess(index)) visible[count++] = index;
		}
		this.backingIndices = Arrays.copyOf(visible, count);
	}

	@Override
	public int size () {
		return backingIndices.length;
	}

	@Override
	public R getResource (int index) {
		return delegate.getResource(backingIndex(index));
	}

	@Override
	public long getAmountAsLong (int index) {
		return delegate.getAmountAsLong(backingIndex(index));
	}

	@Override
	public long getCapacityAsLong (int index, R resource) {
		Objects.requireNonNull(resource, "resource");
		int backingIndex = backingIndex(index);
		if (!resource.isEmpty() && !policy.canInsert(backingIndex, resource)) return 0;
		return delegate.getCapacityAsLong(backingIndex, resource);
	}

	@Override
	public boolean isValid (int index, R resource) {
		Objects.requireNonNull(resource, "resource");
		int backingIndex = backingIndex(index);
		return !resource.isEmpty() && policy.canInsert(backingIndex, resource) && delegate.isValid(backingIndex, resource);
	}

	@Override
	public int insert (int index, R resource, int amount, TransactionContext transaction) {
		Objects.requireNonNull(resource, "resource");
		Objects.requireNonNull(transaction, "transaction");
		int backingIndex = backingIndex(index);
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		if (!policy.canInsert(backingIndex, resource)) return 0;
		return delegate.insert(backingIndex, resource, amount, transaction);
	}

	@Override
	public int extract (int index, R resource, int amount, TransactionContext transaction) {
		Objects.requireNonNull(resource, "resource");
		Objects.requireNonNull(transaction, "transaction");
		int backingIndex = backingIndex(index);
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		if (!policy.canExtract(backingIndex, resource)) return 0;
		return delegate.extract(backingIndex, resource, amount, transaction);
	}

	/**
	 * Returns the immutable view-to-backing mapping for one view index.
	 */
	public int backingIndex (int viewIndex) {
		if (viewIndex < 0 || viewIndex >= backingIndices.length) {
			throw new IndexOutOfBoundsException("Restricted handler index " + viewIndex + " is outside [0, " + backingIndices.length + ")");
		}
		return backingIndices[viewIndex];
	}

	public ResourceHandler<R> delegate () {
		return delegate;
	}
}
