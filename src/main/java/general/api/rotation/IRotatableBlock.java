package general.api.rotation;

import general.api.block.BaseBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Opts a {@link BaseBlock} into a reusable orientation strategy.
 *
 * <p>The strategy automatically contributes its blockstate property and placement behavior.
 * Implementations should return a constant strategy, because blockstate properties are registered
 * while the block is being constructed.</p>
 */
public interface IRotatableBlock {

	BlockRotationStrategy getRotationStrategy ();

	/**
	 * Convenience entry point for the future wrench interaction.
	 */
	default BlockState rotateBlock (BlockState state, boolean reverse) {
		return getRotationStrategy().rotate(state, reverse);
	}

	/** Returns this block's logical front, when its strategy defines one. */
	default Optional<Direction> getFacing (BlockState state) {
		return getRotationStrategy().getFacing(state);
	}
}
