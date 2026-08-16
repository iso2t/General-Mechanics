package general.mechanics.common.block;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.model.IBasicModel;
import general.mechanics.common.block.entity.NetworkConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Allows connecting non-networked blocks to the network with limited interactivity.
 */
public class NetworkConnectorBlock extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<NetworkConnectorBlockEntity>, IBasicModel {

	private BlockEntityType<NetworkConnectorBlockEntity> blockEntityType;

	public NetworkConnectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void setBlockEntity (Class<NetworkConnectorBlockEntity> blockEntityClass, BlockEntityType<NetworkConnectorBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return new NetworkConnectorBlockEntity(blockEntityType, blockPos, blockState);
	}

	@Override
	public void neighborChanged (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Block changedBlock, @Nullable Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, changedBlock, orientation, movedByPiston);
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof NetworkConnectorBlockEntity connector) {
			connector.refreshServices();
		}
	}
}
