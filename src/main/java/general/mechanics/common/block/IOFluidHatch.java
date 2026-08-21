package general.mechanics.common.block;

import general.api.block.IOBlock;
import general.api.resources.Resource;
import net.minecraft.resources.Identifier;

public class IOFluidHatch extends IOBlock {

	public IOFluidHatch (Properties properties, IOMode mode) {
		super(properties, mode, IOType.FLUID);
	}

	@Override
	public Identifier getSideTexture () {
		return switch (getMode()) {
			case INPUT -> Resource.getMainMod("block/machine/machine_bottom_fluid");
			default -> Resource.getMainMod("block/machine/machine_bottom_fluid_output");
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
