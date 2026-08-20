package general.api.block.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface IMiningTier {

	TagKey<Block> getMiningTier ();

	TagKey<Block> getRequiredTool ();

}
