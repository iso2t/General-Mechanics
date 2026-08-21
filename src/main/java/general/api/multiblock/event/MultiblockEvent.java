package general.api.multiblock.event;

import general.api.multiblock.MultiblockController;
import general.api.multiblock.MultiblockInstance;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Base type for server-side multiblock lifecycle events posted on the
 * NeoForge game event bus. These events are observations of an already-applied
 * runtime transition and therefore are not cancellable.
 */
public abstract class MultiblockEvent extends Event {

	private final           ServerLevel          level;
	private final @Nullable MultiblockController controller;
	private final           MultiblockInstance   instance;

	public MultiblockEvent (ServerLevel level, @Nullable MultiblockController controller, MultiblockInstance instance) {
		this.level = Objects.requireNonNull(level, "level");
		this.controller = controller;
		this.instance = Objects.requireNonNull(instance, "instance");
	}

	public ServerLevel level () {
		return level;
	}

	/**
	 * Returns the controller that owns the transition, or {@code null} when the
	 * destruction was detected after the controller block entity had been removed.
	 * The instance and its anchor remain available in either case.
	 */
	public @Nullable MultiblockController controller () {
		return controller;
	}

	/**
	 * Returns the newly formed instance or, for destruction, the last known formed
	 * instance. Its block map is immutable.
	 */
	public MultiblockInstance instance () {
		return instance;
	}

	public enum State {
		UNFORMED,
		FORMED
	}

	/**
	 * Common event type for all detected formed-state transitions. Listening to
	 * this type receives both {@link Formed} and {@link Destroyed} events.
	 */
	public static class StateChanged extends MultiblockEvent {

		private final State previousState;
		private final State state;

		protected StateChanged (ServerLevel level, @Nullable MultiblockController controller, MultiblockInstance instance, State previousState, State state) {
			super(level, controller, instance);
			this.previousState = Objects.requireNonNull(previousState, "previousState");
			this.state = Objects.requireNonNull(state, "state");
			if (previousState == state) {
				throw new IllegalArgumentException("A multiblock state-change event requires different states");
			}
		}

		public State previousState () {
			return previousState;
		}

		public State state () {
			return state;
		}

		public boolean wasFormed () {
			return previousState == State.FORMED;
		}

		public boolean isFormed () {
			return state == State.FORMED;
		}
	}

	/**
	 * Fired once after a controller transitions from unformed to formed.
	 */
	public static final class Formed extends StateChanged {

		public Formed (ServerLevel level, MultiblockController controller, MultiblockInstance instance) {
			super(level, Objects.requireNonNull(controller, "controller"), instance, State.UNFORMED, State.FORMED);
		}

		@Override
		public MultiblockController controller () {
			return Objects.requireNonNull(super.controller());
		}
	}

	/**
	 * Fired once after a previously formed structure becomes invalid or its
	 * controller is removed. Level and chunk unloading do not destroy a structure
	 * and do not fire this event.
	 */
	public static final class Destroyed extends StateChanged {

		public Destroyed (ServerLevel level, @Nullable MultiblockController controller, MultiblockInstance instance) {
			super(level, controller, instance, State.FORMED, State.UNFORMED);
		}
	}
}
