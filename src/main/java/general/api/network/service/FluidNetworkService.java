package general.api.network.service;

import general.api.network.NetworkServices;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Predicate;

public interface FluidNetworkService extends NetworkService {

	int insert (FluidStack stack, boolean simulate);

	FluidStack extract (Predicate<FluidStack> filter, int amount, boolean simulate);

	List<FluidStack> getAvailableFluids ();

	@Override
	default NetworkServiceType<FluidNetworkService> getType () {
		return NetworkServices.FLUID;
	}
}
