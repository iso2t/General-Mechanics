package general.api.formula.core;

import net.minecraft.core.Holder;

import java.util.LinkedHashSet;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;

/**
 * A base elemental building block of a formula — real (e.g. {@code H}, {@code Fe}) or fictional
 * (e.g. {@code Fx}). Atoms are registry entries; the registry id supplies the canonical name, while
 * the {@link #symbol} is what renders inside a formula.
 *
 * @param symbol       short formula symbol, e.g. {@code "Fe"}
 * @param name         display name, e.g. {@code "Iron"}
 * @param category     classifying category
 * @param atomicNumber optional atomic number
 * @param mass         optional atomic mass (g/mol); enables mass calculation
 * @param color        ARGB tint used for tooltip/formula styling
 * @param fictional    whether this atom is a gameplay invention rather than a real element
 * @param traits       optional traits
 */
public record Atom(String symbol, String name, Holder<Category> category, OptionalInt atomicNumber, OptionalDouble mass, int color, boolean fictional, Set<Holder<Trait>> traits) {

	public static final int DEFAULT_COLOR = 0xFFFFFFFF;

	public Atom {
		traits = Set.copyOf(traits);
	}

	public static Builder builder (String symbol) {
		return new Builder(symbol);
	}

	public static final class Builder {
		private final String             symbol;
		private       String             name;
		private       Holder<Category>   category;
		private       OptionalInt        atomicNumber = OptionalInt.empty();
		private       OptionalDouble     mass         = OptionalDouble.empty();
		private       int                color        = DEFAULT_COLOR;
		private       boolean            fictional    = false;
		private final Set<Holder<Trait>> traits       = new LinkedHashSet<>();

		private Builder (String symbol) {
			this.symbol = symbol;
			this.name = symbol;
		}

		public Builder name (String name) {
			this.name = name;
			return this;
		}

		public Builder category (Holder<Category> category) {
			this.category = category;
			return this;
		}

		public Builder atomicNumber (int atomicNumber) {
			this.atomicNumber = OptionalInt.of(atomicNumber);
			return this;
		}

		public Builder atomicMass (double mass) {
			this.mass = OptionalDouble.of(mass);
			return this;
		}

		public Builder color (int color) {
			this.color = color;
			return this;
		}

		public Builder fictional (boolean fictional) {
			this.fictional = fictional;
			return this;
		}

		public Builder trait (Holder<Trait> trait) {
			this.traits.add(trait);
			return this;
		}

		public Atom build () {
			return new Atom(symbol, name, category, atomicNumber, mass, color, fictional, traits);
		}
	}
}
