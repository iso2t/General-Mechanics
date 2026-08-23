package general.mechanics.common.block.misc;

import general.api.block.BaseBlock;
import lombok.Getter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.NonNull;

public class EncasedFluidBlock extends BaseBlock {

	@Getter
	private final FlowingFluid fluid;

	public EncasedFluidBlock (Properties properties, FlowingFluid fluid) {
		super(properties.noOcclusion().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE));
		this.fluid = fluid;
	}

	@Override
	public @NonNull FluidState getFluidState (@NonNull BlockState state) {
		return fluid.getSource(false);
	}

}
