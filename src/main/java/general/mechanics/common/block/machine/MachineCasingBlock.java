package general.mechanics.common.block.machine;

import general.api.block.BaseBlock;
import general.api.model.IBasicModel;
import general.api.tag.CoreTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.ArrayList;
import java.util.List;

public class MachineCasingBlock extends BaseBlock implements IBasicModel {

	public MachineCasingBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE));
	}

	@Override
	public List<TagKey<Block>> getBlockTags () {
		var tags = new ArrayList<>(super.getBlockTags());
		tags.add(CoreTags.Blocks.CORE_MATRICES);
		return List.copyOf(tags);
	}

}
