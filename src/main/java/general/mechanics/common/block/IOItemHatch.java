package general.mechanics.common.block;

import general.api.block.IOBlock;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.MultiblockHatchBlockEntity;
import net.minecraft.resources.Identifier;

public class IOItemHatch extends IOBlock<MultiblockHatchBlockEntity> {

	public IOItemHatch (Properties properties, IOMode mode) {
		super(properties, mode, IOType.ITEM);
	}

	@Override
	public Identifier getSideTexture () {
		return switch (getMode()) {
			case INPUT -> Resource.getMainMod("block/machine/machine_bottom_item");
			default -> Resource.getMainMod("block/machine/machine_bottom_item_output");
		};
	}

	@Override
	public Identifier getTopTexture () {
		return getSideTexture();
	}

	@Override
	public Identifier getBottomTexture () {
		return getSideTexture();
	}
}
