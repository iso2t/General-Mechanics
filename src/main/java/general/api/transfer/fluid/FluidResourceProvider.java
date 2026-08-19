package general.api.transfer.fluid;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Distinct fluid provider contract that can coexist with {@code ItemResourceProvider}.
 */
public interface FluidResourceProvider {

	ResourceHandler<FluidResource> getFluidHandler ();

	FluidInventoryDefinition getFluidDefinition ();

	default void onFluidsChanged (int index, FluidResource previousResource, int previousAmount, FluidResource resource, int amount) {
	}
}
