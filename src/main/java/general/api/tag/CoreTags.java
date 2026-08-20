package general.api.tag;

import general.api.resources.Resource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CoreTags {

	public static class Items {

		public static final TagKey<Item> WRENCHES     = createTag("wrenches");
		public static final TagKey<Item> SAWS         = createTag("saws");
		public static final TagKey<Item> WIRE_CUTTERS = createTag("wire_cutters");

		private static TagKey<Item> createTag (String key) {
			return ItemTags.create(Resource.get(key));
		}
	}

	public static class Blocks {

		private static TagKey<Block> createTag (String key) {
			return BlockTags.create(Resource.get(key));
		}
	}

}
