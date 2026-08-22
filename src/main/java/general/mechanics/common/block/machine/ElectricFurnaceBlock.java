package general.mechanics.common.block.machine;

import general.api.block.BaseBlock;
import general.api.block.BlockEntityTypeOwner;
import general.api.block.IWrenchable;
import general.api.block.util.ILitProvider;
import general.api.block.util.IPickaxe;
import general.api.crafting.MachineRecipeSlot;
import general.api.crafting.MachineRecipeSlots;
import general.api.crafting.MachineRecipeDefinition;
import general.api.crafting.NoRecipeData;
import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.mechanics.common.block.entity.ElectricFurnaceBlockEntity;
import general.mechanics.registries.GenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ElectricFurnaceBlock extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<ElectricFurnaceBlockEntity>, IWrenchable, IMachineModel, IRotatableBlock, RecipeDataProvider, ILitProvider, IPickaxe {

	private BlockEntityType<ElectricFurnaceBlockEntity> blockEntityType;

	public ElectricFurnaceBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON));
		registerDefaultState(getStateDefinition().any().setValue(LIT, false));
	}

	@Override
	public void setBlockEntity (Class<ElectricFurnaceBlockEntity> blockEntityClass, BlockEntityType<ElectricFurnaceBlockEntity> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/electric_furnace/electric_furnace_lit");
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {

	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return Blocks.FURNACE;
	}

	@Override
	public BlockRotationStrategy getRotationStrategy () {
		return BlockRotationStrategies.HORIZONTAL_FACING;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
		if (blockEntityType == null) throw new IllegalStateException("Electric Furnace block entity type has not been bound yet");
		return blockEntityType.create(blockPos, blockState);
	}

	public static MachineRecipeDefinition<NoRecipeData> getRecipeDefinition () {
		return GenRecipes.ELECTRIC_FURNACE;
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/electric_furnace/electric_furnace");
	}

	/**
	 * Logical fields shared by Electric Furnace storage and its future custom
	 * recipe schema.
	 */
	public static final class RecipeSlots {

		public static final MachineRecipeSlot.ItemInput  INPUT     = MachineRecipeSlots.ITEM_INPUT;
		public static final MachineRecipeSlot.ItemInput  CATALYST  = MachineRecipeSlots.itemInput("catalyst");
		public static final MachineRecipeSlot.ItemOutput OUTPUT_1  = MachineRecipeSlots.ITEM_OUTPUT;
		public static final MachineRecipeSlot.ItemOutput OUTPUT_2  = MachineRecipeSlots.itemOutput(2);
		public static final MachineRecipeSlot.ItemOutput OUTPUT_3  = MachineRecipeSlots.itemOutput(3);
		public static final MachineRecipeSlot.ItemOutput OUTPUT_4  = MachineRecipeSlots.itemOutput(4);

		private RecipeSlots () {
		}
	}
}
