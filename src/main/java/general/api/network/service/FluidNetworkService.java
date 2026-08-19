package general.api.network.service;

import general.api.network.NetworkServices;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Predicate;

/**
 * Network-level fluid transfer service preserving fluid data components.
 * Simulations use NeoForge transactions and never commit backing changes.
 */
public interface FluidNetworkService extends NetworkService {

	/**
	 * @return the amount accepted by the exposed capability
	 */
	int insert (FluidStack stack, boolean simulate);

	/**
	 * Extracts one component-identical fluid resource, potentially across tanks.
	 */
	FluidStack extract (Predicate<FluidStack> filter, int amount, boolean simulate);

	/**
	 * Snapshot of fluids the exposed capability can currently extract.
	 */
	List<FluidStack> getAvailableFluids ();

	@Override
	default NetworkServiceType<FluidNetworkService> getType () {
		return NetworkServices.FLUID;
	}
}
