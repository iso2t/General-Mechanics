package general.api.formula.core;

import net.minecraft.core.Holder;

import java.util.Optional;

/**
 * Groups {@link Atom atoms}, {@link Compound compounds}, and {@link Material materials} (e.g. metal,
 * oxide, fictional alloy). Categories are registry entries, so a category is referenced elsewhere by
 * its {@code Holder<Category>}; this record carries only the display data.
 *
 * @param name   human-readable display name, e.g. {@code "Oxide"}
 * @param color  ARGB tint used for tooltip styling
 * @param parent optional parent category for hierarchical grouping
 */
public record Category(String name, int color, Optional<Holder<Category>> parent) {

	public static final int DEFAULT_COLOR = 0xFFB0B0B0;

	public static Builder builder (String name) {
		return new Builder(name);
	}

	public static final class Builder {
		private final String                     name;
		private       int                        color  = DEFAULT_COLOR;
		private       Optional<Holder<Category>> parent = Optional.empty();

		private Builder (String name) {
			this.name = name;
		}

		public Builder color (int color) {
			this.color = color;
			return this;
		}

		public Builder parent (Holder<Category> parent) {
			this.parent = Optional.ofNullable(parent);
			return this;
		}

		public Category build () {
			return new Category(name, color, parent);
		}
	}
}
