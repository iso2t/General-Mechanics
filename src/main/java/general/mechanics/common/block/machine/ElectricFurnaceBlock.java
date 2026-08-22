package general.mechanics.common.block.machine;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.block.util.ILitProvider;
import general.api.block.util.IPickaxe;
import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.api.model.IMachineModel;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.mechanics.common.block.entity.ElectricFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ElectricFurnaceBlock extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<ElectricFurnaceBlockEntity>, IWrenchable, IMachineModel, IRotatableBlock, RecipeDataProvider, ILitProvider, IPickaxe {

	public ElectricFurnaceBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON));
	}

	@Override
	public void setBlockEntity (Class<ElectricFurnaceBlockEntity> blockEntityClass, BlockEntityType<ElectricFurnaceBlockEntity> blockEntityType) {

	}

	@Override
	public Identifier getLitTexture () {
		return null;
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {

	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return null;
	}

	@Override
	public BlockRotationStrategy getRotationStrategy () {
		return BlockRotationStrategies.HORIZONTAL_FACING;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		return null;
	}
}
