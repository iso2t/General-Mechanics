package general.mechanics.entity.block;

import general.mechanics.api.capability.heat.IHeater;
import general.mechanics.api.entity.block.BaseBlockEntity;
import general.mechanics.api.inventory.CoreItemStackHandler;
import general.mechanics.block.machine.HeatingElementBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class HeatingElementBlockEntity extends BaseBlockEntity implements IHeater {

    public HeatingElementBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CoreItemStackHandler createInventory() {
        return new CoreItemStackHandler(0);
    }

    @Override
    public AbstractContainerMenu menu(int id, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) serverTick(state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.gm.industrial_heating_element");
    }

    public void updateHeating(boolean heating) {
        if (level == null) return;

        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(HeatingElementBlock.HEATING) != heating) {
            level.setBlock(worldPosition, state.setValue(HeatingElementBlock.HEATING, heating), 3);
        }
    }

    public void serverTick(BlockState state) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) return;

        updateHeating(isHeating(level, getBlockPos()));

        BlockPos furnacePos = getBlockPos().above();
        var be = level.getBlockEntity(furnacePos);
        if (!(be instanceof AbstractFurnaceBlockEntity furnace)) return;

        // forces to 0 instead of cooling down
        if (!state.getValue(HeatingElementBlock.HEATING)) {
            furnace.litTotalTime = 0;
            furnace.litTimeRemaining = 0;
            setFurnaceLit(level, furnace, false);
            furnace.setChanged();
            return;
        }

        final int chunk = burnChunkTicks(level, furnacePos);
        if (furnace.litTotalTime <= 0) {
            furnace.litTotalTime = chunk;
            furnace.litTimeRemaining = Math.max(furnace.litTimeRemaining, chunk);
            setFurnaceLit(level, furnace, true);
            furnace.setChanged();
        } else {
            furnace.litTotalTime = Math.min(furnace.litTotalTime + chunk, Math.max(furnace.litTimeRemaining, chunk));
            furnace.setChanged();
        }
    }

    /**
     * Turn the lit flag on/off on the block state (so the texture/particles match).
     */
    private void setFurnaceLit(Level level, AbstractFurnaceBlockEntity furnace, boolean lit) {
        var state = level.getBlockState(furnace.getBlockPos());
        var prop = BlockStateProperties.LIT;
        if (state.hasProperty(prop) && state.getValue(prop) != lit) {
            level.setBlock(furnace.getBlockPos(), state.setValue(prop, lit), 3);
        }
    }

    // ---- IHeater ----
    @Override
    public boolean isHeating(Level level, BlockPos furnacePos) {
        return level.hasNeighborSignal(getBlockPos());
    }

    @Override
    public int burnChunkTicks(Level level, BlockPos furnacePos) {
        return 200;
    }

}
