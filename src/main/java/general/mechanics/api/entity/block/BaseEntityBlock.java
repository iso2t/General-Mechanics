package general.mechanics.api.entity.block;

import com.mojang.serialization.MapCodec;
import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.tags.CoreTags;
import general.mechanics.api.util.data.PropertyComponent;
import general.mechanics.api.util.data.PropertyHelper;
import general.mechanics.registries.CoreComponents;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.CRAFTING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public abstract class BaseEntityBlock<T extends BaseBlockEntity> extends BaseBlock implements EntityBlock {

    private Class<T> blockEntityClass;

    @Getter
    private             BlockEntityType<T> blockEntityType;
    public static final EnumProperty<Direction>       FACING      = BlockStateProperties.FACING;
    private             boolean            disassemble = false;
    private final MapCodec<BaseBlock> codec = getCodec();

    public BaseEntityBlock (Properties properties) {
        super(properties);
    }

    public void setBlockEntity (Class<T> blockEntityClass, BlockEntityType<T> blockEntityType) {
        this.blockEntityClass = blockEntityClass;
        this.blockEntityType = blockEntityType;
    }

    @Nullable
    public T getBlockEntity (BlockGetter level, int x, int y, int z) {
        return this.getBlockEntity(level, new BlockPos(x, y, z));
    }

    @Nullable
    public T getBlockEntity (BlockGetter level, BlockPos pos) {
        final BlockEntity te = level.getBlockEntity(pos);
        if (this.blockEntityClass != null && this.blockEntityClass.isInstance(te)) {
            return this.blockEntityClass.cast(te);
        }

        return null;
    }

    public abstract MapCodec<BaseBlock> getCodec ();

    @Nullable
    @Override
    public BlockEntity newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return blockEntityType.create(pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        PropertyComponent<Direction> property = PropertyHelper.of(FACING, context.getNearestLookingDirection().getOpposite());
        return this.defaultBlockState().setValue(POWERED, false)
                .setValue(property.getProperty(), property.getValue())
                .setValue(CRAFTING, false);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED, FACING, CRAFTING);
    }

    @Override
    protected @NotNull RenderShape getRenderShape (@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    /*@Override
    protected void onRemove (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        remove(state, level, pos, newState, movedByPiston, getDisassembled());
        super.onRemove(state, level, pos, newState, movedByPiston);
    }*/

	@Override
	public boolean onDestroyedByPlayer (BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
		remove(state, level, pos, state, false, getDisassembled());
		return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
	}

	public void remove (BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston, boolean wrenched) {
        if (state.getBlock() != newState.getBlock()) {
            var blockentity = this.getBlockEntity(level, pos);
            if (blockentity != null) {
                if (!wrenched) {
                    blockentity.drops(blockentity.getInventory());
                } else {
                    blockentity.drops(blockentity.getInventory());
                }
            }
        }
    }

    /**
     * Called when the block is to be destroyed by a wrench
     */
    public void destroyBlockByWrench (Player player, BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean movedByPiston) {
        disassemble = true;
        playerWillDestroy(level, pos, state, player);
        remove(state, level, pos, state, movedByPiston, disassemble);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3, 512);
        disassemble = false;
    }

	// TODO: Not Supported anymore
    /*@Override
    public void appendHoverText (@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            if (stack.has(CoreComponents.ENERGY_STORED) && stack.has(CoreComponents.ENERGY_MAX)) {
                DecimalFormat format = new DecimalFormat("#,###");
                tooltipComponents.add(Component.literal("Stored: " + format.format(stack.get(CoreComponents.ENERGY_STORED)) + "/" + format.format(stack.get(CoreComponents.ENERGY_MAX)) + " FE"));
            }

            if (stack.has(CoreComponents.INVENTORY)) {
                var inventory = stack.get(CoreComponents.INVENTORY);
                for (int i = 0; i < Objects.requireNonNull(inventory).inventory().size(); i++) {
                    if (!inventory.inventory().get(i).is(Items.AIR)) {
                        var itemstack = inventory.inventory().get(i);
                        tooltipComponents.add(Component.translatable("gui.gm.itemlist", i + 1, itemstack.getCount(), itemstack.getDisplayName()));
                    }
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("gui.gm.press_shift"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }*/

    @Override
    protected @NotNull MapCodec<? extends Block> codec () {
        return codec;
    }

    @Override
    protected @NonNull InteractionResult useItemOn (ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (stack.is(CoreTags.Items.WRENCHES)) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean isSignalSource (@NotNull BlockState state) {
        return false;
    }

    public boolean getDisassembled () {
        return disassemble;
    }

    public Direction getConnectedDirection (BlockState state) {
        return switch (state.getValue(BlockStateProperties.ATTACH_FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> state.getValue(FACING);
        };
    }

}
