package general.api.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface IBlockTagsProvider {

	List<TagKey<Block>> getBlockTags ();

}
