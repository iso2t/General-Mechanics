package general.mechanics.common.block.machine;

import general.api.block.BaseBlock;
import general.api.block.IWrenchable;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SoundType;

public class MachineFrameBlock extends BaseBlock implements IMachineModel, IWrenchable {

	public MachineFrameBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE));
	}

	@Override
	public Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/frame/side");
	}
}
