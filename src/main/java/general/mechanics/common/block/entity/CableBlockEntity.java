package general.mechanics.common.block.entity;

import general.api.network.INetworkInterface;
import general.api.network.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CableBlockEntity extends BlockEntity implements INetworkInterface {

	private final NetworkNode networkNode;

	public CableBlockEntity (BlockEntityType<CableBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.networkNode = new NetworkNode("Cable");
	}

	public void markDirty() {
		//traverse(worldPosition, cable -> cable.outputs = null);
	}

	@Override
	public NetworkNode getNetworkNode () {
		return networkNode;
	}
}
