package general.api.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Reusable server-side recipe subscription and progress state for one machine.
 *
 * <p>The processor prefers its active recipe, falls back to other matching
 * recipes when that recipe's outputs are blocked, and preserves progress while
 * output-blocked or starved of a per-tick work resource. The completion tick
 * combines that work cost with all recipe transfers in one atomic transaction.
 * Persistence is self-contained through {@link #save(ValueOutput)} and
 * {@link #load(ValueInput)}. Transfer simulations are cached while the bound
 * handlers and loaded recipe map remain unchanged; matching itself is always
 * evaluated against the current level and input.</p>
 */
public final class MachineRecipeProcessor {

	private static final String     ACTIVE_RECIPE_TAG = "active_recipe";
	private static final String     PROGRESS_TAG      = "progress";
	private static final String     MAX_PROGRESS_TAG  = "max_progress";
	private static final TickResult IDLE_RESULT       = new TickResult(Status.IDLE, false);
	private static final TickResult RUNNING_RESULT    = new TickResult(Status.RUNNING, false);
	private static final TickResult BLOCKED_RESULT    = new TickResult(Status.BLOCKED, false);
	private static final TickResult STARVED_RESULT    = new TickResult(Status.STARVED, false);
	private static final TickResult CRAFTED_RESULT    = new TickResult(Status.IDLE, true);

	private final     MachineRecipeBinding                 binding;
	private final     List<MachineRecipeSource>            sources;
	private final     MachineWorkRequirement               workRequirement;
	private final     Runnable                             changeCallback;
	private final     Map<ResourceKey<Recipe<?>>, Boolean> transferCache = new HashMap<>();
	private @Nullable ResourceKey<Recipe<?>>               activeRecipe;
	private @Nullable Object                               recipeMapIdentity;
	private           int                                  progress;
	private           int                                  maxProgress;
	private           Status                               status        = Status.IDLE;
	private           boolean                              revisionInitialized;
	private           long                                 itemRevision;
	private           long                                 fluidRevision;

	MachineRecipeProcessor (MachineRecipeBinding binding, Runnable changeCallback) {
		this(binding, MachineWorkRequirement.free(), changeCallback);
	}

	MachineRecipeProcessor (MachineRecipeBinding binding, MachineWorkRequirement workRequirement, Runnable changeCallback) {
		this(binding, binding.definition().createRecipeSources(), workRequirement, changeCallback);
	}

	MachineRecipeProcessor (MachineRecipeBinding binding, List<MachineRecipeSource> sources, MachineWorkRequirement workRequirement, Runnable changeCallback) {
		this.binding = Objects.requireNonNull(binding, "binding");
		Objects.requireNonNull(sources, "sources");
		if (sources.isEmpty()) throw new IllegalArgumentException("At least one machine recipe source is required");
		var checkedSources = new ArrayList<MachineRecipeSource>(sources.size());
		for (MachineRecipeSource source : sources) checkedSources.add(Objects.requireNonNull(source, "recipeSource"));
		this.sources = List.copyOf(checkedSources);
		this.workRequirement = Objects.requireNonNull(workRequirement, "workRequirement");
		this.changeCallback = Objects.requireNonNull(changeCallback, "changeCallback");
	}

	/**
	 * Advances this subscription by one server tick. The returned completion flag
	 * is an event for this call only; {@link #status()} remains durable machine state.
	 */
	public TickResult tick (ServerLevel level) {
		Objects.requireNonNull(level, "level");
		refreshTransferCache(level);
		MachineRecipeInput input = binding.captureInput();
		List<RecipeHolder<MachineRecipe>> candidates = findCandidates(input, level);
		RecipeHolder<MachineRecipe> firstMatch = null;
		RecipeHolder<MachineRecipe> executable = null;

		if (activeRecipe != null) {
			for (RecipeHolder<MachineRecipe> holder : candidates) {
				if (!holder.id().equals(activeRecipe)) continue;
				firstMatch = holder;
				if (canTransfer(holder)) executable = holder;
				break;
			}
		}

		if (executable == null) {
			for (RecipeHolder<MachineRecipe> holder : candidates) {
				if (holder.id().equals(activeRecipe)) continue;
				if (firstMatch == null) firstMatch = holder;
				if (canTransfer(holder)) {
					executable = holder;
					break;
				}
			}
		}

		RecipeHolder<MachineRecipe> selected = executable != null ? executable : firstMatch;
		if (selected == null) {
			setState(null, 0, 0, Status.IDLE);
			return result(status);
		}

		boolean changedRecipe = !selected.id().equals(activeRecipe);
		int selectedDuration = workRequirement.duration(selected.value());
		if (selectedDuration <= 0) throw new IllegalStateException("Machine work requirement returned a non-positive duration: " + selectedDuration);
		int selectedProgress = changedRecipe ? 0 : Math.min(progress, selectedDuration - 1);
		if (executable == null) {
			setState(selected.id(), selectedProgress, selectedDuration, Status.BLOCKED);
			return result(status);
		}

		int nextProgress = selectedProgress + 1;
		if (nextProgress < selectedDuration) {
			if (!consumeWork(selected.value(), selectedProgress)) {
				setState(selected.id(), selectedProgress, selectedDuration, Status.STARVED);
				return result(status);
			}
			setState(selected.id(), nextProgress, selectedDuration, Status.RUNNING);
			return result(status);
		}

		if (!canConsumeWork(selected.value(), selectedProgress)) {
			setState(selected.id(), selectedProgress, selectedDuration, Status.STARVED);
			return result(status);
		}

		if (tryComplete(selected.value(), input, level, selectedProgress)) {
			setState(null, 0, 0, Status.IDLE);
			return CRAFTED_RESULT;
		}

		setState(selected.id(), Math.max(0, selectedDuration - 1), selectedDuration, Status.BLOCKED);
		return result(status);
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

	public boolean isStarved () {
		return status == Status.STARVED;
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
		ResourceKey<Recipe<?>> loadedRecipe = input.getString(ACTIVE_RECIPE_TAG).map(Identifier::tryParse).filter(Objects::nonNull).map(id -> ResourceKey.<Recipe<?>>create(Registries.RECIPE, id)).orElse(null);
		this.activeRecipe = loadedRecipe;
		this.progress = loadedRecipe == null ? 0 : loadedProgress;
		this.maxProgress = loadedRecipe == null ? 0 : loadedMax;
		this.status = Status.IDLE;
		invalidateTransferCache();
	}

	private List<RecipeHolder<MachineRecipe>> findCandidates (MachineRecipeInput input, ServerLevel level) {
		for (MachineRecipeSource source : sources) {
			List<RecipeHolder<MachineRecipe>> found = Objects.requireNonNull(source.findMatching(input, level), "Machine recipe source returned null");
			if (found.isEmpty()) continue;

			var matches = new ArrayList<RecipeHolder<MachineRecipe>>(found.size());
			for (RecipeHolder<MachineRecipe> holder : found) {
				Objects.requireNonNull(holder, "Machine recipe source returned a null holder");
				MachineRecipe recipe = Objects.requireNonNull(holder.value(), "Machine recipe source returned a null recipe");
				if (recipe.definition() != binding.definition()) {
					throw new IllegalArgumentException("Machine recipe source returned a recipe for " + recipe.definition().id() + " to binding " + binding.definition().id());
				}
				if (binding.matches(recipe, input, level)) matches.add(holder);
			}
			if (!matches.isEmpty()) return List.copyOf(matches);
		}
		return List.of();
	}

	private boolean canTransfer (RecipeHolder<MachineRecipe> holder) {
		if (!binding.tracksContentRevisions()) return binding.canTransfer(holder.value());
		return transferCache.computeIfAbsent(holder.id(), id -> binding.canTransfer(holder.value()));
	}

	private void refreshTransferCache (ServerLevel level) {
		if (!binding.tracksContentRevisions()) return;
		long currentItemRevision = binding.itemContentRevision();
		long currentFluidRevision = binding.fluidContentRevision();
		Object currentRecipeMap = level.recipeAccess().recipeMap();
		if (revisionInitialized && itemRevision == currentItemRevision && fluidRevision == currentFluidRevision && recipeMapIdentity == currentRecipeMap) return;
		transferCache.clear();
		itemRevision = currentItemRevision;
		fluidRevision = currentFluidRevision;
		recipeMapIdentity = currentRecipeMap;
		revisionInitialized = true;
	}

	private void invalidateTransferCache () {
		transferCache.clear();
		recipeMapIdentity = null;
		revisionInitialized = false;
	}

	private boolean canConsumeWork (MachineRecipe recipe, int completedTicks) {
		try (Transaction transaction = Transaction.openRoot()) {
			return workRequirement.consume(recipe, completedTicks, transaction);
		}
	}

	private boolean consumeWork (MachineRecipe recipe, int completedTicks) {
		try (Transaction transaction = Transaction.openRoot()) {
			if (!workRequirement.consume(recipe, completedTicks, transaction)) return false;
			transaction.commit();
			return true;
		}
	}

	private boolean tryComplete (MachineRecipe recipe, MachineRecipeInput input, ServerLevel level, int completedTicks) {
		if (!binding.matches(recipe, input, level)) return false;
		try (Transaction transaction = Transaction.openRoot()) {
			if (!binding.transfer(recipe, transaction, level.getRandom())) return false;
			if (!workRequirement.consume(recipe, completedTicks, transaction)) return false;
			transaction.commit();
			return true;
		}
	}

	private static TickResult result (Status status) {
		return switch (status) {
			case IDLE -> IDLE_RESULT;
			case RUNNING -> RUNNING_RESULT;
			case BLOCKED -> BLOCKED_RESULT;
			case STARVED -> STARVED_RESULT;
		};
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
		/**
		 * No matching recipe is active.
		 */
		IDLE,
		/**
		 * Work advanced during the current tick.
		 */
		RUNNING,
		/**
		 * A recipe matches, but its resource outputs cannot currently be transferred.
		 */
		BLOCKED,
		/**
		 * A recipe is executable, but its per-tick work requirement was unavailable.
		 */
		STARVED
	}

	/**
	 * Outcome of one processor tick.
	 *
	 * @param status  processor state after the tick
	 * @param crafted {@code true} only when this tick atomically completed a recipe
	 */
	public record TickResult(Status status, boolean crafted) {

		public TickResult {
			Objects.requireNonNull(status, "status");
			if (crafted && status != Status.IDLE) {
				throw new IllegalArgumentException("A crafted tick must leave the recipe processor idle");
			}
		}
	}
}
