package general.mechanics.materials;

import general.api.formula.GenFormula;
import general.api.formula.builtin.FormulaAtoms;
import general.api.formula.builtin.FormulaCategories;
import general.api.formula.builtin.FormulaTraits;
import general.api.formula.core.*;
import general.api.resources.Resource;
import general.mechanics.formula.Formulas;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * This mod's formula {@link Material materials}. Each material is declared once via
 * {@link #register(String, MaterialFactory)}, which creates its {@link ResourceKey} constant and records
 * the build recipe; {@link #bootstrap(BootstrapContext)} iterates and registers them all.
 */
public final class Materials {

	private Materials () {}

	private static final List<Entry> ENTRIES = new ArrayList<>();

	// Plastics
	public static final ResourceKey<Material> POLYETHYLENE = register("Polyethylene", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 2).atom(l.atom(FormulaAtoms.HYDROGEN), 4))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYPROPYLENE = register("Polypropylene", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 3).atom(l.atom(FormulaAtoms.HYDROGEN), 6))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYSTYRENE = register("Polystyrene", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 8).atom(l.atom(FormulaAtoms.HYDROGEN), 8))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYVINYL_CHLORIDE = register("Polyvinyl Chloride", (b, l) -> b
			.category(l.category(FormulaCategories.SYNTHETIC))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 2).atom(l.atom(FormulaAtoms.HYDROGEN), 3).atom(l.atom(FormulaAtoms.CHLORINE), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYETHYLENE_TEREPHTHALATE = register("Polyethylene Terephthalate", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 10).atom(l.atom(FormulaAtoms.HYDROGEN), 8).atom(l.atom(FormulaAtoms.OXYGEN), 4))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> ACRYLONITRILE_BUTADIENE_STYRENE = register("Acrylonitrile Butadiene Styrene", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder()
					.group(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 8).atom(l.atom(FormulaAtoms.HYDROGEN), 8), 1)
					.group(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 4).atom(l.atom(FormulaAtoms.HYDROGEN), 6), 1)
					.group(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 3).atom(l.atom(FormulaAtoms.HYDROGEN), 3).atom(l.atom(FormulaAtoms.NITROGEN), 1), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYCARBONATE = register("Polycarbonate", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 16).atom(l.atom(FormulaAtoms.HYDROGEN), 14).atom(l.atom(FormulaAtoms.OXYGEN), 3))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> NYLON = register("Nylon", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 6).atom(l.atom(FormulaAtoms.HYDROGEN), 11).atom(l.atom(FormulaAtoms.NITROGEN), 1).atom(l.atom(FormulaAtoms.OXYGEN), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYURETHANE = register("Polyurethane", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 3).atom(l.atom(FormulaAtoms.HYDROGEN), 8).atom(l.atom(FormulaAtoms.NITROGEN), 2).atom(l.atom(FormulaAtoms.OXYGEN), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYTETRAFLUOROETHYLENE = register("Polytetrafluoroethylene", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 2).atom(l.atom(FormulaAtoms.FLUORINE), 4))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> POLYETHERETHERKETONE = register("Polyetheretherketone", (b, l) -> b
			.category(l.category(FormulaCategories.POLYMER))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CARBON), 19).atom(l.atom(FormulaAtoms.HYDROGEN), 12).atom(l.atom(FormulaAtoms.OXYGEN), 3))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.ORGANIC))
			.color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	// Standard materials
	public static final ResourceKey<Material> WATER = register("Water", (b, l) -> b
			.category(l.category(FormulaCategories.FLUID))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.HYDROGEN), 2).atom(l.atom(FormulaAtoms.OXYGEN), 1))
			.trait(l.trait(FormulaTraits.ORGANIC)));

	public static final ResourceKey<Material> STEEL = register("Steel", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.IRON), 106).atom(l.atom(FormulaAtoms.CARBON), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFF8C8C9C).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> IRON = register("Iron", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.IRON), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE)).trait(l.trait(FormulaTraits.MAGNETIC)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFFD8D8D8).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> COPPER = register("Copper", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COPPER), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFE0853F).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> GOLD = register("Gold", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.GOLD), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFFFD700).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> STAINLESS_STEEL = register("Stainless Steel", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.IRON), 25).atom(l.atom(FormulaAtoms.CHROMIUM), 6).atom(l.atom(FormulaAtoms.NICKEL), 2))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFF8C8C9C).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> ALUMINIUM = register("Aluminium", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.ALUMINIUM), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE)).trait(l.trait(FormulaTraits.LIGHTWEIGHT)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFFD0D2D5).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> TITANIUM = register("Titanium", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.TITANIUM), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.LIGHTWEIGHT))
			.color(0xFFB0B5BA).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> TUNGSTEN = register("Tungsten", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.TUNGSTEN), 1))
			.trait(l.trait(FormulaTraits.DENSE)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFF6F7378).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> COBALT = register("Cobalt", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COBALT), 1))
			.trait(l.trait(FormulaTraits.MAGNETIC)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFF4A6FB0).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> NICKEL = register("Nickel", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.NICKEL), 1))
			.trait(l.trait(FormulaTraits.MAGNETIC)).trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFC9C6B8).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> ZINC = register("Zinc", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.ZINC), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFBFC9CC).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> TIN = register("Tin", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.TIN), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFC8CDD0).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> LEAD = register("Lead", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.LEAD), 1))
			.trait(l.trait(FormulaTraits.DENSE)).trait(l.trait(FormulaTraits.TOXIC))
			.color(0xFF575E66).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> SILVER = register("Silver", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.SILVER), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFE8E8EE).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> PLATINUM = register("Platinum", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.PLATINUM), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE)).trait(l.trait(FormulaTraits.CATALYTIC)).trait(l.trait(FormulaTraits.DENSE))
			.color(0xFFE5E4DF).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> CHROMIUM = register("Chromium", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.CHROMIUM), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.BRITTLE))
			.color(0xFFC6CDD2).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> MANGANESE = register("Manganese", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.MANGANESE), 1))
			.trait(l.trait(FormulaTraits.BRITTLE)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFF9C9AA0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> MAGNESIUM = register("Magnesium", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.MAGNESIUM), 1))
			.trait(l.trait(FormulaTraits.LIGHTWEIGHT)).trait(l.trait(FormulaTraits.FLAMMABLE)).trait(l.trait(FormulaTraits.REACTIVE))
			.color(0xFFCFCFC9).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> LITHIUM = register("Lithium", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.LITHIUM), 1))
			.trait(l.trait(FormulaTraits.LIGHTWEIGHT)).trait(l.trait(FormulaTraits.REACTIVE)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFB8BBC0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> URANIUM = register("Uranium", (b, l) -> b
			.category(l.category(FormulaCategories.RADIOACTIVE))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.URANIUM), 1))
			.trait(l.trait(FormulaTraits.RADIOACTIVE)).trait(l.trait(FormulaTraits.DENSE)).trait(l.trait(FormulaTraits.ENERGETIC))
			.color(0xFF5E7042).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.RAW, ItemForm.ROD, ItemForm.PILE));

	// Alloys — atom-ratio formulas (consistent with Steel/Stainless Steel above), approximating real
	// compositions. Declared after the pure metals so their constituents already exist.
	public static final ResourceKey<Material> BRONZE = register("Bronze", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COPPER), 7).atom(l.atom(FormulaAtoms.TIN), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFCD7F32).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> BRASS = register("Brass", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COPPER), 2).atom(l.atom(FormulaAtoms.ZINC), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFD1B642).form(ItemForm.getDefaultForms().toArray(ItemForm[]::new)));

	public static final ResourceKey<Material> ELECTRUM = register("Electrum", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.GOLD), 1).atom(l.atom(FormulaAtoms.SILVER), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFE8D98A).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> INVAR = register("Invar", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.IRON), 2).atom(l.atom(FormulaAtoms.NICKEL), 1))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.MAGNETIC))
			.color(0xFF9AA0A6).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> CONSTANTAN = register("Constantan", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COPPER), 5).atom(l.atom(FormulaAtoms.NICKEL), 4))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFB08D6A).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> CUPRONICKEL = register("Cupronickel", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COPPER), 3).atom(l.atom(FormulaAtoms.NICKEL), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFFC2A98E).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> SOLDER = register("Solder", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.TIN), 3).atom(l.atom(FormulaAtoms.LEAD), 2))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFB7BDC2).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.ROD, ItemForm.PILE));

	public static final ResourceKey<Material> NICHROME = register("Nichrome", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.NICKEL), 4).atom(l.atom(FormulaAtoms.CHROMIUM), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFF8E8E94).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE, ItemForm.ROD, ItemForm.PILE));

	/**
	 * Declares a material: creates its {@link ResourceKey} (path derived from {@code name}) and records the
	 * recipe for {@link #bootstrap(BootstrapContext)}.
	 */
	private static ResourceKey<Material> register (String name, MaterialFactory factory) {
		var key = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get(name.toLowerCase().replace(' ', '_')));
		ENTRIES.add(new Entry(name, key, factory));
		return key;
	}

	/**
	 * Bootstraps this mod's materials. Combined with the genapi materials in {@link Formulas}.
	 */
	public static void bootstrap (BootstrapContext<Material> ctx) {
		Lookups lookups = new Lookups(
				ctx.lookup(GenFormula.ATOM_REGISTRY),
				ctx.lookup(GenFormula.CATEGORY_REGISTRY),
				ctx.lookup(GenFormula.TRAIT_REGISTRY));

		for (Entry entry : ENTRIES) {
			Material.Builder builder = Material.builder(entry.name());
			entry.factory().configure(builder, lookups);
			ctx.register(entry.key(), builder.build());
		}
	}

	private record Entry(String name, ResourceKey<Material> key, MaterialFactory factory) {}

	/** Configures a pre-named {@link Material.Builder}; resolves atom/category/trait keys via {@link Lookups}. */
	@FunctionalInterface
	public interface MaterialFactory {
		void configure (Material.Builder builder, Lookups lookups);
	}

	/** Bootstrap-time holder lookups with convenience accessors. */
	public record Lookups(HolderGetter<Atom> atoms, HolderGetter<Category> categories, HolderGetter<Trait> traits) {

		public Holder<Atom> atom (ResourceKey<Atom> key) {
			return atoms.getOrThrow(key);
		}

		public Holder<Category> category (ResourceKey<Category> key) {
			return categories.getOrThrow(key);
		}

		public Holder<Trait> trait (ResourceKey<Trait> key) {
			return traits.getOrThrow(key);
		}
	}
}
