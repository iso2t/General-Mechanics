package general.mechanics.common.block.entity;

import general.api.block.IHeater;
import general.api.block.entity.BaseBlockEntity;
import general.mechanics.common.block.misc.HeatingElementBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class HeatingElementBlockEntity extends BaseBlockEntity implements IHeater {

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<HeatingElementBlockEntity> type) {
		// Doesn't have capabilities. Due to BE registration, this method must exist.
	}

	public HeatingElementBlockEntity (BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void updateHeating (boolean heating) {
		if (level == null) return;

		BlockState state = level.getBlockState(worldPosition);
		if (state.getValue(HeatingElementBlock.HEATING) != heating) {
			level.setBlock(worldPosition, state.setValue(HeatingElementBlock.HEATING, heating), 3);
		}
	}

	public void tick (Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) serverTick(state);
	}

	public void serverTick (BlockState state) {
		Level level = getLevel();
		if (level == null || level.isClientSide()) return;

		updateHeating(isHeating(level, getBlockPos()));

		BlockPos furnacePos = getBlockPos().above();
		var be = level.getBlockEntity(furnacePos);
		if (!(be instanceof AbstractFurnaceBlockEntity furnace)) return;

		// forces to 0 instead of cooling down
		if (!state.getValue(HeatingElementBlock.HEATING)) {
			furnace.litTimeRemaining = 0;
			furnace.litTotalTime = 0;
			setFurnaceLit(level, furnace, false);
			furnace.setChanged();
			return;
		}

		final int chunk = burnChunkTicks(level, furnacePos);
		if (furnace.litTimeRemaining <= 0) {
			furnace.litTimeRemaining = chunk;
			furnace.litTotalTime = Math.max(furnace.litTotalTime, chunk);
			setFurnaceLit(level, furnace, true);
			furnace.setChanged();
		} else {
			furnace.litTimeRemaining = Math.min(furnace.litTimeRemaining + chunk, Math.max(furnace.litTotalTime, chunk));
			furnace.setChanged();
		}
	}

	/**
	 * Turn the lit flag on/off on the block state (so the texture/particles match).
	 */
	private void setFurnaceLit (Level level, AbstractFurnaceBlockEntity furnace, boolean lit) {
		var state = level.getBlockState(furnace.getBlockPos());
		var prop = BlockStateProperties.LIT;
		if (state.hasProperty(prop) && state.getValue(prop) != lit) {
			level.setBlock(furnace.getBlockPos(), state.setValue(prop, lit), 3);
		}
	}

	@Override
	public boolean isHeating (Level level, BlockPos furnacePos) {
		return level.hasNeighborSignal(getBlockPos());
	}

	@Override
	public int burnChunkTicks (Level level, BlockPos furnacePos) {
		return 200;
	}

}
