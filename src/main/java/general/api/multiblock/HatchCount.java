package general.api.multiblock;

/**
 * Inclusive number of hatches accepted by one multiblock hatch route.
 */
public record HatchCount(int minimum, int maximum) {

	public HatchCount {
		if (minimum < 0) throw new IllegalArgumentException("Minimum hatch count cannot be negative: " + minimum);
		if (maximum < minimum) throw new IllegalArgumentException("Maximum hatch count " + maximum + " is below minimum " + minimum);
	}

	public static HatchCount exactly (int count) {
		return new HatchCount(count, count);
	}

	public static HatchCount between (int minimum, int maximum) {
		return new HatchCount(minimum, maximum);
	}

	public static HatchCount atMost (int maximum) {
		return new HatchCount(0, maximum);
	}

	public static HatchCount atLeast (int minimum) {
		return new HatchCount(minimum, Integer.MAX_VALUE);
	}

	public static HatchCount any () {
		return atLeast(0);
	}

	public boolean contains (int count) {
		return count >= minimum && count <= maximum;
	}
}
