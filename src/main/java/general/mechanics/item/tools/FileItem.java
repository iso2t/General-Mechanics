package general.mechanics.item.tools;

import general.api.crafting.IRecipeProvider;
import general.api.item.ToolItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class FileItem extends ToolItem {

	public FileItem (Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		/*ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("P")
				.pattern("P")
				.pattern("S")
				.define('P', CoreElements.STEEL_INGOT.get().getPlateItem())
				.define('S', Items.STICK)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/file"));*/
	}

}
