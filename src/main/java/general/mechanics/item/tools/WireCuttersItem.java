package general.mechanics.item.tools;

import general.api.crafting.RecipeGenerationContext;
import general.api.item.ToolItem;

public class WireCuttersItem extends ToolItem {

	public WireCuttersItem (Properties properties) {
		super(properties, 143);
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
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
