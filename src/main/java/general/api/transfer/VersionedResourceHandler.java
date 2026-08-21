package general.api.transfer;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

/**
 * Resource handler that exposes a cheap invalidation token for derived state.
 *
 * <p>The revision must change after any committed state change that can affect
 * transfer results. Simulations, aborted transactions, and net-zero operations
 * must leave it unchanged.</p>
 */
public interface VersionedResourceHandler<R extends Resource> extends ResourceHandler<R> {

	long contentRevision ();
}
