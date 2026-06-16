package general.mechanics.item.tools;

import general.api.crafting.IRecipeProvider;
import general.api.item.ToolItem;
import general.api.tag.CoreTags;
import general.mechanics.registries.GenParts;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;

public class PhillipsScrewdriverItem extends ToolItem {

	public PhillipsScrewdriverItem(Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("HPI")
				.pattern("PRP")
				.pattern("RPF")
				.define('H', CoreTags.Items.HAMMERS)
				.define('P', CoreTags.Items.PLASTIC)
				.define('I', GenParts.STEEL.get().getNuggetItem())
				.define('R', GenParts.STEEL.get().getRodItem())
				.define('F', CoreTags.Items.FILES)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/phillips_screwdriver"));
	}
}
