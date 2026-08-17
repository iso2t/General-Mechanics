package general.api.rotation;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.Optional;

/**
 * Defines how a block stores, chooses, and cycles its orientation.
 *
 * <p>Strategies own the orientation property so blocks can share the same placement and wrench
 * rotation rules without each block duplicating blockstate code.</p>
 */
public interface BlockRotationStrategy {

	/** Adds this strategy's orientation property to the subscribing block's state definition. */
	void addProperties (StateDefinition.Builder<Block, BlockState> builder);

	/** Returns the state to use when a block is placed. */
	BlockState getStateForPlacement (BlockState state, BlockPlaceContext context);

	/** Returns the next orientation for a future wrench interaction. */
	BlockState rotate (BlockState state, boolean reverse);

	/**
	 * Resolves the block's front for side-aware logic such as capabilities.
	 * Axis-only strategies have no single front and return an empty result.
	 */
	default Optional<Direction> getFacing (BlockState state) {
		return Optional.empty();
	}
}
