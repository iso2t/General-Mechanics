package general.api.machine;

import general.api.definitions.MultiblockDefinition;
import general.api.multiblock.MultiblockController;
import general.api.multiblock.MultiblockInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Opt-in controller behavior for a machine that can form a multiblock.
 */
public interface MachineMultiblock extends MachineHost, MultiblockController {

	@Override
	default MultiblockDefinition getMultiblockDefinition () {
		return machine().requireMultiblockDefinition();
	}

	@Override
	default BlockPos getMultiblockPosition () {
		if (this instanceof MachineBlockEntity blockEntity) return blockEntity.getBlockPos();
		throw new IllegalStateException("Machine multiblock owner is not a block entity");
	}

	@Override
	default Direction getMultiblockFacing () {
		return machine().getMultiblockFacing();
	}

	@Override
	default boolean isMultiblockFormed () {
		return machine().isFormed();
	}

	@Override
	default void setMultiblockFormed (boolean formed) {
		machine().setFormed(formed);
	}

	@Override
	default void onMultiblockValidated (MultiblockInstance instance) {
		machine().onMultiblockValidated(instance);
	}

	@Override
	default void onMultiblockFormed (MultiblockInstance instance) {
		machine().onMultiblockFormed(instance);
	}
}
