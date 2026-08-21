package general.mechanics.common.block;

import general.api.block.IOBlock;
import general.api.resources.Resource;
import net.minecraft.resources.Identifier;

public class IOPowerHatch extends IOBlock {

	public IOPowerHatch (Properties properties) {
		super(properties, IOMode.ANY, IOType.POWER);
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/machine_bottom_power");
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
