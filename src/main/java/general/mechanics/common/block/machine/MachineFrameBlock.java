package general.mechanics.common.block.machine;

import general.api.block.BaseBlock;
import general.api.block.IWrenchable;
import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;

public class MachineFrameBlock extends BaseBlock implements IMachineModel, IWrenchable, RecipeDataProvider {

	public MachineFrameBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE));
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/frame/side");
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.MISC, this, 1).pattern("LLL").pattern("SLS").pattern("SSS").define('L', GenBlocks.LIMESTONE_POLISHED).define('S', GenItems.STEEL), "machine_frame_block");
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return GenItems.STEEL;
	}
}
