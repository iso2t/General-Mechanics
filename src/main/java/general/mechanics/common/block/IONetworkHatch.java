package general.mechanics.common.block;

import general.api.block.IOBlock;
import general.api.resources.Resource;
import net.minecraft.resources.Identifier;

public class IONetworkHatch extends IOBlock {

	public IONetworkHatch (Properties properties) {
		super(properties, IOMode.ANY, IOType.NETWORK);
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/machine_bottom_network");
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
