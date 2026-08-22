package general.api.crafting;

import general.api.machine.power.MachinePowerPlan;
import general.api.machine.power.MachinePowerProfile;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Machine work requirement that extracts an exact RF schedule from an energy
 * handler.
 */
public final class MachineEnergyWorkRequirement implements MachineWorkRequirement {

	private final EnergyHandler                               energy;
	private final Function<? super MachineRecipe, MachinePowerPlan> planner;

	private MachineEnergyWorkRequirement (EnergyHandler energy, Function<? super MachineRecipe, MachinePowerPlan> planner) {
		this.energy = Objects.requireNonNull(energy, "energy");
		this.planner = Objects.requireNonNull(planner, "planner");
	}

	public static MachineEnergyWorkRequirement fromProfile (EnergyHandler energy, MachinePowerProfile profile) {
		Objects.requireNonNull(profile, "profile");
		return fromPlanner(energy, recipe -> profile.plan(recipe.duration()));
	}

	/**
	 * Uses the current operating profile whenever a recipe tick is evaluated. A
	 * machine should reset active work before changing profiles if it requires one
	 * profile to remain fixed for an entire operation.
	 */
	public static MachineEnergyWorkRequirement fromProfile (EnergyHandler energy, Supplier<MachinePowerProfile> profile) {
		Objects.requireNonNull(profile, "profile");
		return fromPlanner(energy, recipe -> Objects.requireNonNull(profile.get(), "Machine power profile supplier returned null").plan(recipe.duration()));
	}

	/**
	 * Supports recipe-specific energy overrides while retaining the same processor.
	 */
	public static MachineEnergyWorkRequirement fromPlanner (EnergyHandler energy, Function<? super MachineRecipe, MachinePowerPlan> planner) {
		return new MachineEnergyWorkRequirement(energy, planner);
	}

	public MachinePowerPlan plan (MachineRecipe recipe) {
		return Objects.requireNonNull(planner.apply(Objects.requireNonNull(recipe, "recipe")), "Machine power planner returned null");
	}

	@Override
	public int duration (MachineRecipe recipe) {
		return plan(recipe).duration();
	}

	@Override
	public boolean consume (MachineRecipe recipe, int completedTicks, TransactionContext transaction) {
		Objects.requireNonNull(transaction, "transaction");
		int required = plan(recipe).energyForTick(completedTicks);
		return required == 0 || energy.extract(required, transaction) == required;
	}
}
