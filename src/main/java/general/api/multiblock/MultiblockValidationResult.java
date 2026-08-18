package general.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record MultiblockValidationResult(Status status, MultiblockInstance instance, BlockPos failedPosition, MultiblockElement expected, BlockState found) {

	public enum Status {
		VALID,
		INVALID,
		UNLOADED
	}

	public boolean valid () {
		return status == Status.VALID;
	}

	public boolean unloaded () {
		return status == Status.UNLOADED;
	}

	public static MultiblockValidationResult valid (MultiblockInstance instance) {
		return new MultiblockValidationResult(Status.VALID, instance, null, null, null);
	}

	public static MultiblockValidationResult invalid (BlockPos position, MultiblockElement expected, BlockState found) {
		return new MultiblockValidationResult(Status.INVALID, null, position, expected, found);
	}

	public static MultiblockValidationResult unloaded (BlockPos position, MultiblockElement expected) {
		return new MultiblockValidationResult(Status.UNLOADED, null, position, expected, null);
	}
}
