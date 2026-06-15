package general.api.formula.core;

import general.api.formula.core.FormulaPart.AtomPart;
import general.api.formula.core.FormulaPart.CompoundPart;
import general.api.formula.core.FormulaPart.MaterialPart;
import net.minecraft.core.Holder;

import java.util.*;

/**
 * A gameplay material made from atoms, compounds, and/or other materials (e.g. Iron, Steel, Fluxium).
 * Materials are registry entries usable to describe items, blocks, and fluids.
 *
 * <p>The rendered {@link #formula} is either set explicitly via the builder, or derived by composing
 * the {@link #components}. Components are {@link FormulaPart parts} whose {@code count} is an opaque
 * gameplay amount/ratio (e.g. {@code Iron×98}, {@code Carbon×2}).
 *
 * @param name         display name
 * @param category     classifying category
 * @param formula      rendered formula (explicit or derived from {@link #components})
 * @param components   the gameplay makeup
 * @param traits       optional traits
 * @param color        ARGB tint used for tooltip/material styling
 * @param properties   optional physical properties
 * @param fictional    whether this material is a gameplay invention
 * @param defaultForms default item forms (design-only; see {@link ItemForm})
 */
public record Material(String name, Holder<Category> category, Formula formula, List<FormulaPart> components, Set<Holder<Trait>> traits, int color, MaterialProperties properties, boolean fictional, Set<ItemForm> defaultForms) {

	public static final int DEFAULT_COLOR = 0xFFFFFFFF;

	public Material {
		components = List.copyOf(components);
		traits = Set.copyOf(traits);
		defaultForms = Set.copyOf(defaultForms);
	}

	/**
	 * Approximate material mass (g/mol) from the formula, if all contributing atoms carry masses.
	 */
	public OptionalDouble mass () {
		return formula.mass();
	}

	public static Builder builder (String name) {
		return new Builder(name);
	}

	public static final class Builder {
		private final String             name;
		private       Holder<Category>   category;
		private       Formula            formula;
		private final List<FormulaPart>  components   = new ArrayList<>();
		private final Set<Holder<Trait>> traits       = new LinkedHashSet<>();
		private       int                color        = DEFAULT_COLOR;
		private       MaterialProperties properties   = MaterialProperties.EMPTY;
		private       boolean            fictional    = false;
		private final Set<ItemForm>      defaultForms = EnumSet.noneOf(ItemForm.class);

		private Builder (String name) {
			this.name = name;
		}

		public Builder category (Holder<Category> category) {
			this.category = category;
			return this;
		}

		/**
		 * Sets an explicit formula, overriding derivation from components.
		 */
		public Builder formula (Formula formula) {
			this.formula = formula;
			return this;
		}

		public Builder formula (Formula.Builder formula) {
			return formula(formula.build());
		}

		public Builder atom (Holder<Atom> atom, int amount) {
			components.add(new AtomPart(atom, amount));
			return this;
		}

		public Builder compound (Holder<Compound> compound, int amount) {
			components.add(new CompoundPart(compound, amount));
			return this;
		}

		public Builder material (Holder<Material> material, int amount) {
			components.add(new MaterialPart(material, amount));
			return this;
		}

		/**
		 * Adds an arbitrary component part.
		 */
		public Builder component (FormulaPart part) {
			components.add(part);
			return this;
		}

		public Builder trait (Holder<Trait> trait) {
			this.traits.add(trait);
			return this;
		}

		public Builder color (int color) {
			this.color = color;
			return this;
		}

		public Builder properties (MaterialProperties properties) {
			this.properties = properties;
			return this;
		}

		public Builder properties (MaterialProperties.Builder properties) {
			return properties(properties.build());
		}

		public Builder fictional (boolean fictional) {
			this.fictional = fictional;
			return this;
		}

		public Builder form (ItemForm... forms) {
			this.defaultForms.addAll(List.of(forms));
			return this;
		}

		public Material build () {
			Formula resolved = formula != null ? formula : new Formula(components);
			return new Material(name, category, resolved, components, traits, color, properties, fictional, defaultForms);
		}
	}
}
