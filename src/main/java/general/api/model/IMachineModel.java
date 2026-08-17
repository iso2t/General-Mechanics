package general.api.model;

import general.api.resources.Resource;
import net.minecraft.resources.Identifier;

public interface IMachineModel {

	default Identifier getTopTexture () {
		return Resource.getMainMod("block/machine/machine_top");
	}

	default Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/machine_side");
	}

	default Identifier getFrontTexture () {
		return getSideTexture();
	}

	default Identifier getBottomTexture () {
		return Resource.getMainMod("block/machine/machine_bottom");
	}

}
