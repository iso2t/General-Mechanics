package general.api.transfer;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Cached block-side capability views for any resource type.
 *
 * <p>All wrappers are constructed once by {@link Builder#build()}. Capability
 * lookup performs only an array lookup and never allocates. Unconfigured sides
 * and, by default, a {@code null} capability context share one zero-slot denied
 * view. Storage itself has no knowledge of directions.</p>
 */
public final class SidedResourceHandlers<R extends Resource> {

	private final ResourceHandler<R>   internalHandler;
	private final ResourceHandler<R>[] sides;
	private final ResourceHandler<R>   unsided;

	private SidedResourceHandlers (ResourceHandler<R> internalHandler, Map<Direction, ResourceAccessPolicy<R>> policies, ResourceAccessPolicy<R> unsidedPolicy) {
		this.internalHandler = internalHandler;
		var denied = new RestrictedResourceHandler<>(internalHandler, ResourceAccess.none());
		this.sides = newHandlerArray(Direction.values().length);
		for (Direction direction : Direction.values()) {
			ResourceAccessPolicy<R> policy = policies.get(direction);
			sides[direction.ordinal()] = policy == null ? denied : new RestrictedResourceHandler<>(internalHandler, policy);
		}
		this.unsided = unsidedPolicy == null ? denied : new RestrictedResourceHandler<>(internalHandler, unsidedPolicy);
	}

	public static <R extends Resource> Builder<R> builder (ResourceHandler<R> internalHandler) {
		return new Builder<>(internalHandler);
	}

	/**
	 * Cached view for a capability context; {@code null} uses the explicit unsided policy.
	 */
	public ResourceHandler<R> forSide (@Nullable Direction side) {
		return side == null ? unsided : sides[side.ordinal()];
	}

	/**
	 * Unrestricted handler intended for the owning machine's internal operations.
	 */
	public ResourceHandler<R> internalHandler () {
		return internalHandler;
	}

	@SuppressWarnings("unchecked")
	private static <R extends Resource> ResourceHandler<R>[] newHandlerArray (int size) {
		return (ResourceHandler<R>[]) new ResourceHandler<?>[size];
	}

	public static final class Builder<R extends Resource> {

		private final ResourceHandler<R>                          internalHandler;
		private final EnumMap<Direction, ResourceAccessPolicy<R>> policies = new EnumMap<>(Direction.class);
		private       ResourceAccessPolicy<R>                     unsidedPolicy;

		private Builder (ResourceHandler<R> internalHandler) {
			this.internalHandler = Objects.requireNonNull(internalHandler, "internalHandler");
		}

		public Builder<R> side (Direction side, ResourceAccessPolicy<R> policy) {
			Objects.requireNonNull(side, "side");
			Objects.requireNonNull(policy, "policy");
			if (policies.putIfAbsent(side, policy) != null) {
				throw new IllegalArgumentException("Resource access policy already configured for side " + side);
			}
			return this;
		}

		/**
		 * Overrides the default policy that denies automation without a side.
		 */
		public Builder<R> unsided (ResourceAccessPolicy<R> policy) {
			if (unsidedPolicy != null) throw new IllegalStateException("Unsided resource access policy is already configured");
			unsidedPolicy = Objects.requireNonNull(policy, "policy");
			return this;
		}

		public SidedResourceHandlers<R> build () {
			return new SidedResourceHandlers<>(internalHandler, policies, unsidedPolicy);
		}
	}
}
