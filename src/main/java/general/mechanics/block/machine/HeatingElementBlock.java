package general.mechanics.block.machine;

import com.mojang.serialization.MapCodec;
import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.entity.block.BaseBlockEntity;
import general.mechanics.api.entity.block.BaseEntityBlock;
import general.mechanics.api.util.data.PropertyComponent;
import general.mechanics.api.util.data.PropertyHelper;
import general.mechanics.entity.block.HeatingElementBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.enums.BubbleColumnDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class HeatingElementBlock extends BaseEntityBlock<HeatingElementBlockEntity> {

    public static final BooleanProperty HEATING = BooleanProperty.create("heating");

    public HeatingElementBlock(Properties properties) {
        this();
    }

    public HeatingElementBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK));
        registerDefaultState(getStateDefinition().any().setValue(HEATING, false));
    }

    @Override
    public MapCodec<BaseBlock> getCodec() {
        return simpleCodec(HeatingElementBlock::new);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
        if (state.getValue(HEATING)) BubbleColumnBlock.updateColumn(level, pos.above(), state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return ((lvl, pos, blockState, type) -> {
            if (type instanceof BaseBlockEntity)
                ((BaseBlockEntity) type).tick(lvl, pos, blockState);
        });
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        PropertyComponent<Direction> property = PropertyHelper.of(FACING, context.getNearestLookingDirection().getOpposite());
        return this.defaultBlockState().setValue(property.getProperty(), property.getValue())
                .setValue(HEATING, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, HEATING);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity)) return;

        if (!isHeating(state)) return;
        entity.hurt(level.damageSources().campfire(), 1.0F);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(1.0F, 0.0F, 1.0F, 15.0F, 15.0F, 15.0F);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!isHeating(state)) return;

        double xPos = (double) pos.getX() + 0.5;
        double yPos = pos.getY();
        double zPos = (double) pos.getZ() + 0.5;

        Direction direction = Direction.UP;
        Direction.Axis axis = direction.getAxis();

        double defaultOffset = random.nextDouble() * 0.6 - 0.3;
        double xOffsets = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : defaultOffset;
        double yOffsets = random.nextDouble() * 6.0 / 8.0;
        double zOffsets = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : defaultOffset;

        if (blockAbove(level, pos).is(Blocks.AIR)) {
            level.addParticle(ParticleTypes.SMOKE, xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
        }

        if (random.nextDouble() < 0.1) {
            level.playLocalSound(xPos, yPos, zPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        super.animateTick(state, level, pos, random);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public @NotNull BubbleColumnDirection getBubbleColumnDirection(@NotNull BlockState state) {
        if (state.is(this) && isHeating(state)) {
            return BubbleColumnDirection.UPWARD;
        }
        return BubbleColumnDirection.NONE;
    }

    @Override
	protected @NonNull BlockState updateShape (@NonNull BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, @NonNull Direction direction, @NonNull BlockPos neighborPos, @NonNull BlockState neighborState, @NonNull RandomSource random) {
        if (direction == Direction.UP && neighborState.is(Blocks.WATER) && isHeating(state)) {
            level.scheduleTick(pos, this, 20);
        }

        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canConnectRedstone(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    protected BlockState blockAbove(Level level, BlockPos pos) {
        return level.getBlockState(pos.above());
    }

    protected boolean isHeating(BlockState state) {
        return state.is(this) && state.getValue(HEATING);
    }
}
