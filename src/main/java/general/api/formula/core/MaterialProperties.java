package general.api.formula.core;

import java.util.OptionalDouble;

/**
 * Optional physical/gameplay properties of a {@link Material}. All values are optional — materials
 * are not required to carry scientific data.
 *
 * @param density      relative density (gameplay units)
 * @param hardness     relative hardness
 * @param conductivity electrical/thermal conductivity (gameplay units)
 * @param reactivity   chemical reactivity (gameplay units)
 */
public record MaterialProperties(OptionalDouble density, OptionalDouble hardness, OptionalDouble conductivity, OptionalDouble reactivity) {

	public static final MaterialProperties EMPTY = new MaterialProperties(OptionalDouble.empty(), OptionalDouble.empty(), OptionalDouble.empty(), OptionalDouble.empty());

	public boolean isEmpty () {
		return density.isEmpty() && hardness.isEmpty() && conductivity.isEmpty() && reactivity.isEmpty();
	}

	public static Builder builder () {
		return new Builder();
	}

	public static final class Builder {
		private OptionalDouble density      = OptionalDouble.empty();
		private OptionalDouble hardness     = OptionalDouble.empty();
		private OptionalDouble conductivity = OptionalDouble.empty();
		private OptionalDouble reactivity   = OptionalDouble.empty();

		public Builder density (double v) {
			this.density = OptionalDouble.of(v);
			return this;
		}

		public Builder hardness (double v) {
			this.hardness = OptionalDouble.of(v);
			return this;
		}

		public Builder conductivity (double v) {
			this.conductivity = OptionalDouble.of(v);
			return this;
		}

		public Builder reactivity (double v) {
			this.reactivity = OptionalDouble.of(v);
			return this;
		}

		public MaterialProperties build () {
			return new MaterialProperties(density, hardness, conductivity, reactivity);
		}
	}
}
