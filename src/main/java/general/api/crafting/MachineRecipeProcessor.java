package general.api.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Reusable server-side recipe subscription and progress state for one machine.
 *
 * <p>The processor prefers its active recipe, falls back to other matching
 * recipes when that recipe's outputs are blocked, preserves progress while
 * blocked, and delegates final mutation to the binding's atomic transaction.
 * Persistence is self-contained through {@link #save(ValueOutput)} and
 * {@link #load(ValueInput)}.</p>
 */
public final class MachineRecipeProcessor {

	private static final String ACTIVE_RECIPE_TAG = "active_recipe";
	private static final String PROGRESS_TAG      = "progress";
	private static final String MAX_PROGRESS_TAG  = "max_progress";

	private final MachineRecipeBinding binding;
	private final Runnable             changeCallback;
	private @Nullable ResourceKey<Recipe<?>> activeRecipe;
	private int                                  progress;
	private int                                  maxProgress;
	private Status                               status = Status.IDLE;

	MachineRecipeProcessor (MachineRecipeBinding binding, Runnable changeCallback) {
		this.binding = Objects.requireNonNull(binding, "binding");
		this.changeCallback = Objects.requireNonNull(changeCallback, "changeCallback");
	}

	/**
	 * Advances this subscription by one server tick.
	 */
	public Status tick (ServerLevel level) {
		Objects.requireNonNull(level, "level");
		MachineRecipeInput input = binding.captureInput();
		RecipeHolder<MachineRecipe> firstMatch = null;
		RecipeHolder<MachineRecipe> executable = null;

		if (activeRecipe != null) {
			var active = level.recipeAccess().getRecipeFor(binding.definition().type(), input, level, activeRecipe);
			if (active.isPresent()) {
				firstMatch = active.orElseThrow();
				if (binding.canExecute(firstMatch.value(), level)) executable = firstMatch;
			}
		}

		if (executable == null) {
			for (RecipeHolder<MachineRecipe> holder : level.recipeAccess().recipeMap().byType(binding.definition().type())) {
				if (holder.id().equals(activeRecipe) || !holder.value().matches(input, level)) continue;
				if (firstMatch == null) firstMatch = holder;
				if (binding.canExecute(holder.value(), level)) {
					executable = holder;
					break;
				}
			}
		}

		RecipeHolder<MachineRecipe> selected = executable != null ? executable : firstMatch;
		if (selected == null) {
			setState(null, 0, 0, Status.IDLE);
			return status;
		}

		boolean changedRecipe = !selected.id().equals(activeRecipe);
		int selectedDuration = selected.value().duration();
		int selectedProgress = changedRecipe ? 0 : Math.min(progress, selectedDuration);
		if (executable == null) {
			setState(selected.id(), selectedProgress, selectedDuration, Status.BLOCKED);
			return status;
		}

		selectedProgress++;
		if (selectedProgress < selectedDuration) {
			setState(selected.id(), selectedProgress, selectedDuration, Status.RUNNING);
			return status;
		}

		if (binding.tryExecute(selected.value(), level)) {
			setState(null, 0, 0, Status.COMPLETED);
			return status;
		}

		setState(selected.id(), Math.max(0, selectedDuration - 1), selectedDuration, Status.BLOCKED);
		return status;
	}

	public void reset () {
		setState(null, 0, 0, Status.IDLE);
	}

	public int progress () {
		return progress;
	}

	public int maxProgress () {
		return maxProgress;
	}

	public Status status () {
		return status;
	}

	public boolean isRunning () {
		return status == Status.RUNNING;
	}

	public @Nullable ResourceKey<Recipe<?>> activeRecipe () {
		return activeRecipe;
	}

	public void save (ValueOutput output) {
		Objects.requireNonNull(output, "output");
		output.putInt(PROGRESS_TAG, progress);
		output.putInt(MAX_PROGRESS_TAG, maxProgress);
		if (activeRecipe == null) output.discard(ACTIVE_RECIPE_TAG);
		else output.putString(ACTIVE_RECIPE_TAG, activeRecipe.identifier().toString());
	}

	public void load (ValueInput input) {
		Objects.requireNonNull(input, "input");
		int loadedMax = Math.max(0, input.getIntOr(MAX_PROGRESS_TAG, 0));
		int loadedProgress = Math.clamp(input.getIntOr(PROGRESS_TAG, 0), 0, loadedMax);
		ResourceKey<Recipe<?>> loadedRecipe = input.getString(ACTIVE_RECIPE_TAG)
				.map(Identifier::tryParse)
				.filter(Objects::nonNull)
				.map(id -> ResourceKey.<Recipe<?>>create(Registries.RECIPE, id))
				.orElse(null);
		this.activeRecipe = loadedRecipe;
		this.progress = loadedRecipe == null ? 0 : loadedProgress;
		this.maxProgress = loadedRecipe == null ? 0 : loadedMax;
		this.status = Status.IDLE;
	}

	private void setState (@Nullable ResourceKey<Recipe<?>> recipe, int progress, int maxProgress, Status status) {
		if (progress < 0 || maxProgress < 0 || progress > maxProgress) {
			throw new IllegalArgumentException("Invalid machine recipe progress " + progress + "/" + maxProgress);
		}
		Objects.requireNonNull(status, "status");
		if (Objects.equals(activeRecipe, recipe) && this.progress == progress && this.maxProgress == maxProgress && this.status == status) return;
		this.activeRecipe = recipe;
		this.progress = progress;
		this.maxProgress = maxProgress;
		this.status = status;
		changeCallback.run();
	}

	public enum Status {
		IDLE,
		RUNNING,
		BLOCKED,
		COMPLETED
	}
}
