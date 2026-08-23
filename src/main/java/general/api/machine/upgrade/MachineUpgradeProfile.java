package general.api.machine.upgrade;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Objects;

/**
 * Immutable modifiers supplied by one machine upgrade tier.
 *
 * <p>Every value is an independent multiplier and defaults to {@code 1x}. A
 * machine applies only the modifiers relevant to the resources it owns; for
 * example, an energy consumer ignores {@link #energyOutputMultiplier()} and a
 * machine without tanks ignores the fluid modifiers.</p>
 *
 * <p>Profiles describe a tier, not a quantity of installed blocks. A uniform
 * multiblock core should resolve its tier once and apply the resulting profile
 * once.</p>
 */
public record MachineUpgradeProfile(double processingSpeedMultiplier, double energyCapacityMultiplier, double energyInputMultiplier, double energyOutputMultiplier, double fluidCapacityMultiplier, double fluidTransferMultiplier) {

	private static final String PROCESSING_SPEED_TAG = "processing_speed";
	private static final String ENERGY_CAPACITY_TAG  = "energy_capacity";
	private static final String ENERGY_INPUT_TAG     = "energy_input";
	private static final String ENERGY_OUTPUT_TAG    = "energy_output";
	private static final String FLUID_CAPACITY_TAG   = "fluid_capacity";
	private static final String FLUID_TRANSFER_TAG   = "fluid_transfer";

	public static final MachineUpgradeProfile IDENTITY = new MachineUpgradeProfile(1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);

	public MachineUpgradeProfile {
		requireMultiplier("Processing speed", processingSpeedMultiplier);
		requireMultiplier("Energy capacity", energyCapacityMultiplier);
		requireMultiplier("Energy input", energyInputMultiplier);
		requireMultiplier("Energy output", energyOutputMultiplier);
		requireMultiplier("Fluid capacity", fluidCapacityMultiplier);
		requireMultiplier("Fluid transfer", fluidTransferMultiplier);
	}

	/**
	 * Returns a profile that does not change any machine behavior.
	 */
	public static MachineUpgradeProfile identity () {
		return IDENTITY;
	}

	/**
	 * Compatibility factory for upgrades that currently define only processing
	 * speed. Other modifiers remain at {@code 1x}.
	 */
	public static MachineUpgradeProfile speed (double processingSpeedMultiplier) {
		return builder().processingSpeed(processingSpeedMultiplier).build();
	}

	public static Builder builder () {
		return new Builder();
	}

	public Builder toBuilder () {
		return new Builder(this);
	}

	public void save (ValueOutput output) {
		Objects.requireNonNull(output, "output");
		output.putDouble(PROCESSING_SPEED_TAG, processingSpeedMultiplier);
		output.putDouble(ENERGY_CAPACITY_TAG, energyCapacityMultiplier);
		output.putDouble(ENERGY_INPUT_TAG, energyInputMultiplier);
		output.putDouble(ENERGY_OUTPUT_TAG, energyOutputMultiplier);
		output.putDouble(FLUID_CAPACITY_TAG, fluidCapacityMultiplier);
		output.putDouble(FLUID_TRANSFER_TAG, fluidTransferMultiplier);
	}

	public static MachineUpgradeProfile load (ValueInput input) {
		return load(input, IDENTITY);
	}

	/**
	 * Loads a profile while using the supplied profile for missing or invalid
	 * values. This keeps legacy and manually edited saves loadable.
	 */
	public static MachineUpgradeProfile load (ValueInput input, MachineUpgradeProfile fallback) {
		Objects.requireNonNull(input, "input");
		Objects.requireNonNull(fallback, "fallback");
		return new MachineUpgradeProfile(readMultiplier(input, PROCESSING_SPEED_TAG, fallback.processingSpeedMultiplier), readMultiplier(input, ENERGY_CAPACITY_TAG, fallback.energyCapacityMultiplier), readMultiplier(input, ENERGY_INPUT_TAG, fallback.energyInputMultiplier), readMultiplier(input, ENERGY_OUTPUT_TAG, fallback.energyOutputMultiplier), readMultiplier(input, FLUID_CAPACITY_TAG, fallback.fluidCapacityMultiplier), readMultiplier(input, FLUID_TRANSFER_TAG, fallback.fluidTransferMultiplier));
	}

	public boolean isIdentity () {
		return equals(IDENTITY);
	}

