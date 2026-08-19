package general.api.transfer;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Factory and builder methods for immutable and dynamically gated policies.
 */
public final class ResourceAccess {

	private static final Predicate<Resource>            ACCEPT_ALL = resource -> true;
	private static final ResourceAccessPolicy<Resource> NONE       = new ConstantPolicy(false);
	private static final ResourceAccessPolicy<Resource> ALL        = new ConstantPolicy(true);

	private ResourceAccess () {
	}

	@SuppressWarnings("unchecked")
	public static <R extends Resource> ResourceAccessPolicy<R> none () {
		return (ResourceAccessPolicy<R>) NONE;
	}

	@SuppressWarnings("unchecked")
	public static <R extends Resource> ResourceAccessPolicy<R> all () {
		return (ResourceAccessPolicy<R>) ALL;
	}

	public static <R extends Resource> Builder<R> builder (ResourceInventoryDefinition<R> definition) {
		return new Builder<>(Objects.requireNonNull(definition, "definition"));
	}

	/**
	 * Builds index-based access when no named definition is available.
	 */
	public static <R extends Resource> Builder<R> builder (int handlerSize) {
		if (handlerSize < 0) throw new IllegalArgumentException("Handler size must be non-negative: " + handlerSize);
		return new Builder<>(handlerSize);
	}

	/**
	 * Applies live global gates while keeping the base policy's visibility fixed.
	 * This directly supports the existing machine-wide auto-import/export toggles.
	 */
	public static <R extends Resource> ResourceAccessPolicy<R> gated (ResourceAccessPolicy<R> policy, BooleanSupplier insertionEnabled, BooleanSupplier extractionEnabled) {
		Objects.requireNonNull(policy, "policy");
		Objects.requireNonNull(insertionEnabled, "insertionEnabled");
		Objects.requireNonNull(extractionEnabled, "extractionEnabled");
		return new ResourceAccessPolicy<>() {
			@Override
			public boolean canAccess (int index) {
				return policy.canAccess(index);
			}

			@Override
			public boolean canInsert (int index, R resource) {
				return insertionEnabled.getAsBoolean() && policy.canInsert(index, resource);
			}

			@Override
			public boolean canExtract (int index, R resource) {
				return extractionEnabled.getAsBoolean() && policy.canExtract(index, resource);
			}
		};
	}

	/**
	 * Selects insert or extract rules from a live per-side mode. The exposed slots
	 * are the stable union of both policies, so cached wrappers remain valid when
	 * a side changes mode.
	 */
	public static <R extends Resource> ResourceAccessPolicy<R> switching (ResourceAccessPolicy<R> insertionPolicy, ResourceAccessPolicy<R> extractionPolicy, Supplier<ResourceIoMode> mode) {
		Objects.requireNonNull(insertionPolicy, "insertionPolicy");
		Objects.requireNonNull(extractionPolicy, "extractionPolicy");
		Objects.requireNonNull(mode, "mode");
		return new ResourceAccessPolicy<>() {
			@Override
			public boolean canAccess (int index) {
				return insertionPolicy.canAccess(index) || extractionPolicy.canAccess(index);
			}

			@Override
			public boolean canInsert (int index, R resource) {
				return currentMode(mode).allowsInsertion() && insertionPolicy.canInsert(index, resource);
			}

			@Override
			public boolean canExtract (int index, R resource) {
				return currentMode(mode).allowsExtraction() && extractionPolicy.canExtract(index, resource);
			}
		};
	}

	private static ResourceIoMode currentMode (Supplier<ResourceIoMode> mode) {
		return Objects.requireNonNull(mode.get(), "Resource I/O mode supplier returned null");
	}

	private record ConstantPolicy(boolean allow) implements ResourceAccessPolicy<Resource> {
		@Override
		public boolean canAccess (int index) {
			return allow;
		}

		@Override
		public boolean canInsert (int index, Resource resource) {
			return allow;
		}

		@Override
		public boolean canExtract (int index, Resource resource) {
			return allow;
		}
	}

	/**
	 * Definition-aware policy builder. Repeating the same permission for a slot is
	 * rejected, while separately adding insert and extract produces bidirectional
	 * access intentionally.
	 */
	public static final class Builder<R extends Resource> {

		private final ResourceInventoryDefinition<R> definition;
		private final boolean[]                      insertion;
		private final boolean[]                      extraction;
		private final List<Predicate<? super R>>     insertionFilters;
		private final List<Predicate<? super R>>     extractionFilters;

