package general.api.transfer;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;

import java.util.Objects;

/**
 * Ready-to-use generic specialization backed by immutable
 * {@link ResourceStack}s. A future resource type only needs an empty instance
 * and a stack codec to use the complete definition/access/sided infrastructure.
 */
public final class ResourceStackHandler<R extends Resource> extends DefinitionBackedResourceHandler<ResourceStack<R>, R> {

	public ResourceStackHandler (ResourceInventoryDefinition<R> definition, R emptyResource, Codec<ResourceStack<R>> stackCodec, ResourceChangeListener<R> changeListener) {
		super(definition, new ResourceStack<>(requireEmpty(emptyResource), 0), stackCodec, changeListener);
	}

	public ResourceStackHandler (ResourceInventoryDefinition<R> definition, R emptyResource, Codec<ResourceStack<R>> stackCodec, Runnable changeCallback) {
		this(definition, emptyResource, stackCodec, ResourceChangeListener.from(changeCallback));
	}

	@Override
	protected R getResourceFrom (ResourceStack<R> stack) {
		return stack.resource();
	}

	@Override
	protected int getAmountFrom (ResourceStack<R> stack) {
		return stack.amount();
	}

	@Override
	protected ResourceStack<R> getStackFrom (R resource, int amount) {
		return ResourceHandlerUtil.isEmpty(resource, amount) ? emptyStack : new ResourceStack<>(resource, amount);
	}

	@Override
	protected ResourceStack<R> copyOf (ResourceStack<R> stack) {
		return stack;
	}

	private static <R extends Resource> R requireEmpty (R resource) {
		Objects.requireNonNull(resource, "emptyResource");
		if (!resource.isEmpty()) throw new IllegalArgumentException("Expected an empty resource, got " + resource);
		return resource;
	}
}
