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

import java.util.Properties;

public class WrenchItem extends ToolItem {

	public WrenchItem (Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("PHP")
				.pattern(" R ")
				.pattern(" R ")
				.define('P', GenParts.STEEL.get().getPlateItem())
				.define('H', CoreTags.Items.HAMMERS)
				.define('R', GenParts.STEEL.get().getRodItem())
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/wrench"));
	}
}
