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

	/**
	 * Registers a set of crafting recipes using the given inputs.
	 *
	 * @param holder    The holder of {@link Item} instances, which acts as a registry or lookup
	 *                  mechanism for items that will be used in crafting recipes.
	 * @param consumer  A consumer for recipe outputs, which processes or stores recipes during registration.
	 * @param criterion A criterion for unlocking the crafting recipes, typically used for progression
	 *                  or achievement systems.
	 */
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
