package general.mechanics.item.tools;

import general.api.crafting.IRecipeProvider;
import general.api.item.ToolItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;

public class SocketDriverItem extends ToolItem {

	public SocketDriverItem(Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		/*ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("HPI")
				.pattern("PRP")
				.pattern("RPF")
				.define('H', CoreTags.Items.HAMMERS)
				.define('P', CoreTags.Items.PLASTIC)
				.define('I', CoreElements.STAINLESS_STEEL_INGOT.get())
				.define('R', CoreElements.STEEL_INGOT.get().getRodItem())
				.define('F', CoreTags.Items.FILES)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/socket_driver"));*/
	}
}
