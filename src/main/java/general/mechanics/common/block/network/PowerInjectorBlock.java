package general.mechanics.common.block.network;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.PowerInjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PowerInjectorBlock extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<PowerInjectorBlockEntity>, IMachineModel {

	private BlockEntityType<PowerInjectorBlockEntity> blockEntityType;

	public PowerInjectorBlock (Properties properties) {
		super(properties);
	}

	@Override
	public void setBlockEntity (Class<PowerInjectorBlockEntity> blockEntityClass, BlockEntityType<PowerInjectorBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return new PowerInjectorBlockEntity(blockEntityType, blockPos, blockState);
	}

	@Override
	public void neighborChanged (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Block changedBlock, @Nullable Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, changedBlock, orientation, movedByPiston);
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PowerInjectorBlockEntity connector) {
			if (connector.refreshServices()) {
				level.updateNeighborsAt(pos, this);
			}
		}
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/machine_side_network");
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/machine_side_power");
	}

	@Override
	public Identifier getTopTexture () {
		return Resource.getMainMod("block/machine/machine_top_power");
	}

	@Override
	public Identifier getBottomTexture () {
		return Resource.getMainMod("block/machine/machine_bottom_power");
	}
}
