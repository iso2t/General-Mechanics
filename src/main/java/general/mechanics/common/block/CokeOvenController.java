package general.mechanics.common.block;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.block.util.ILitProvider;
import general.api.crafting.IRecipeProvider;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.mechanics.common.block.entity.CokeOvenControllerBlockEntity;
import general.mechanics.registries.GenBlocks;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CokeOvenController extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<CokeOvenControllerBlockEntity>, IWrenchable, IMachineModel, IRotatableBlock, IRecipeProvider, ILitProvider {

	private BlockEntityType<CokeOvenControllerBlockEntity> blockEntityType;

	public CokeOvenController (Properties properties) {
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(LIT, false));
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

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		var path = Resource.getFromBlock(this).getPath();
		ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("BBB")
				.pattern("BSB")
				.pattern("BBB")
				.define('B', GenBlocks.COKE_OVEN_BRICKS)
				.define('S', Blocks.BLAST_FURNACE)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey(path + "_from_bricks"));
	}

	@Override
	public ItemLike getCriterionItem () {
		return GenBlocks.COKE_OVEN_BRICKS;
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/coke_oven_controller_lit");
	}

	@Override
	public void animateTick (@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
		if (!(level.getBlockEntity(pos) instanceof CokeOvenControllerBlockEntity entity) || !state.getValue(LIT) || !entity.isMultiblockFormed()) return;

		double xPos = pos.getX() + 0.5D;
		double yPos = pos.getY();
		double zPos = pos.getZ() + 0.5D;

		if (random.nextDouble() < 0.1D) level.playLocalSound(pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f, 1f, true);

		var direction = getFacing(state);
		if (direction.isEmpty()) return;
		var axis = direction.get().getAxis();

		double defaultOffset = random.nextDouble() * 0.6 - 0.3;
		double xOffsets = axis == Direction.Axis.X ? (double) direction.get().getStepX() * 0.52 : defaultOffset;
		double yOffsets = random.nextDouble() * 6.0 / 8.0;
		double zOffsets = axis == Direction.Axis.Z ? (double) direction.get().getStepZ() * 0.52 : defaultOffset;

		var stack = entity.getInputStack();
		if (!stack.isEmpty()) {
			level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack.getItem()), xPos + xOffsets, yPos + yOffsets, zPos + zOffsets, 0d, 0d, 0d);
		}
	}
}
