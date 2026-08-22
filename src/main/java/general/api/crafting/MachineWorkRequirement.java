package general.api.crafting;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

/**
 * Transactional resource requirement applied once for each advancing recipe
 * tick.
 *
 * <p>Implementations must stage mutations in the supplied transaction and return
 * {@code false} unless the complete tick cost was staged. The processor commits
 * ordinary work ticks directly and combines the final cost with recipe input and
 * output transfer in the same root transaction.</p>
 */
@FunctionalInterface
public interface MachineWorkRequirement {

	MachineWorkRequirement FREE = (recipe, completedTicks, transaction) -> true;

	/**
	 * Effective operation duration. Requirements that do not modify speed inherit
	 * the recipe duration.
	 */
	default int duration (MachineRecipe recipe) {
		return Objects.requireNonNull(recipe, "recipe").duration();
	}

	/**
	 * Stages the cost of the next work tick.
	 *
	 * @param completedTicks number of work ticks already completed for this operation
	 */
	boolean consume (MachineRecipe recipe, int completedTicks, TransactionContext transaction);

	static MachineWorkRequirement free () {
		return FREE;
	}
}
