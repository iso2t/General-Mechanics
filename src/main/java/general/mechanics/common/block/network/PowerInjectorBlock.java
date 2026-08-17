package general.mechanics.common.block.network;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.mechanics.common.block.entity.PowerInjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PowerInjectorBlock extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<PowerInjectorBlockEntity>, IMachineModel, IRotatableBlock, IWrenchable {

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

	@Override
	public BlockRotationStrategy getRotationStrategy () {
		return BlockRotationStrategies.HORIZONTAL_FACING;
	}
}
