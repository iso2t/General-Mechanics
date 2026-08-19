package general.api.network.util;

import general.api.capabilities.GeneralCapabilities;
import general.api.network.INetworkInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class NetworkHelper {

	private NetworkHelper () {
	}

	public static INetworkInterface getInterface (Level level, BlockPos pos, Direction direction) {
		return level.getCapability(GeneralCapabilities.NETWORK_HANDLER_BLOCK, pos, direction);
	}

	public static boolean canConnect (Level level, BlockPos pos, Direction direction) {
		INetworkInterface first = getInterface(level, pos, direction);

		if (first == null) {
			return false;
		}

		INetworkInterface second = getInterface(level, pos.relative(direction), direction.getOpposite());

		return second != null;
	}

}
