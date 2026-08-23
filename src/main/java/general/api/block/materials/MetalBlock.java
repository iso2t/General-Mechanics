package general.api.block.materials;

import general.api.block.BaseBlock;
import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.api.item.materials.IngotItem;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;

import java.util.Objects;
import java.util.function.Supplier;

public class MetalBlock extends BaseBlock implements IMachineModel, RecipeDataProvider {

	private final Supplier<? extends IngotItem> ingot;

	public MetalBlock (Properties properties, Supplier<? extends IngotItem> ingot) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
		this.ingot = Objects.requireNonNull(ingot, "ingot");
	}

	public IngotItem getIngot () {
		return ingot.get();
	}

	public int getColor () {
		return getIngot().getColor();
	}

	@Override
	public Identifier getTopTexture () {
		return Resource.getMainMod("block/metal_block");
	}

	@Override
	public Identifier getSideTexture () {
		return getTopTexture();
	}

	@Override
	public Identifier getBottomTexture () {
		return getTopTexture();
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		var path = context.ownerId().getPath();
		context.save(ShapedRecipeBuilder.shaped(context.items(), RecipeCategory.BUILDING_BLOCKS, this).pattern("III").pattern("III").pattern("III").define('I', this::getIngot), "materials/" + path + "_from_ingots");
		context.save(ShapelessRecipeBuilder.shapeless(context.items(), RecipeCategory.MISC, this::getIngot, 9).requires(this), "materials/ingots_from_" + path);
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return this::getIngot;
	}
}
