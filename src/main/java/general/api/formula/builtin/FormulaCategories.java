package general.api.formula.builtin;

import general.api.formula.GenFormula;
import general.api.formula.core.Category;
import net.minecraft.resources.ResourceKey;

/**
 * Built-in {@link Category} keys (namespace {@code genapi}). Values are populated by
 * {@link GenFormulaBootstrap}. Mods may register their own categories under their own namespace.
 */
public final class FormulaCategories {

	private FormulaCategories () {}

	public static final ResourceKey<Category> METAL       = GenFormula.categoryKey("metal");
	public static final ResourceKey<Category> NONMETAL    = GenFormula.categoryKey("nonmetal");
	public static final ResourceKey<Category> METALLOID   = GenFormula.categoryKey("metalloid");
	public static final ResourceKey<Category> NOBLE_GAS   = GenFormula.categoryKey("noble_gas");
	public static final ResourceKey<Category> ALLOY       = GenFormula.categoryKey("alloy");
	public static final ResourceKey<Category> POLYMER     = GenFormula.categoryKey("polymer");
	public static final ResourceKey<Category> ORGANIC     = GenFormula.categoryKey("organic");
	public static final ResourceKey<Category> INORGANIC   = GenFormula.categoryKey("inorganic");
	public static final ResourceKey<Category> SYNTHETIC   = GenFormula.categoryKey("synthetic");
	public static final ResourceKey<Category> FICTIONAL   = GenFormula.categoryKey("fictional");
	public static final ResourceKey<Category> FICTIONAL_ALLOY = GenFormula.categoryKey("fictional_alloy");
	public static final ResourceKey<Category> FUEL        = GenFormula.categoryKey("fuel");
	public static final ResourceKey<Category> OXIDE       = GenFormula.categoryKey("oxide");
	public static final ResourceKey<Category> SALT        = GenFormula.categoryKey("salt");
	public static final ResourceKey<Category> ACID        = GenFormula.categoryKey("acid");
	public static final ResourceKey<Category> BASE        = GenFormula.categoryKey("base");
	public static final ResourceKey<Category> CIRCUIT     = GenFormula.categoryKey("circuit");
	public static final ResourceKey<Category> MECHANICAL  = GenFormula.categoryKey("mechanical");
	public static final ResourceKey<Category> ELECTRICAL  = GenFormula.categoryKey("electrical");
	public static final ResourceKey<Category> RADIOACTIVE = GenFormula.categoryKey("radioactive");
	public static final ResourceKey<Category> CRYSTAL     = GenFormula.categoryKey("crystal");
	public static final ResourceKey<Category> DUST        = GenFormula.categoryKey("dust");
	public static final ResourceKey<Category> GAS         = GenFormula.categoryKey("gas");
	public static final ResourceKey<Category> FLUID       = GenFormula.categoryKey("fluid");
}
