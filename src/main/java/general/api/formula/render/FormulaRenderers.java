package general.api.formula.render;

import general.api.formula.core.Atom;
import general.api.formula.core.Formula;
import general.api.formula.core.FormulaPart;
import general.api.formula.core.FormulaPart.*;

/**
 * Built-in {@link FormulaRenderer renderers} and the digit conversion helpers they share.
 */
public final class FormulaRenderers {

	private FormulaRenderers () {
	}

	/**
	 * ASCII rendering, e.g. {@code Fe2O3}. Counts of {@code 1} are omitted.
	 */
	public static final FormulaRenderer PLAIN = formula -> render(formula, false);

	/**
	 * Unicode-subscript rendering
	 */
	public static final FormulaRenderer UNICODE = formula -> render(formula, true);

	/**
	 * Verbose structural rendering for debugging.
	 */
	public static final FormulaRenderer DEBUG = FormulaRenderers::renderDebug;

	/**
	 * Styled Minecraft component rendering
	 */
	public static final FormulaRenderer MINECRAFT_COMPONENT = new FormulaRenderer() {
		@Override
		public String renderString (Formula formula) {
			return UNICODE.renderString(formula);
		}
	};

	public static FormulaRenderer byMode (RenderMode mode) {
		return switch (mode) {
			case PLAIN -> PLAIN;
			case UNICODE -> UNICODE;
			case DEBUG -> DEBUG;
		};
	}

	private static String render (Formula formula, boolean subscript) {
		StringBuilder sb = new StringBuilder();
		for (FormulaPart part : formula.parts()) {
			renderPart(sb, part, subscript);
		}
		return sb.toString();
	}

	private static void renderPart (StringBuilder sb, FormulaPart part, boolean subscript) {
		switch (part) {
			case AtomPart a -> {
				sb.append(a.atom().value().symbol());
				appendCount(sb, a.count(), subscript);
			}
			case CompoundPart c -> {
				sb.append(symbolOf(c.compound().value().formula(), subscript));
				appendCount(sb, c.count(), subscript);
			}
			case MaterialPart m -> {
				sb.append(symbolOf(m.material().value().formula(), subscript));
				appendCount(sb, m.count(), subscript);
			}
			case FictionalPart f -> {
				sb.append(f.symbol());
				appendCount(sb, f.count(), subscript);
			}
			case GroupPart g -> {
				sb.append('(').append(render(g.group(), subscript)).append(')');
				appendCount(sb, g.count(), subscript);
			}
		}
	}

	/**
	 * Renders a referenced compound/material's own formula inline.
	 */
	private static String symbolOf (Formula formula, boolean subscript) {
		return render(formula, subscript);
	}

	private static void appendCount (StringBuilder sb, int count, boolean subscript) {
		if (count <= 1) {
			return;
		}
		String digits = Integer.toString(count);
		if (!subscript) {
			sb.append(digits);
			return;
		}
		for (int i = 0; i < digits.length(); i++) {
			sb.append(subscriptDigit(digits.charAt(i)));
		}
	}

	/**
	 * Maps an ASCII digit {@code '0'..'9'} to its Unicode subscript {@code '₀'..'₉'}.
	 */
	public static char subscriptDigit (char ascii) {
		return (ascii >= '0' && ascii <= '9') ? (char) ('₀' + (ascii - '0')) : ascii;
	}

	private static String renderDebug (Formula formula) {
		StringBuilder sb = new StringBuilder("Formula[");
		for (int i = 0; i < formula.parts().size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(debugPart(formula.parts().get(i)));
		}
		return sb.append(']').toString();
	}

	private static String debugPart (FormulaPart part) {
		return switch (part) {
			case AtomPart a -> describeAtom(a.atom().value()) + "x" + a.count();
			case CompoundPart c -> "compound(" + renderDebug(c.compound().value().formula()) + ")x" + c.count();
			case MaterialPart m -> "material(" + renderDebug(m.material().value().formula()) + ")x" + m.count();
			case FictionalPart f -> "fictional(" + f.symbol() + ")x" + f.count();
			case GroupPart g -> "group(" + renderDebug(g.group()) + ")x" + g.count();
		};
	}

	private static String describeAtom (Atom atom) {
		return atom.symbol() + (atom.fictional() ? "*" : "");
	}
}
