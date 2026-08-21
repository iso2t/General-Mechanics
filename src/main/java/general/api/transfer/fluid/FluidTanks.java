package general.api.transfer.fluid;

import general.api.transfer.ResourceSlotDefinition;
import general.api.transfer.ResourceSlotKey;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.function.Predicate;

/**
 * Readable fluid-unit and physical tank-definition helpers.
 */
public final class FluidTanks {

	/**
	 * NeoForge 26.1 fluid units in one bucket.
	 */
	public static final int BUCKET = FluidType.BUCKET_VOLUME;

	private FluidTanks () {
	}

	public static int buckets (int count) {
		if (count < 0) throw new IllegalArgumentException("Bucket count must be non-negative: " + count);
		return Math.multiplyExact(count, FluidType.BUCKET_VOLUME);
	}

	public static ResourceSlotDefinition<FluidResource> tank (String name, int capacity) {
		return ResourceSlotDefinition.named(name, capacity);
	}

	public static ResourceSlotDefinition<FluidResource> tank (ResourceSlotKey slot, int capacity) {
		return ResourceSlotDefinition.named(slot, capacity);
	}

	public static ResourceSlotDefinition<FluidResource> tank (String name, int capacity, Predicate<? super FluidResource> validator) {
		return ResourceSlotDefinition.named(name, capacity, validator);
	}

	public static ResourceSlotDefinition<FluidResource> tank (ResourceSlotKey slot, int capacity, Predicate<? super FluidResource> validator) {
		return ResourceSlotDefinition.named(slot, capacity, validator);
	}
}
