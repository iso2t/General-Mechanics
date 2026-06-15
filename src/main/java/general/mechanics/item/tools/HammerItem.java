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

public class HammerItem extends ToolItem {

	public HammerItem(Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		/*ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("II ")
				.pattern("IIS")
				.pattern("II ")
				.define('I', CoreElements.STEEL_INGOT.get())
				.define('S', Items.STICK)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/hammer"));*/
	}
}
