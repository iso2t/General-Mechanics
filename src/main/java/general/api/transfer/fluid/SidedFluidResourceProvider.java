package general.api.transfer.fluid;

import general.api.transfer.SidedResourceHandlers;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

/**
 * Fluid capability provider backed by prebuilt, cached side views.
 */
public interface SidedFluidResourceProvider extends FluidResourceProvider {

	SidedResourceHandlers<FluidResource> getSidedFluidHandlers ();

	default ResourceHandler<FluidResource> getFluidHandler (@Nullable Direction side) {
		return getSidedFluidHandlers().forSide(side);
	}
}
