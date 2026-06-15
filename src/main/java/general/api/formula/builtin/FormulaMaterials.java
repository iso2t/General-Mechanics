package general.api.formula.builtin;

import general.api.formula.GenFormula;
import general.api.formula.core.Material;
import net.minecraft.resources.ResourceKey;

/**
 * Built-in {@link Material} keys (namespace {@code genapi}). Values are populated by
 * {@link GenFormulaBootstrap}.
 */
public final class FormulaMaterials {

	private FormulaMaterials () {}

	public static final ResourceKey<Material> IRON     = GenFormula.materialKey("iron");
	public static final ResourceKey<Material> COPPER   = GenFormula.materialKey("copper");
	public static final ResourceKey<Material> STEEL    = GenFormula.materialKey("steel");
	public static final ResourceKey<Material> REDSTONE = GenFormula.materialKey("redstone");
	public static final ResourceKey<Material> SILICON  = GenFormula.materialKey("silicon");
}
