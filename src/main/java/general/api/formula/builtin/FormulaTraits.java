package general.api.formula.builtin;

import general.api.formula.GenFormula;
import general.api.formula.core.Trait;
import net.minecraft.resources.ResourceKey;

/**
 * Built-in {@link Trait} keys (namespace {@code genapi}). Values are populated by
 * {@link GenFormulaBootstrap}. Mods may register their own traits under their own namespace.
 */
public final class FormulaTraits {

	private FormulaTraits () {}

	public static final ResourceKey<Trait> CONDUCTIVE  = GenFormula.traitKey("conductive");
	public static final ResourceKey<Trait> MAGNETIC    = GenFormula.traitKey("magnetic");
	public static final ResourceKey<Trait> REACTIVE    = GenFormula.traitKey("reactive");
	public static final ResourceKey<Trait> RADIOACTIVE = GenFormula.traitKey("radioactive");
	public static final ResourceKey<Trait> STABLE      = GenFormula.traitKey("stable");
	public static final ResourceKey<Trait> UNSTABLE    = GenFormula.traitKey("unstable");
	public static final ResourceKey<Trait> ORGANIC     = GenFormula.traitKey("organic");
	public static final ResourceKey<Trait> SYNTHETIC   = GenFormula.traitKey("synthetic");
	public static final ResourceKey<Trait> BRITTLE     = GenFormula.traitKey("brittle");
	public static final ResourceKey<Trait> DENSE       = GenFormula.traitKey("dense");
	public static final ResourceKey<Trait> LIGHTWEIGHT = GenFormula.traitKey("lightweight");
	public static final ResourceKey<Trait> STRUCTURAL  = GenFormula.traitKey("structural");
	public static final ResourceKey<Trait> ENERGETIC   = GenFormula.traitKey("energetic");
	public static final ResourceKey<Trait> FLAMMABLE   = GenFormula.traitKey("flammable");
	public static final ResourceKey<Trait> INSULATING  = GenFormula.traitKey("insulating");
	public static final ResourceKey<Trait> CATALYTIC   = GenFormula.traitKey("catalytic");
	public static final ResourceKey<Trait> TOXIC       = GenFormula.traitKey("toxic");
	public static final ResourceKey<Trait> CORROSIVE   = GenFormula.traitKey("corrosive");
}
