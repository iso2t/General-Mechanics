package general.api.transfer;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.Objects;

/**
 * Receives committed resource changes. Transactional handlers invoke this only
 * after a root transaction commits and only when the final slot contents differ
 * from the contents at the beginning of that transaction.
 */
@FunctionalInterface
public interface ResourceChangeListener<R extends Resource> {

	void onResourceChanged (int index, R previousResource, int previousAmount, R resource, int amount);

	/**
	 * Adapts lifecycle callbacks such as {@code BlockEntity::setChanged}.
	 */
	static <R extends Resource> ResourceChangeListener<R> from (Runnable callback) {
		Objects.requireNonNull(callback, "callback");
		return (index, previousResource, previousAmount, resource, amount) -> callback.run();
	}

	static <R extends Resource> ResourceChangeListener<R> none () {
		return (index, previousResource, previousAmount, resource, amount) -> {
		};
	}
}
