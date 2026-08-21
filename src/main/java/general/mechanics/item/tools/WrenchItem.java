package general.mechanics.item.tools;

import general.api.crafting.RecipeGenerationContext;
import general.api.item.ToolItem;

public class WrenchItem extends ToolItem {

	public WrenchItem (Properties properties) {
		super(properties, 143);
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		/*ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("PHP")
				.pattern(" R ")
				.pattern(" R ")
				.define('P', GenParts.STEEL.get().getPlateItem())
				.define('H', CoreTags.Items.HAMMERS)
				.define('R', GenParts.STEEL.get().getRodItem())
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/wrench"));*/
	}
}
