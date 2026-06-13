package general.mechanics.api.block.machine;

import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.entity.DisassemblyHandler;
import general.mechanics.api.item.plastic.PlasticType;
import general.mechanics.util.RomanNumeral;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@Getter
public class MachineFrameBlock extends BaseBlock implements DisassemblyHandler {

	// Connectivity to adjacent MachineFrame blocks in each cardinal direction
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty EAST  = BooleanProperty.create("east");
	public static final BooleanProperty WEST  = BooleanProperty.create("west");
	public static final BooleanProperty UP    = BooleanProperty.create("up");
	public static final BooleanProperty DOWN  = BooleanProperty.create("down");

	private final PlasticType plasticType;
	private final int         level;

	public MachineFrameBlock (Properties properties, PlasticType plasticType) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));

		this.plasticType = plasticType;
		this.level = plasticType.ordinal() + 1;
	}

	public MachineFrameBlock (PlasticType plasticType) {
		this(Properties.ofFullCopy(Blocks.IRON_BLOCK), plasticType);
	}

	@Override
	protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
	}

	@Override
	public @Nullable BlockState getStateForPlacement (@NotNull BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		BlockGetter level = context.getLevel();
		return this.defaultBlockState().setValue(NORTH, isSame(level, pos.relative(Direction.NORTH))).setValue(SOUTH, isSame(level, pos.relative(Direction.SOUTH))).setValue(EAST, isSame(level, pos.relative(Direction.EAST))).setValue(WEST, isSame(level, pos.relative(Direction.WEST))).setValue(UP, isSame(level, pos.relative(Direction.UP))).setValue(DOWN, isSame(level, pos.relative(Direction.DOWN)));
	}

	@Override
	protected @NonNull BlockState updateShape (@NonNull BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, Direction direction, @NonNull BlockPos neighborPos, @NonNull BlockState neighborState, @NonNull RandomSource random) {
		return switch (direction) {
			case NORTH -> state.setValue(NORTH, isSame(level, neighborPos));
			case SOUTH -> state.setValue(SOUTH, isSame(level, neighborPos));
			case EAST -> state.setValue(EAST, isSame(level, neighborPos));
			case WEST -> state.setValue(WEST, isSame(level, neighborPos));
			case UP -> state.setValue(UP, isSame(level, neighborPos));
			case DOWN -> state.setValue(DOWN, isSame(level, neighborPos));
		};
	}

	@Override
	public void appendHoverText (@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
		builder.accept(Component.literal(getPlasticType().getAbbreviation()));
		builder.accept(Component.literal("§e" + getPlasticType().getFormula()));
		if (Minecraft.getInstance().hasShiftDown()) builder.accept(Component.literal("§oLevel: " + RomanNumeral.toRoman(level)));
		else builder.accept(Component.translatable("gui.gm.press_shift"));
		super.appendHoverText(stack, context, toolTip, builder, tooltipFlag);
	}

	private boolean isSame (BlockGetter level, BlockPos pos) {
		var state = level.getBlockState(pos);
		return state.getBlock() instanceof MachineFrameBlock;
	}

	@Override
	public InteractionResult disassemble (Player player, Level level, BlockHitResult hitResult, ItemStack stack, @Nullable ItemStack existingData) {
		level.destroyBlock(hitResult.getBlockPos(), true, player);
		return InteractionResult.SUCCESS;
	}
}
