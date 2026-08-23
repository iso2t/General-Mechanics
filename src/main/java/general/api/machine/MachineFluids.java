package general.api.machine;

import general.api.transfer.SidedResourceHandlers;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.SidedFluidResourceProvider;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Opt-in fluid-storage feature for a machine block entity.
 */
public interface MachineFluids extends MachineHost, SidedFluidResourceProvider {

	@Override
	default ResourceHandler<FluidResource> getFluidHandler () {
		return machine().requireFluidHandler();
	}

	@Override
	default FluidInventoryDefinition getFluidDefinition () {
		return machine().requireFluidDefinition();
	}

	@Override
	default SidedResourceHandlers<FluidResource> getSidedFluidHandlers () {
		return machine().requireSidedFluidHandlers();
	}
}
