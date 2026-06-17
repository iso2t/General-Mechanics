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
			.color(0xFF8C8C9C).form(ItemForm.INGOT, ItemForm.PLATE, ItemForm.ROD, ItemForm.GEAR));

	public static final ResourceKey<Material> IRON = register("Iron", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.IRON), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE)).trait(l.trait(FormulaTraits.MAGNETIC)).trait(l.trait(FormulaTraits.STRUCTURAL))
			.color(0xFFD8D8D8).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE));

	public static final ResourceKey<Material> COPPER = register("Copper", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.COPPER), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFE0853F).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.WIRE, ItemForm.PLATE));

	public static final ResourceKey<Material> GOLD = register("Gold", (b, l) -> b
			.category(l.category(FormulaCategories.METAL))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.GOLD), 1))
			.trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFFFFD700).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.WIRE, ItemForm.PLATE));

	public static final ResourceKey<Material> STAINLESS_STEEL = register("Stainless Steel", (b, l) -> b
			.category(l.category(FormulaCategories.ALLOY))
			.formula(Formula.builder().atom(l.atom(FormulaAtoms.IRON), 25).atom(l.atom(FormulaAtoms.CHROMIUM), 6).atom(l.atom(FormulaAtoms.NICKEL), 2))
			.trait(l.trait(FormulaTraits.STRUCTURAL)).trait(l.trait(FormulaTraits.CONDUCTIVE))
			.color(0xFF8C8C9C).form(ItemForm.INGOT, ItemForm.PLATE, ItemForm.ROD, ItemForm.GEAR));

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
