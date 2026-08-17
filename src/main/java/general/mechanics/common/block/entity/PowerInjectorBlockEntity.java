package general.mechanics.common.block.entity;

import general.api.network.INetworkInterface;
import general.api.network.NetworkEndpoint;
import general.api.network.NetworkNode;
import general.mechanics.common.network.NetworkConnectorServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PowerInjectorBlockEntity  extends BlockEntity implements INetworkInterface {

	private final NetworkNode networkNode;
	private       Direction   serviceDirection;

	public PowerInjectorBlockEntity (BlockEntityType<PowerInjectorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.networkNode = new NetworkNode("PowerInjector");
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
