package general.mechanics.item.tools;

import general.api.item.ToolItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;

public class WireCuttersItem extends ToolItem {

	public WireCuttersItem (Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		/*ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("PFP")
				.pattern("HPS")
				.pattern("RBR")
				.define('P', GenParts.STEEL.get().getPlateItem())
				.define('F', CoreTags.Items.FILES)
				.define('H', CoreTags.Items.HAMMERS)
				.define('S', CoreTags.Items.SOCKET_DRIVERS)
				.define('R', GenParts.STEEL.get().getRodItem())
				.define('B', CoreTags.Items.BOLTS)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/wire_cutters"));*/
	}
}