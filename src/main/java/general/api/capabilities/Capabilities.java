package general.api.capabilities;

import general.api.network.INetworkInterface;
import general.api.resources.Resource;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class Capabilities {

	/**
	 * For blocks that can be networked together.
	 */
	public static final BlockCapability<INetworkInterface, @Nullable Direction> NETWORK_HANDLER_BLOCK = BlockCapability.createSided(Resource.get("network_interface"), INetworkInterface.class);

}
