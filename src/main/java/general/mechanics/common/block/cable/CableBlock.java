package general.mechanics.common.block.cable;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.capabilities.Capabilities;
import general.mechanics.common.block.entity.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class CableBlock extends BaseBlock implements SimpleWaterloggedBlock, EntityBlock, BlockEntityTypeOwner<CableBlockEntity> {

	public static final EnumProperty<ConnectorType> NORTH = EnumProperty.create("north", ConnectorType.class);
	public static final EnumProperty<ConnectorType> SOUTH = EnumProperty.create("south", ConnectorType.class);
	public static final EnumProperty<ConnectorType> WEST  = EnumProperty.create("west", ConnectorType.class);
	public static final EnumProperty<ConnectorType> EAST  = EnumProperty.create("east", ConnectorType.class);
	public static final EnumProperty<ConnectorType> UP    = EnumProperty.create("up", ConnectorType.class);
	public static final EnumProperty<ConnectorType> DOWN  = EnumProperty.create("down", ConnectorType.class);

	public static final ModelProperty<BlockState> FACADEID = new ModelProperty<>();

	private static VoxelShape[]                                              shapeCache = null;
	private BlockEntityType<CableBlockEntity> blockEntityType;

	private static final VoxelShape SHAPE_CABLE_NORTH = Shapes.box(.4, .4, 0, .6, .6, .4);
	private static final VoxelShape SHAPE_CABLE_SOUTH = Shapes.box(.4, .4, .6, .6, .6, 1);
	private static final VoxelShape SHAPE_CABLE_WEST  = Shapes.box(0, .4, .4, .4, .6, .6);
	private static final VoxelShape SHAPE_CABLE_EAST  = Shapes.box(.6, .4, .4, 1, .6, .6);
	private static final VoxelShape SHAPE_CABLE_UP    = Shapes.box(.4, .6, .4, .6, 1, .6);
	private static final VoxelShape SHAPE_CABLE_DOWN  = Shapes.box(.4, 0, .4, .6, .4, .6);

	private static final VoxelShape SHAPE_BLOCK_NORTH = Shapes.box(.2, .2, 0, .8, .8, .1);
	private static final VoxelShape SHAPE_BLOCK_SOUTH = Shapes.box(.2, .2, .9, .8, .8, 1);
	private static final VoxelShape SHAPE_BLOCK_WEST  = Shapes.box(0, .2, .2, .1, .8, .8);
	private static final VoxelShape SHAPE_BLOCK_EAST  = Shapes.box(.9, .2, .2, 1, .8, .8);
	private static final VoxelShape SHAPE_BLOCK_UP    = Shapes.box(.2, .9, .2, .8, 1, .8);
	private static final VoxelShape SHAPE_BLOCK_DOWN  = Shapes.box(.2, 0, .2, .8, .1, .8);

	public CableBlock (Properties properties) {
		super(properties);
		makeShapes();
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	private int calculateShapeIndex (ConnectorType north, ConnectorType south, ConnectorType west, ConnectorType east, ConnectorType up, ConnectorType down) {
		int l = ConnectorType.values().length;
		return ((((south.ordinal() * l + north.ordinal()) * l + west.ordinal()) * l + east.ordinal()) * l + up.ordinal()) * l + down.ordinal();
	}

	private void makeShapes () {
		if (shapeCache == null) {
			int length = ConnectorType.values().length;
			shapeCache = new VoxelShape[length * length * length * length * length * length];

			for (ConnectorType up : ConnectorType.VALUES) {
				for (ConnectorType down : ConnectorType.VALUES) {
					for (ConnectorType north : ConnectorType.VALUES) {
						for (ConnectorType south : ConnectorType.VALUES) {
							for (ConnectorType east : ConnectorType.VALUES) {
								for (ConnectorType west : ConnectorType.VALUES) {
									int idx = calculateShapeIndex(north, south, west, east, up, down);
									shapeCache[idx] = makeShape(north, south, west, east, up, down);
								}
							}
						}
					}
				}
			}

		}
	}

	private VoxelShape makeShape (ConnectorType north, ConnectorType south, ConnectorType west, ConnectorType east, ConnectorType up, ConnectorType down) {
		VoxelShape shape = Shapes.box(.4, .4, .4, .6, .6, .6);
		shape = combineShape(shape, north, SHAPE_CABLE_NORTH, SHAPE_BLOCK_NORTH);
		shape = combineShape(shape, south, SHAPE_CABLE_SOUTH, SHAPE_BLOCK_SOUTH);
		shape = combineShape(shape, west, SHAPE_CABLE_WEST, SHAPE_BLOCK_WEST);
		shape = combineShape(shape, east, SHAPE_CABLE_EAST, SHAPE_BLOCK_EAST);
		shape = combineShape(shape, up, SHAPE_CABLE_UP, SHAPE_BLOCK_UP);
		shape = combineShape(shape, down, SHAPE_CABLE_DOWN, SHAPE_BLOCK_DOWN);
		return shape;
	}

	private VoxelShape combineShape (VoxelShape shape, ConnectorType connectorType, VoxelShape cableShape, VoxelShape blockShape) {
		if (connectorType == ConnectorType.CABLE) {
			return Shapes.join(shape, cableShape, BooleanOp.OR);
		} else if (connectorType == ConnectorType.BLOCK) {
			return Shapes.join(shape, Shapes.join(blockShape, cableShape, BooleanOp.OR), BooleanOp.OR);
		} else {
			return shape;
		}
	}

	@Nonnull
	@Override
	public VoxelShape getShape (@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		ConnectorType north = getConnectorType(world, pos, Direction.NORTH);
		ConnectorType south = getConnectorType(world, pos, Direction.SOUTH);
		ConnectorType west = getConnectorType(world, pos, Direction.WEST);
		ConnectorType east = getConnectorType(world, pos, Direction.EAST);
		ConnectorType up = getConnectorType(world, pos, Direction.UP);
		ConnectorType down = getConnectorType(world, pos, Direction.DOWN);
		int index = calculateShapeIndex(north, south, west, east, up, down);
		return shapeCache[index];
	}

	@Nonnull
	@Override
	public BlockState updateShape (BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return calculateState((LevelAccessor) level, pos, state);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return new CableBlockEntity(blockEntityType, blockPos, blockState);
	}

	@Override
	public void setBlockEntity (Class<CableBlockEntity> blockEntityClass, BlockEntityType<CableBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public void neighborChanged (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Block block, @Nullable Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CableBlockEntity cable) {
			cable.markDirty();
		}
	}

	@Override
	public void setPlacedBy (@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CableBlockEntity cable) {
			cable.markDirty();
		}
		BlockState blockState = calculateState(level, pos, state);
		if (state != blockState) {
			level.setBlockAndUpdate(pos, blockState);
		}
	}

	// Return the connector type for the given position and facing direction
	private ConnectorType getConnectorType (BlockGetter world, BlockPos connectorPos, Direction facing) {
		BlockPos pos = connectorPos.relative(facing);
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof CableBlock) {
			return ConnectorType.CABLE;
		} else if (isConnectable(world, connectorPos, facing)) {
			return ConnectorType.BLOCK;
		} else {
			return ConnectorType.NONE;
		}
	}

	// Return true if the block at the given position is connectable to a cable. This is the
	// case if the block supports forge energy
	public static boolean isConnectable (BlockGetter world, BlockPos connectorPos, Direction facing) {
		BlockPos pos = connectorPos.relative(facing);
		BlockState state = world.getBlockState(pos);
		// Datagen and model/shape probes use lightweight BlockGetters (such as
		// EmptyBlockGetter), which cannot expose capabilities. Treat those as
		// non-connectable instead of assuming every BlockGetter is a live Level.
		if (!(world instanceof Level level)) {
			return false;
		}
		if (state.isAir()) {
			return false;
		}
		BlockEntity te = world.getBlockEntity(pos);
		if (te == null) {
			return false;
		}
		var networkInterface = level.getCapability(Capabilities.NETWORK_HANDLER_BLOCK, pos, facing.getOpposite());
		return networkInterface != null && networkInterface.isNetworkEnabled();
	}

	@Override
	protected void createBlockStateDefinition (@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED, NORTH, SOUTH, EAST, WEST, UP, DOWN);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement (BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		return calculateState(world, pos, defaultBlockState()).setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Nonnull
	private BlockState calculateState (LevelAccessor world, BlockPos pos, BlockState state) {
		ConnectorType north = getConnectorType(world, pos, Direction.NORTH);
		ConnectorType south = getConnectorType(world, pos, Direction.SOUTH);
		ConnectorType west = getConnectorType(world, pos, Direction.WEST);
		ConnectorType east = getConnectorType(world, pos, Direction.EAST);
		ConnectorType up = getConnectorType(world, pos, Direction.UP);
		ConnectorType down = getConnectorType(world, pos, Direction.DOWN);

		return state.setValue(NORTH, north).setValue(SOUTH, south).setValue(WEST, west).setValue(EAST, east).setValue(UP, up).setValue(DOWN, down);
	}

	@Nonnull
	@Override
	public FluidState getFluidState (BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

}
