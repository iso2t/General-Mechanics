package general.mechanics.common.block.misc;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.mechanics.common.block.entity.HeatingElementBlockEntity;
import general.mechanics.datagen.data.GenDamageTypes;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class HeatingElementBlock extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<HeatingElementBlockEntity>, IWrenchable, RecipeDataProvider {

	public static final BooleanProperty HEATING = BooleanProperty.create("heating");

	private BlockEntityType<HeatingElementBlockEntity> blockEntityType;

	public HeatingElementBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE));
		registerDefaultState(defaultBlockState().setValue(HEATING, false));
	}

	@Override
	public void setBlockEntity (Class<HeatingElementBlockEntity> blockEntityClass, BlockEntityType<HeatingElementBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		if (blockEntityType == null) throw new IllegalStateException("Heating Element block entity type has not been bound yet");
		return blockEntityType.create(blockPos, blockState);
	}

	@Override
	public BlockState getStateForPlacement (@NonNull BlockPlaceContext context) {
		return this.defaultBlockState().setValue(HEATING, true);
	}

	@Override
	protected void createBlockStateDefinition (StateDefinition.@NonNull Builder<Block, BlockState> builder) {
		builder.add(HEATING);
	}

	@Override
	protected void tick (@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		super.tick(state, level, pos, random);
		if (state.getValue(HEATING) && level.getBlockEntity(pos) instanceof HeatingElementBlockEntity) {
			BubbleColumnBlock.updateColumn(Blocks.BUBBLE_COLUMN, level, pos.above(), state);
		}
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker (@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> type) {
		return ((lvl, pos, state, t) -> {
			if (t instanceof HeatingElementBlockEntity) ((HeatingElementBlockEntity) t).tick(lvl, pos, state);
		});
	}

	@Override
	protected void entityInside (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Entity entity, @NonNull InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		if (!(entity instanceof LivingEntity)) return;

		if (!isHeating(state)) return;
		if (!level.isClientSide() && level instanceof ServerLevel server) entity.hurtServer(server, GenDamageTypes.create(server, GenDamageTypes.HEATING_ELEMENT), 2F);
	}

	@Override
	protected @NonNull VoxelShape getCollisionShape (@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return Block.box(1.0F, 0.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	}

	@Override
	public void animateTick (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
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
			level.playLocalSound(xPos, yPos, zPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, true);
		}

		super.animateTick(state, level, pos, random);
	}

	@Override
	protected void onPlace (@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
		level.scheduleTick(pos, this, 20);
	}

	@Override
	public @NotNull BubbleColumnDirection getBubbleColumnDirection (@NotNull BlockState state) {
		if (state.is(this) && isHeating(state)) {
			return BubbleColumnDirection.UPWARD;
		}
		return BubbleColumnDirection.NONE;
	}

	@Override
	protected @NonNull BlockState updateShape (@NonNull BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, @NonNull Direction directionToNeighbour, @NonNull BlockPos neighbourPos, @NonNull BlockState neighbourState, @NonNull RandomSource random) {
		if (directionToNeighbour == Direction.UP && neighbourState.is(Blocks.WATER) && isHeating(state)) ticks.scheduleTick(pos, this, 20);
		return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
	}

	@Override
	public boolean canConnectRedstone (@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	protected BlockState blockAbove (Level level, BlockPos pos) {
		return level.getBlockState(pos.above());
	}

	protected boolean isHeating (BlockState state) {
		return state.is(this) && state.getValue(HEATING);
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.MISC, this, 1)
				.pattern("SSS")
				.pattern("BEB")
				.pattern("SSS")
				.define('E', GenBlocks.ENCASED_LAVA.get())
				.define('S', GenItems.STEEL.get().getPlateItem())
				.define('B', GenItems.STEEL),
				"heating_element");
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return GenBlocks.ENCASED_LAVA;
	}
}
