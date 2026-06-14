package general.api.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public interface IItemTagsProvider {

	List<TagKey<Item>> getItemTags ();

}
