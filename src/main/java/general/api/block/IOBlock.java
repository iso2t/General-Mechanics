package general.api.block;

import general.api.block.util.IPickaxe;
import general.api.model.IMachineModel;
import lombok.Getter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class IOBlock extends BaseBlock implements IWrenchable, IMachineModel, IPickaxe {

	@Getter
	private final IOMode mode;

	@Getter
	private final IOType type;

	public IOBlock (Properties properties, IOMode mode, IOType type) {
		super(properties);
		this.mode = mode;
		this.type = type;
	}

	public enum IOType {
		ITEM,
		FLUID,
		POWER,
		NETWORK,
		ANY
	}

	public enum IOMode {
		INPUT,
		OUTPUT,
		ANY
	}

	@Override
	public TagKey<Block> getMiningTier () {
		return BlockTags.NEEDS_IRON_TOOL;
	}
}
