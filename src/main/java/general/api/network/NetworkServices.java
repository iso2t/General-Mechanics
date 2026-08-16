package general.api.network;

import general.api.network.service.*;
import general.api.resources.Resource;

public final class NetworkServices {

	public static final NetworkServiceType<ItemNetworkService> ITEM = new NetworkServiceType<>(Resource.getMainMod("items"), ItemNetworkService.class);

	public static final NetworkServiceType<FluidNetworkService> FLUID = new NetworkServiceType<>(Resource.getMainMod("fluid"), FluidNetworkService.class);

	public static final NetworkServiceType<EnergyNetworkService> ENERGY = new NetworkServiceType<>(Resource.getMainMod("energy"), EnergyNetworkService.class);

	public static final NetworkServiceType<ControlNetworkService> CONTROL = new NetworkServiceType<>(Resource.getMainMod("control"), ControlNetworkService.class);

	public static final NetworkServiceType<DataNetworkService> DATA = new NetworkServiceType<>(Resource.getMainMod("data"), DataNetworkService.class);

	private NetworkServices () {
	}

}
