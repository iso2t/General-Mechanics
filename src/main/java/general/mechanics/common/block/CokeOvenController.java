package general.mechanics.common.block;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.mechanics.common.block.entity.CokeOvenControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CokeOvenController extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<CokeOvenControllerBlockEntity>, IWrenchable, IMachineModel, IRotatableBlock {

	private BlockEntityType<CokeOvenControllerBlockEntity> blockEntityType;

	public CokeOvenController (Properties properties) {
		super(properties);
	}

	@Override
	public void setBlockEntity (Class<CokeOvenControllerBlockEntity> blockEntityClass, BlockEntityType<CokeOvenControllerBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return new CokeOvenControllerBlockEntity(blockEntityType, blockPos, blockState);
	}

	@Override
	protected @NonNull InteractionResult useWithoutItem (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof CokeOvenControllerBlockEntity controller) {
			return controller.useMultiblock(player, hitResult);
		}
		return InteractionResult.PASS;
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/coke_oven_controller");
	}

	@Override
	public Identifier getBottomTexture () {
		return Resource.getMainMod("block/machine/coke_oven_controller_bottom");
	}

	@Override
	public Identifier getTopTexture () {
		return getBottomTexture();
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/coke_oven_controller_side");
	}

	@Override
	public BlockRotationStrategy getRotationStrategy () {
		return BlockRotationStrategies.HORIZONTAL_FACING;
	}
}