	/**
	 * Multiplies two independent upgrade sources together.
	 */
	public MachineUpgradeProfile combine (MachineUpgradeProfile other) {
		Objects.requireNonNull(other, "other");
		return new MachineUpgradeProfile(processingSpeedMultiplier * other.processingSpeedMultiplier, energyCapacityMultiplier * other.energyCapacityMultiplier, energyInputMultiplier * other.energyInputMultiplier, energyOutputMultiplier * other.energyOutputMultiplier, fluidCapacityMultiplier * other.fluidCapacityMultiplier, fluidTransferMultiplier * other.fluidTransferMultiplier);
	}

	public int scaleEnergyCapacity (int baseCapacity) {
		return scale("Energy capacity", baseCapacity, energyCapacityMultiplier);
	}

	public int scaleEnergyInput (int baseInput) {
		return scale("Energy input", baseInput, energyInputMultiplier);
	}

	public int scaleEnergyOutput (int baseOutput) {
		return scale("Energy output", baseOutput, energyOutputMultiplier);
	}

	public int scaleFluidCapacity (int baseCapacity) {
		return scale("Fluid capacity", baseCapacity, fluidCapacityMultiplier);
	}

	public int scaleFluidTransfer (int baseTransfer) {
		return scale("Fluid transfer", baseTransfer, fluidTransferMultiplier);
	}

	private static int scale (String name, int base, double multiplier) {
		if (base < 0) throw new IllegalArgumentException(name + " base value cannot be negative: " + base);
		if (base == 0) return 0;

		double scaled = Math.ceil(base * multiplier);
		if (!Double.isFinite(scaled) || scaled > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(name + " exceeds the integer range after applying " + multiplier + "x to " + base);
		}
		return (int) scaled;
	}

	private static double requireMultiplier (String name, double multiplier) {
		if (!Double.isFinite(multiplier) || multiplier <= 0.0D) {
			throw new IllegalArgumentException(name + " multiplier must be finite and positive: " + multiplier);
		}
		return multiplier;
	}

	private static double readMultiplier (ValueInput input, String tag, double fallback) {
		double multiplier = input.getDoubleOr(tag, fallback);
		return Double.isFinite(multiplier) && multiplier > 0.0D ? multiplier : fallback;
	}

	/**
	 * Builder with identity defaults. Multipliers below {@code 1x} are valid and
	 * can describe deliberate tradeoffs or downgrades.
	 */
	public static final class Builder {

		private double processingSpeed = 1.0D;
		private double energyCapacity  = 1.0D;
		private double energyInput     = 1.0D;
		private double energyOutput    = 1.0D;
		private double fluidCapacity   = 1.0D;
		private double fluidTransfer   = 1.0D;

		private Builder () {
		}

		private Builder (MachineUpgradeProfile profile) {
			processingSpeed = profile.processingSpeedMultiplier;
			energyCapacity = profile.energyCapacityMultiplier;
			energyInput = profile.energyInputMultiplier;
			energyOutput = profile.energyOutputMultiplier;
			fluidCapacity = profile.fluidCapacityMultiplier;
			fluidTransfer = profile.fluidTransferMultiplier;
		}

		public Builder processingSpeed (double multiplier) {
			processingSpeed = requireMultiplier("Processing speed", multiplier);
			return this;
		}

		public Builder energyCapacity (double multiplier) {
			energyCapacity = requireMultiplier("Energy capacity", multiplier);
			return this;
		}

		public Builder energyInput (double multiplier) {
			energyInput = requireMultiplier("Energy input", multiplier);
			return this;
		}

		public Builder energyOutput (double multiplier) {
			energyOutput = requireMultiplier("Energy output", multiplier);
			return this;
		}

		/**
		 * Sets energy input and output to the same transfer multiplier.
		 */
		public Builder energyTransfer (double multiplier) {
			return energyInput(multiplier).energyOutput(multiplier);
		}

		public Builder fluidCapacity (double multiplier) {
			fluidCapacity = requireMultiplier("Fluid capacity", multiplier);
			return this;
		}

		public Builder fluidTransfer (double multiplier) {
			fluidTransfer = requireMultiplier("Fluid transfer", multiplier);
			return this;
		}

		public Builder energy (double capacityMultiplier, double inputMultiplier, double outputMultiplier) {
			return energyCapacity(capacityMultiplier).energyInput(inputMultiplier).energyOutput(outputMultiplier);
		}

		public Builder fluid (double capacityMultiplier, double transferMultiplier) {
			return fluidCapacity(capacityMultiplier).fluidTransfer(transferMultiplier);
		}

		public MachineUpgradeProfile build () {
			return new MachineUpgradeProfile(processingSpeed, energyCapacity, energyInput, energyOutput, fluidCapacity, fluidTransfer);
		}
	}
}
