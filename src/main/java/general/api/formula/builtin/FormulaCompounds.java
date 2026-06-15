package general.api.formula.builtin;

import general.api.formula.GenFormula;
import general.api.formula.core.Compound;
import net.minecraft.resources.ResourceKey;

/**
 * Built-in {@link Compound} keys (namespace {@code genapi}). Values are populated by
 * {@link GenFormulaBootstrap}.
 */
public final class FormulaCompounds {

	private FormulaCompounds () {}

	public static final ResourceKey<Compound> WATER            = GenFormula.compoundKey("water");
	public static final ResourceKey<Compound> IRON_OXIDE       = GenFormula.compoundKey("iron_oxide");
	public static final ResourceKey<Compound> COPPER_SULFATE   = GenFormula.compoundKey("copper_sulfate");
	public static final ResourceKey<Compound> GLUCOSE          = GenFormula.compoundKey("glucose");
	public static final ResourceKey<Compound> AMMONIUM_SULFATE = GenFormula.compoundKey("ammonium_sulfate");
}
