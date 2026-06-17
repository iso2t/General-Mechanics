package general.api.formula.builtin;

import general.api.formula.GenFormula;
import general.api.formula.core.Atom;
import net.minecraft.resources.ResourceKey;

/**
 * Built-in {@link Atom} keys (namespace {@code genapi}): a practical subset of real elements plus a
 * couple of fictional gameplay "elements". Values are populated by {@link GenFormulaBootstrap}.
 */
public final class FormulaAtoms {

	private FormulaAtoms () {}

	public static final ResourceKey<Atom> HYDROGEN   = GenFormula.atomKey("hydrogen");
	public static final ResourceKey<Atom> HELIUM     = GenFormula.atomKey("helium");
	public static final ResourceKey<Atom> CARBON     = GenFormula.atomKey("carbon");
	public static final ResourceKey<Atom> NITROGEN   = GenFormula.atomKey("nitrogen");
	public static final ResourceKey<Atom> OXYGEN     = GenFormula.atomKey("oxygen");
	public static final ResourceKey<Atom> FLUORINE   = GenFormula.atomKey("fluorine");
	public static final ResourceKey<Atom> SODIUM     = GenFormula.atomKey("sodium");
	public static final ResourceKey<Atom> ALUMINIUM  = GenFormula.atomKey("aluminium");
	public static final ResourceKey<Atom> SILICON    = GenFormula.atomKey("silicon");
	public static final ResourceKey<Atom> PHOSPHORUS = GenFormula.atomKey("phosphorus");
	public static final ResourceKey<Atom> SULFUR     = GenFormula.atomKey("sulfur");
	public static final ResourceKey<Atom> CHLORINE   = GenFormula.atomKey("chlorine");
	public static final ResourceKey<Atom> TITANIUM   = GenFormula.atomKey("titanium");
	public static final ResourceKey<Atom> CHROMIUM   = GenFormula.atomKey("chromium");
	public static final ResourceKey<Atom> IRON       = GenFormula.atomKey("iron");
	public static final ResourceKey<Atom> COPPER     = GenFormula.atomKey("copper");
	public static final ResourceKey<Atom> ZINC       = GenFormula.atomKey("zinc");
	public static final ResourceKey<Atom> GOLD       = GenFormula.atomKey("gold");
	public static final ResourceKey<Atom> NICKEL     = GenFormula.atomKey("nickel");

	public static final ResourceKey<Atom> REDSTONE   = GenFormula.atomKey("redstone");
}