		private Builder (ResourceInventoryDefinition<R> definition) {
			this.definition = definition;
			this.insertion = new boolean[definition.size()];
			this.extraction = new boolean[definition.size()];
			this.insertionFilters = defaultFilters(definition.size());
			this.extractionFilters = defaultFilters(definition.size());
		}

		private Builder (int size) {
			this.definition = null;
			this.insertion = new boolean[size];
			this.extraction = new boolean[size];
			this.insertionFilters = defaultFilters(size);
			this.extractionFilters = defaultFilters(size);
		}

		public Builder<R> insert (String... names) {
			for (String name : requireNames(names)) insert(index(name), acceptAll());
			return this;
		}

		public Builder<R> extract (String... names) {
			for (String name : requireNames(names)) extract(index(name), acceptAll());
			return this;
		}

		public Builder<R> both (String... names) {
			for (String name : requireNames(names)) {
				int index = index(name);
				insert(index, acceptAll());
				extract(index, acceptAll());
			}
			return this;
		}

		public Builder<R> insert (int... indices) {
			for (int index : requireIndices(indices)) insert(index, acceptAll());
			return this;
		}

		public Builder<R> extract (int... indices) {
			for (int index : requireIndices(indices)) extract(index, acceptAll());
			return this;
		}

		public Builder<R> both (int... indices) {
			for (int index : requireIndices(indices)) {
				insert(index, acceptAll());
				extract(index, acceptAll());
			}
			return this;
		}

		public Builder<R> insert (String name, Predicate<? super R> filter) {
			return insert(index(name), filter);
		}

		public Builder<R> extract (String name, Predicate<? super R> filter) {
			return extract(index(name), filter);
		}

		public Builder<R> insert (int index, Predicate<? super R> filter) {
			checkIndex(index);
			if (insertion[index]) throw duplicate("insertion", index);
			insertion[index] = true;
			insertionFilters.set(index, Objects.requireNonNull(filter, "filter"));
			return this;
		}

		public Builder<R> extract (int index, Predicate<? super R> filter) {
			checkIndex(index);
			if (extraction[index]) throw duplicate("extraction", index);
			extraction[index] = true;
			extractionFilters.set(index, Objects.requireNonNull(filter, "filter"));
			return this;
		}

		public ResourceAccessPolicy<R> build () {
			List<Predicate<? super R>> insertRules = List.copyOf(insertionFilters);
			List<Predicate<? super R>> extractRules = List.copyOf(extractionFilters);
			return new BuiltPolicy<R>(insertion.clone(), extraction.clone(), insertRules, extractRules);
		}

		private int index (String name) {
			if (definition == null) throw new IllegalStateException("Named resource access requires a resource inventory definition");
			return definition.index(name);
		}

		private void checkIndex (int index) {
			if (index < 0 || index >= insertion.length) {
				throw new IndexOutOfBoundsException("Access policy index " + index + " is outside [0, " + insertion.length + ")");
			}
		}

		private IllegalArgumentException duplicate (String operation, int index) {
			String slot = definition == null ? "#" + index : definition.get(index).name().orElse("#" + index);
			return new IllegalArgumentException("Duplicate " + operation + " rule for resource slot '" + slot + "'");
		}

		private static String[] requireNames (String[] names) {
			return Objects.requireNonNull(names, "names");
		}

		private static int[] requireIndices (int[] indices) {
			return Objects.requireNonNull(indices, "indices");
		}

		private static <R extends Resource> List<Predicate<? super R>> defaultFilters (int size) {
			var filters = new ArrayList<Predicate<? super R>>(size);
			for (int index = 0; index < size; index++) filters.add(acceptAll());
			return filters;
		}

		@SuppressWarnings("unchecked")
		private static <R extends Resource> Predicate<R> acceptAll () {
			return (Predicate<R>) ACCEPT_ALL;
		}
	}

	private record BuiltPolicy<R extends Resource>(boolean[] insertion, boolean[] extraction, List<Predicate<? super R>> insertionFilters, List<Predicate<? super R>> extractionFilters) implements ResourceAccessPolicy<R> {

		@Override
		public boolean canAccess (int index) {
			return insertion[index] || extraction[index];
		}

		@Override
		public boolean canInsert (int index, R resource) {
			return insertion[index] && insertionFilters.get(index).test(resource);
		}

		@Override
		public boolean canExtract (int index, R resource) {
			return extraction[index] && extractionFilters.get(index).test(resource);
		}
	}
}
