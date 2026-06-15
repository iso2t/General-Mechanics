package general.api.formula.core;

import java.util.Optional;

/**
 * Describes a gameplay behaviour or identity of an {@link Atom}, {@link Compound}, or
 * {@link Material} (e.g. conductive, reactive, energetic). Traits are registry entries referenced
 * elsewhere by {@code Holder<Trait>}; this record carries the display data.
 *
 * @param name        human-readable display name, e.g. {@code "Conductive"}
 * @param color       ARGB tint used for tooltip styling
 * @param description optional one-line description
 */
public record Trait(String name, int color, Optional<String> description) {

	public static final int DEFAULT_COLOR = 0xFF7FB0FF;

	public static Builder builder (String name) {
		return new Builder(name);
	}

	public static final class Builder {
		private final String           name;
		private       int              color       = DEFAULT_COLOR;
		private       Optional<String> description = Optional.empty();

		private Builder (String name) {
			this.name = name;
		}

		public Builder color (int color) {
			this.color = color;
			return this;
		}

		public Builder description (String description) {
			this.description = Optional.ofNullable(description);
			return this;
		}

		public Trait build () {
			return new Trait(name, color, description);
		}
	}
}
