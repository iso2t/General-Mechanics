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

	/**
	 * Retrieves the rotation strategy defining how the block's orientation is managed.
	 * <p>
	 * The returned strategy determines the block's orientation behavior, property registration,
	 * placement rules, and rotation mechanics. This method is expected to provide a consistent,
	 * predefined strategy for each block implementing the IRotatableBlock interface.
	 *
	 * @return the block's rotation strategy, encapsulating its orientation logic.
	 */
	BlockRotationStrategy getRotationStrategy ();


	/**
	 * Rotates the provided block state according to the block's rotation strategy.
	 *
	 * @param state   the current {@code BlockState} to rotate
	 * @param reverse whether the rotation should proceed in the reverse direction
	 * @return the resulting {@code BlockState} after applying the rotation
	 */
	default BlockState rotateBlock (BlockState state, boolean reverse) {
		return getRotationStrategy().rotate(state, reverse);
	}

	/**
	 * Retrieves the direction this block is facing, based on its rotation strategy.
	 * This method delegates to the block's rotation strategy to determine the facing direction.
	 * For some strategies (e.g., axis-based), no specific facing exists, and an empty result is returned.
	 *
	 * @param state the current {@code BlockState} of the block
	 * @return an {@code Optional} containing the facing {@code Direction} if applicable, or an empty {@code Optional} if not
	 */
	default Optional<Direction> getFacing (BlockState state) {
		return getRotationStrategy().getFacing(state);
	}
}
