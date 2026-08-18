package general.api.block.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface IAxe extends IMiningTier {

	@Override
	default TagKey<Block> getMiningTier () {
		return BlockTags.NEEDS_STONE_TOOL;
	}

	@Override
	default TagKey<Block> getRequiredTool () {
		return BlockTags.MINEABLE_WITH_AXE;
	}

}
