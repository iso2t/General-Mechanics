package general.api.crafting;

import general.api.resources.Resource;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.stream.Stream;

public interface IRecipeProvider {

	void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion);

	/**
	 * The item used to build the recipe's unlock criterion.
	 */
	ItemLike getCriterionItem ();

	static ResourceKey<Recipe<?>> createKey (String path) {
		return ResourceKey.create(Registries.RECIPE, Resource.get(path));
	}

	static Stream<ItemLike> getItemFromTag (TagKey<Item> tag) {
		return BuiltInRegistries.ITEM.getOrThrow(tag).stream().map(holder -> (ItemLike) holder.value());
	}

}
