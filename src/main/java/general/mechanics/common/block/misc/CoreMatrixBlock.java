package general.mechanics.common.block.misc;

import general.api.block.DecorativeBlock;
import general.api.tag.CoreTags;
import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Upgrade material used by machines whose multiblock core requires one uniform
 * matrix tier.
 */
@Getter
public class CoreMatrixBlock extends DecorativeBlock {

	private final double processingSpeedMultiplier;

	public CoreMatrixBlock (double processingSpeedMultiplier, Properties properties) {
		super(properties);
		if (!Double.isFinite(processingSpeedMultiplier) || processingSpeedMultiplier <= 0.0D) {
			throw new IllegalArgumentException("Core matrix processing speed must be finite and positive: " + processingSpeedMultiplier);
		}
		this.processingSpeedMultiplier = processingSpeedMultiplier;
	}

	@Override
	public List<TagKey<Block>> getBlockTags () {
		var tags = new ArrayList<>(super.getBlockTags());
		tags.add(CoreTags.Blocks.CORE_MATRICES);
		return List.copyOf(tags);
	}
}
