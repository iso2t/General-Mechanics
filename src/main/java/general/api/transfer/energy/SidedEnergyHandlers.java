package general.api.transfer.energy;

import general.api.transfer.ResourceIoMode;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Cached, dynamically gated energy capability views for block sides.
 *
 * <p>Capability lookup returns {@code null} while a side is disabled. A handler
 * obtained before a configuration change remains safe because its transfer
 * methods consult the same live mode supplier.</p>
 */
public final class SidedEnergyHandlers {

	private final           EnergyHandler internalHandler;
	private final           SideView[]    sides;
	private final @Nullable SideView      unsided;

	private SidedEnergyHandlers (EnergyHandler internalHandler, Map<Direction, Supplier<ResourceIoMode>> modes, @Nullable Supplier<ResourceIoMode> unsidedMode) {
		this.internalHandler = internalHandler;
		this.sides = new SideView[Direction.values().length];
		for (Direction direction : Direction.values()) {
			Supplier<ResourceIoMode> mode = modes.get(direction);
			if (mode != null) sides[direction.ordinal()] = new SideView(mode, new SupplierBackedEnergyHandler(() -> internalHandler, mode));
		}
		this.unsided = unsidedMode == null ? null : new SideView(unsidedMode, new SupplierBackedEnergyHandler(() -> internalHandler, unsidedMode));
	}

	public static Builder builder (EnergyHandler internalHandler) {
		return new Builder(internalHandler);
	}

	/**
	 * Returns the current capability view, or {@code null} when that side is not
	 * configured for energy transfer.
	 */
	public @Nullable EnergyHandler forSide (@Nullable Direction side) {
		SideView view = side == null ? unsided : sides[side.ordinal()];
		return view == null || currentMode(view.mode()) == ResourceIoMode.NONE ? null : view.handler();
	}

	/**
	 * Unrestricted storage intended for the owning machine's internal work.
	 */
	public EnergyHandler internalHandler () {
		return internalHandler;
	}

	private static ResourceIoMode currentMode (Supplier<ResourceIoMode> supplier) {
		ResourceIoMode mode = supplier.get();
		return mode == null ? ResourceIoMode.NONE : mode;
	}

	private record SideView(Supplier<ResourceIoMode> mode, EnergyHandler handler) {
	}

	public static final class Builder {

		private final     EnergyHandler                                internalHandler;
		private final     EnumMap<Direction, Supplier<ResourceIoMode>> modes = new EnumMap<>(Direction.class);
		private @Nullable Supplier<ResourceIoMode>                     unsidedMode;

		private Builder (EnergyHandler internalHandler) {
			this.internalHandler = Objects.requireNonNull(internalHandler, "internalHandler");
		}

		public Builder side (Direction side, ResourceIoMode mode) {
			Objects.requireNonNull(mode, "mode");
			return side(side, () -> mode);
		}

		public Builder side (Direction side, Supplier<ResourceIoMode> mode) {
			Objects.requireNonNull(side, "side");
			Objects.requireNonNull(mode, "mode");
			if (modes.putIfAbsent(side, mode) != null) throw new IllegalArgumentException("Energy mode already configured for side " + side);
			return this;
		}

		public Builder unsided (ResourceIoMode mode) {
			Objects.requireNonNull(mode, "mode");
			return unsided(() -> mode);
		}

		public Builder unsided (Supplier<ResourceIoMode> mode) {
			if (unsidedMode != null) throw new IllegalStateException("Unsided energy mode is already configured");
			unsidedMode = Objects.requireNonNull(mode, "mode");
			return this;
		}

		public SidedEnergyHandlers build () {
			return new SidedEnergyHandlers(internalHandler, modes, unsidedMode);
		}
	}
}
