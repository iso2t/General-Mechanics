package general.api.transfer;

import net.neoforged.neoforge.transfer.resource.Resource;

/**
 * External access rules expressed in backing-handler indices.
 *
 * <p>{@link RestrictedResourceHandler} evaluates visibility once when a view is
 * created, producing an immutable compact index mapping. Insertion and
 * extraction methods are evaluated on every operation, allowing policies to
 * consult live machine side modes and import/export toggles. Consequently,
 * {@link #canAccess} must remain stable for the lifetime of a cached view.</p>
 */
public interface ResourceAccessPolicy<R extends Resource> {

	boolean canAccess (int index);

	boolean canInsert (int index, R resource);

	boolean canExtract (int index, R resource);
}
