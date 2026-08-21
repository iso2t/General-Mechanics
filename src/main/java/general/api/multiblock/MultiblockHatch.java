package general.api.multiblock;

import general.api.block.IOBlock;

/**
 * A controller-bound block entity that can occupy a hatchable multiblock casing
 * position.
 */
public interface MultiblockHatch extends MultiblockAttachment {

	IOBlock.IOType getHatchType ();

	IOBlock.IOMode getHatchMode ();
}
