package general.mechanics.common.block.entity;

import general.api.definitions.MultiblockDefinition;
import general.api.multiblock.MultiblockController;
import general.api.multiblock.MultiblockHandler;
import general.api.multiblock.MultiblockInstance;
import general.mechanics.registries.GenMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

/** Authoritative multiblock controller state for a coke oven. */
public class CokeOvenControllerBlockEntity extends BlockEntity implements MultiblockController {

	private boolean formed;

	public CokeOvenControllerBlockEntity (BlockEntityType<CokeOvenControllerBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public MultiblockDefinition getMultiblockDefinition () {
		return GenMultiblocks.COKE_OVEN;
	}

	@Override
	public BlockPos getMultiblockPosition () {
		return getBlockPos();
	}

	@Override
	public Direction getMultiblockFacing () {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
	}

	@Override
	public boolean isMultiblockFormed () {
		return formed;
	}

	@Override
	public void setMultiblockFormed (boolean formed) {
		this.formed = formed;
	}

	@Override
	public void onMultiblockFormed (MultiblockInstance instance) {
		if (getLevel() instanceof ServerLevel serverLevel) {
			MultiblockHandler.spawnFormationParticles(serverLevel, instance);
		}
	}

	@Override
	public InteractionResult onFormedMultiblockUse (Player player, BlockHitResult hitResult, MultiblockInstance instance) {
		player.sendSystemMessage(Component.literal("I'm a little teapot!")); // TODO: You're not a teapot. I just had no idea  what to put for testing purposes.
		return MultiblockController.super.onFormedMultiblockUse(player, hitResult, instance);
	}

	@Override
	protected void saveAdditional (@NonNull ValueOutput output) {
		super.saveAdditional(output);
		output.putBoolean(FORMED_TAG, formed);
	}

	@Override
	protected void loadAdditional (@NonNull ValueInput input) {
		super.loadAdditional(input);
		formed = input.getBooleanOr(FORMED_TAG, false);
	}

}
