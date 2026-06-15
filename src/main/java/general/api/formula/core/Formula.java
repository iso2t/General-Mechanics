package general.api.formula.core;

import general.api.formula.core.FormulaPart.*;
import general.api.formula.render.FormulaRenderer;
import general.api.formula.render.FormulaRenderers;
import general.api.formula.render.RenderMode;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * An ordered, immutable sequence of {@link FormulaPart parts} that together describe a chemical-style
 * formula (e.g. {@code Fe₂O₃}, {@code (NH₄)₂SO₄}, {@code FeRx₂Fx}).
 *
 * <p>Build with {@link #builder()}:
 * <pre>{@code
 * Formula f = Formula.builder()
 *     .atom(FormulaAtoms.IRON, 2)
 *     .atom(FormulaAtoms.OXYGEN, 3)
 *     .build();
 * f.render(RenderMode.UNICODE); // "Fe₂O₃"
 * f.render(RenderMode.PLAIN);   // "Fe2O3"
 * }</pre>
 */
public record Formula(List<FormulaPart> parts) {

	public Formula {
		parts = List.copyOf(parts);
	}

	public static Builder builder () {
		return new Builder();
	}

	public String render (RenderMode mode) {
		return FormulaRenderers.byMode(mode).renderString(this);
	}

	public String render (FormulaRenderer renderer) {
		return renderer.renderString(this);
	}

	public String plain () {
		return render(RenderMode.PLAIN);
	}

	public String unicode () {
		return render(RenderMode.UNICODE);
	}

	public Component renderComponent () {
		return FormulaRenderers.MINECRAFT_COMPONENT.renderComponent(this);
	}

	public boolean isFictional () {
		for (FormulaPart part : parts) {
			if (isFictional(part)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isFictional (FormulaPart part) {
		return switch (part) {
			case AtomPart a -> a.atom().value().fictional();
			case CompoundPart c -> c.compound().value().fictional();
			case MaterialPart m -> m.material().value().fictional();
			case FictionalPart ignored -> true;
			case GroupPart g -> g.group().isFictional();
		};
	}

	/**
	 * Approximate molecular/material mass (g/mol) summed from atom masses × counts. Returns
	 * {@link OptionalDouble#empty()} if any contributing atom lacks a mass or the formula contains a
	 * fictional part (mass is then indeterminate).
	 */
	public OptionalDouble mass () {
		double total = 0;
		for (FormulaPart part : parts) {
			OptionalDouble partMass = massOf(part);
			if (partMass.isEmpty()) {
				return OptionalDouble.empty();
			}
			total += partMass.getAsDouble();
		}
		return OptionalDouble.of(total);
	}

	private static OptionalDouble massOf (FormulaPart part) {
		return switch (part) {
			case AtomPart a -> scale(a.atom().value().mass(), a.count());
			case CompoundPart c -> scale(c.compound().value().formula().mass(), c.count());
			case MaterialPart m -> scale(m.material().value().formula().mass(), m.count());
			case FictionalPart ignored -> OptionalDouble.empty();
			case GroupPart g -> scale(g.group().mass(), g.count());
		};
	}

	private static OptionalDouble scale (OptionalDouble value, int count) {
		return value.isPresent() ? OptionalDouble.of(value.getAsDouble() * count) : OptionalDouble.empty();
	}

	public static final class Builder {
		private final List<FormulaPart> parts = new ArrayList<>();

		public Builder part (FormulaPart part) {
			parts.add(part);
			return this;
		}

		public Builder atom (Holder<Atom> atom, int count) {
			return part(new AtomPart(atom, requirePositive(count)));
		}

		public Builder atom (Holder<Atom> atom) {
			return atom(atom, 1);
		}

		public Builder compound (Holder<Compound> compound, int count) {
			return part(new CompoundPart(compound, requirePositive(count)));
		}

		public Builder compound (Holder<Compound> compound) {
			return compound(compound, 1);
		}

		public Builder material (Holder<Material> material, int count) {
			return part(new MaterialPart(material, requirePositive(count)));
		}

		public Builder material (Holder<Material> material) {
			return material(material, 1);
		}

		public Builder fictionalPart (String symbol, int count) {
			return part(new FictionalPart(symbol, requirePositive(count)));
		}

		public Builder fictionalPart (String symbol) {
			return fictionalPart(symbol, 1);
		}

		public Builder group (Formula group, int count) {
			return part(new GroupPart(group, requirePositive(count)));
		}

		public Builder group (Builder group, int count) {
			return group(group.build(), count);
		}

		public Formula build () {
			return new Formula(parts);
		}

		private static int requirePositive (int count) {
			if (count < 1) {
				throw new IllegalArgumentException("Formula part count must be >= 1, was " + count);
			}
			return count;
		}
	}
}
