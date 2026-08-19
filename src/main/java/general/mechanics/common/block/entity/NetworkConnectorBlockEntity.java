package general.mechanics.common.block.entity;

import general.api.capabilities.GeneralCapabilities;
import general.api.network.INetworkInterface;
import general.api.network.NetworkEndpoint;
import general.api.network.NetworkNode;
import general.mechanics.common.network.NetworkConnectorServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public class NetworkConnectorBlockEntity extends BlockEntity implements INetworkInterface {

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<NetworkConnectorBlockEntity> type) {
		event.registerBlockEntity(GeneralCapabilities.NETWORK_HANDLER_BLOCK, type, (connector, side) -> side != Direction.UP ? connector : null);
	}

	private final NetworkNode networkNode;
	private Direction serviceDirection;

	public NetworkConnectorBlockEntity (BlockEntityType<NetworkConnectorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.networkNode = new NetworkNode("NetworkConnector");
	}

	@Override
	public NetworkNode getNetworkNode () {
		return networkNode;
	}

	@Override
	public void onLoad () {
		super.onLoad();
		if (refreshServices()) {
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
	}

	@Override
	public void setRemoved () {
		networkNode.getServices().clear();
		super.setRemoved();
	}

	public boolean refreshServices () {
		if (level == null || level.isClientSide()) return false;
		boolean wasEnabled = serviceDirection != null;
		serviceDirection = null;

		if (NetworkConnectorServices.refresh(networkNode, level, worldPosition.relative(Direction.UP), Direction.UP.getOpposite())) {
			serviceDirection = Direction.UP;
			return !wasEnabled;
		}
		return wasEnabled;
	}

	@Override
	public boolean isNetworkEnabled () {
		refreshServices();
		return serviceDirection != null;
	}

	@Override
	public List<NetworkEndpoint> getNetworkEndpoints () {
		refreshServices();
		return serviceDirection == null ? List.of() : List.of(new NetworkEndpoint(worldPosition.relative(serviceDirection)));
	}
}
