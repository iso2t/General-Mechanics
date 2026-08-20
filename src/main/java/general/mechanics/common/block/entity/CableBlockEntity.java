package general.mechanics.common.block.entity;

import general.api.capabilities.GeneralCapabilities;
import general.api.network.INetworkInterface;
import general.api.network.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CableBlockEntity extends BlockEntity implements INetworkInterface {

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<CableBlockEntity> type) {
		event.registerBlockEntity(GeneralCapabilities.NETWORK_HANDLER_BLOCK, type, (cable, side) -> cable);
	}

	private final NetworkNode networkNode;

	public CableBlockEntity (BlockEntityType<CableBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.networkNode = new NetworkNode("Cable");
	}

	public void markDirty () {
		//traverse(worldPosition, cable -> cable.outputs = null);
	}

	@Override
	public NetworkNode getNetworkNode () {
		return networkNode;
	}
}
