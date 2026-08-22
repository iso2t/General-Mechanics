package general.api.machine.power;

/**
 * Immutable energy schedule for one machine operation.
 *
 * <p>The total is distributed exactly across the effective duration. Tick costs
 * may differ by at most one RF, avoiding cumulative rounding overpayment.</p>
 */
public record MachinePowerPlan(int duration, long totalEnergy) {

	public MachinePowerPlan {
		if (duration <= 0) throw new IllegalArgumentException("Machine power plan duration must be positive: " + duration);
		if (totalEnergy <= 0) throw new IllegalArgumentException("Machine power plan total energy must be positive: " + totalEnergy);
		if (peakEnergyPerTick(duration, totalEnergy) > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Machine power plan requires more than " + Integer.MAX_VALUE + " RF in one tick");
		}
	}

	/**
	 * Exact cost for the next tick after {@code completedTicks} work ticks.
	 */
	public int energyForTick (int completedTicks) {
		if (completedTicks < 0 || completedTicks >= duration) {
			throw new IndexOutOfBoundsException("Completed work ticks " + completedTicks + " are outside [0, " + duration + ")");
		}
		long base = totalEnergy / duration;
		long remainder = totalEnergy % duration;
		return Math.toIntExact(base + (completedTicks < remainder ? 1 : 0));
	}

	/**
	 * Highest scheduled cost, suitable for RF/t display and capacity checks.
	 */
	public int peakEnergyPerTick () {
		return Math.toIntExact(peakEnergyPerTick(duration, totalEnergy));
	}

	private static long peakEnergyPerTick (int duration, long totalEnergy) {
		return Math.ceilDiv(totalEnergy, duration);
	}
}
