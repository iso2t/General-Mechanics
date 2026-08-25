package general.mechanics.common.block.machine;

import general.api.machine.MachineBlock;
import general.api.model.IConfigurableMachineModel;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.FluidInfuserBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SoundType;

/**
 * Powered container filler. Its operation is capability-driven rather than
 * recipe-driven because the tank's current fluid determines the result.
 */
public class FluidInfuserBlock extends MachineBlock<FluidInfuserBlockEntity> implements IConfigurableMachineModel {

	public FluidInfuserBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON), FluidInfuserBlockEntity.class, FluidInfuserBlockEntity.MACHINE);
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/fluid_infuser/fluid_infuser");
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/fluid_infuser/fluid_infuser_lit");
	}
}
