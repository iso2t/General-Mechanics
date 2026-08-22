package general.api.machine.power;

/**
 * Storage limits and processing economics for a machine operating tier.
 *
 * <p>Recipe duration supplies the base work. Speed changes the number of work
 * ticks, while the energy multiplier changes total energy independently. This
 * preserves a predictable upgrade model: speed normally raises RF/t because the
 * same work is completed sooner, and efficiency can reduce total RF.</p>
 */
public record MachinePowerProfile(int capacity, int maxInput, int baseEnergyPerTick, double speedMultiplier, double energyMultiplier) {

	public MachinePowerProfile {
		if (capacity <= 0) throw new IllegalArgumentException("Machine energy capacity must be positive: " + capacity);
		if (maxInput <= 0) throw new IllegalArgumentException("Machine maximum energy input must be positive: " + maxInput);
		if (maxInput > capacity) throw new IllegalArgumentException("Machine maximum energy input cannot exceed capacity");
		if (baseEnergyPerTick <= 0) throw new IllegalArgumentException("Machine base energy per tick must be positive: " + baseEnergyPerTick);
		if (!Double.isFinite(speedMultiplier) || speedMultiplier <= 0) throw new IllegalArgumentException("Machine speed multiplier must be finite and positive: " + speedMultiplier);
		if (!Double.isFinite(energyMultiplier) || energyMultiplier <= 0) throw new IllegalArgumentException("Machine energy multiplier must be finite and positive: " + energyMultiplier);
	}

	public static MachinePowerProfile base (int capacity, int maxInput, int baseEnergyPerTick) {
		return new MachinePowerProfile(capacity, maxInput, baseEnergyPerTick, 1.0D, 1.0D);
	}

	public MachinePowerProfile withSpeed (double speedMultiplier) {
		return new MachinePowerProfile(capacity, maxInput, baseEnergyPerTick, speedMultiplier, energyMultiplier);
	}

	public MachinePowerProfile withEnergyMultiplier (double energyMultiplier) {
		return new MachinePowerProfile(capacity, maxInput, baseEnergyPerTick, speedMultiplier, energyMultiplier);
	}

	public MachinePowerProfile withModifiers (double speedMultiplier, double energyMultiplier) {
		return new MachinePowerProfile(capacity, maxInput, baseEnergyPerTick, speedMultiplier, energyMultiplier);
	}

	public MachinePowerPlan plan (int baseDuration) {
		if (baseDuration <= 0) throw new IllegalArgumentException("Base machine duration must be positive: " + baseDuration);

		double scaledDuration = Math.ceil(baseDuration / speedMultiplier);
		if (scaledDuration > Integer.MAX_VALUE) throw new IllegalArgumentException("Effective machine duration exceeds integer range: " + scaledDuration);
		int effectiveDuration = Math.max(1, (int) scaledDuration);

		long baseTotal = Math.multiplyExact((long) baseDuration, baseEnergyPerTick);
		double scaledTotal = Math.ceil(baseTotal * energyMultiplier);
		if (!Double.isFinite(scaledTotal) || scaledTotal > Long.MAX_VALUE) throw new IllegalArgumentException("Effective machine energy exceeds long range: " + scaledTotal);
		long effectiveTotal = Math.max(1L, (long) scaledTotal);
		return new MachinePowerPlan(effectiveDuration, effectiveTotal);
	}
}
