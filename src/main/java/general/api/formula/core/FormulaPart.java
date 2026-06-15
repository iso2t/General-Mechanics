package general.api.formula.core;

import net.minecraft.core.Holder;

import java.util.List;

/**
 * A single segment of a {@link Formula}. Parts are an exhaustive, sealed set so renderers, mass
 * calculation, and codecs can switch over them without a default case.
 *
 * <ul>
 *     <li>{@link AtomPart} — an atom with a count, e.g. {@code O₂}</li>
 *     <li>{@link CompoundPart} — a referenced compound with a count</li>
 *     <li>{@link MaterialPart} — a referenced material with a count</li>
 *     <li>{@link FictionalPart} — a free-form symbolic part, e.g. {@code Fx}</li>
 *     <li>{@link GroupPart} — a parenthesised nested group, e.g. {@code (NH₄)₂}</li>
 * </ul>
 */
public sealed interface FormulaPart {

	/**
	 * The multiplier/subscript applied to this part (always {@code >= 1}).
	 */
	int count ();

	record AtomPart(Holder<Atom> atom, int count) implements FormulaPart {
	}

	record CompoundPart(Holder<Compound> compound, int count) implements FormulaPart {
	}

	record MaterialPart(Holder<Material> material, int count) implements FormulaPart {
	}

	record FictionalPart(String symbol, int count) implements FormulaPart {
	}

	record GroupPart(Formula group, int count) implements FormulaPart {

		public GroupPart {
			List.copyOf(group.parts()); // null-check defensively
		}
	}
}
