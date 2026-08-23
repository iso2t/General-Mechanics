package general.mechanics.common.block.misc;

import general.api.block.DecorativeBlock;
import general.api.machine.upgrade.MachineUpgradeProfile;
import general.api.machine.upgrade.MachineUpgradeProvider;
import general.api.tag.CoreTags;
import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Upgrade material used by machines whose multiblock core requires one uniform
 * matrix tier.
 */
@Getter
public class CoreMatrixBlock extends DecorativeBlock implements MachineUpgradeProvider {

	private final MachineUpgradeProfile upgradeProfile;

	/**
	 * Compatibility constructor for speed-only matrix definitions.
	 */
	public CoreMatrixBlock (double processingSpeedMultiplier, Properties properties) {
		this(MachineUpgradeProfile.speed(processingSpeedMultiplier), properties);
	}

	public CoreMatrixBlock (MachineUpgradeProfile upgradeProfile, Properties properties) {
		super(properties);
		this.upgradeProfile = Objects.requireNonNull(upgradeProfile, "upgradeProfile");
	}

	/**
	 * Compatibility accessor for consumers that only understand processing speed.
	 */
	public double getProcessingSpeedMultiplier () {
		return upgradeProfile.processingSpeedMultiplier();
	}

	@Override
	public List<TagKey<Block>> getBlockTags () {
		var tags = new ArrayList<>(super.getBlockTags());
		tags.add(CoreTags.Blocks.CORE_MATRICES);
		return List.copyOf(tags);
	}
}
