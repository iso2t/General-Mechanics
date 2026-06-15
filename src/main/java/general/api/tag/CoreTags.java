package general.api.tag;

import general.api.resources.Resource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CoreTags {

	public static class Items {
		public static final TagKey<Item> PLASTIC = createTag("plastic");
		public static final TagKey<Item> PLASTIC_BLOCKS = createTag("plastic_blocks");

		// Plastic type specific tags
		public static final TagKey<Item> POLYETHYLENE = createTag("polyethylene");
		public static final TagKey<Item> POLYPROPYLENE = createTag("polypropylene");
		public static final TagKey<Item> POLYSTYRENE = createTag("polystyrene");
		public static final TagKey<Item> POLYVINYL_CHLORIDE = createTag("polyvinyl_chloride");
		public static final TagKey<Item> POLYETHYLENE_TEREPHTHALATE = createTag("polyethylene_terephthalate");
		public static final TagKey<Item> ACRYLONITRILE_BUTADIENE_STYRENE = createTag("acrylonitrile_butadiene_styrene");
		public static final TagKey<Item> POLYCARBONATE = createTag("polycarbonate");
		public static final TagKey<Item> NYLON = createTag("nylon");
		public static final TagKey<Item> POLYURETHANE = createTag("polyurethane");
		public static final TagKey<Item> POLYTETRAFLUOROETHYLENE = createTag("polytetrafluoroethylene");
		public static final TagKey<Item> POLYETHERETHERKETONE = createTag("polyetheretherketone");

		public static final TagKey<Item> PLATES = createTag("plates");
		public static final TagKey<Item> RODS  = createTag("rods");
		public static final TagKey<Item> PILES = createTag("piles");

		public static final TagKey<Item> WRENCHES = createTag("wrenches");
		public static final TagKey<Item> FLATHEAD_SCREWDRIVERS = createTag("flathead_screwdrivers");
		public static final TagKey<Item> PHILLIPS_SCREWDRIVERS = createTag("philips_screwdrivers");
		public static final TagKey<Item> HAMMERS = createTag("hammers");
		public static final TagKey<Item> SOCKET_DRIVERS = createTag("socket_drivers");
		public static final TagKey<Item> SAWS = createTag("saws");
		public static final TagKey<Item> WIRE_CUTTERS = createTag("wire_cutters");
		public static final TagKey<Item> FILES = createTag("files");

		public static final TagKey<Item> CARBON = createTag("carbon");

		public static final TagKey<Item> BOLTS = createTag("bolts");
		public static final TagKey<Item> SCREWS = createTag("screws");

		private static TagKey<Item> createTag (String key) {
			return ItemTags.create(Resource.get(key));
		}
	}

	public static class Blocks {
		public static final TagKey<Block> PLASTIC = createTag("plastic");

		private static TagKey<Block> createTag (String key) {
			return BlockTags.create(Resource.get(key));
		}
	}

}
