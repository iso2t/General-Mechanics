package general.api.multiblock;

import net.minecraft.core.BlockPos;

import java.util.Objects;

/**
 * Resolved, runtime-validated route for one controller-owned hatch.
 */
public record MultiblockHatchContext(MultiblockInstance instance, BlockPos hatchPosition, MultiblockHatchDefinition definition) {

	public MultiblockHatchContext {
		Objects.requireNonNull(instance, "instance");
		hatchPosition = Objects.requireNonNull(hatchPosition, "hatchPosition").immutable();
		Objects.requireNonNull(definition, "definition");
	}
}
