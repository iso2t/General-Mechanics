package general.api.item;

import general.api.crafting.IRecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * An item that has an in-line recipe definition.
 */
public abstract class RecipeProviderItem extends Item implements IRecipeProvider, IItemTagsProvider {

	public RecipeProviderItem (Properties properties) {
		super(properties);
	}

	@Override
	public List<TagKey<Item>> getItemTags () {
		return List.of();
	}

}
