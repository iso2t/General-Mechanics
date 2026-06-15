package general.api.formula.core;

import general.api.formula.core.FormulaPart.AtomPart;
import general.api.formula.core.FormulaPart.CompoundPart;
import general.api.formula.core.FormulaPart.GroupPart;
import net.minecraft.core.Holder;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

/**
 * A formula made from atoms and/or nested groups (e.g. water, iron oxide, ammonium sulfate). A
 * compound is a registry entry; its registry id provides the canonical name used in references.
 *
 * <pre>{@code
 * Compound water = Compound.builder("Water")
 *     .atom(hydrogen, 2)
 *     .atom(oxygen, 1)
 *     .category(inorganic)
 *     .build();
 * }</pre>
 *
 * @param name      display name
 * @param formula   the composed formula
 * @param category  optional classifying category
 * @param traits    optional traits
 * @param fictional whether this compound is a gameplay invention
 */
public record Compound(String name, Formula formula, Optional<Holder<Category>> category, Set<Holder<Trait>> traits, boolean fictional) {

	public Compound {
		traits = Set.copyOf(traits);
	}

	public OptionalDouble mass () {
		return formula.mass();
	}

	public static Builder builder (String name) {
		return new Builder(name);
	}

	public static Formula.Builder group () {
		return Formula.builder();
	}

	public static final class Builder {
		private final String                     name;
		private final Formula.Builder            formula   = Formula.builder();
		private       Optional<Holder<Category>> category  = Optional.empty();
		private final Set<Holder<Trait>>         traits    = new LinkedHashSet<>();
		private       boolean                    fictional = false;

		private Builder (String name) {
			this.name = name;
		}

		public Builder atom (Holder<Atom> atom, int count) {
			formula.part(new AtomPart(atom, count));
			return this;
		}

		public Builder atom (Holder<Atom> atom) {
			return atom(atom, 1);
		}

		public Builder compound (Holder<Compound> compound, int count) {
			formula.part(new CompoundPart(compound, count));
			return this;
		}

		public Builder group (Formula.Builder group, int count) {
			formula.part(new GroupPart(group.build(), count));
			return this;
		}

		public Builder group (Formula group, int count) {
			formula.part(new GroupPart(group, count));
			return this;
		}

		public Builder category (Holder<Category> category) {
			this.category = Optional.ofNullable(category);
			return this;
		}

		public Builder trait (Holder<Trait> trait) {
			this.traits.add(trait);
			return this;
		}

		public Builder fictional (boolean fictional) {
			this.fictional = fictional;
			return this;
		}

		public Compound build () {
			return new Compound(name, formula.build(), category, traits, fictional);
		}
	}
}
