package general.api.formula.builtin;

import general.api.formula.GenFormula;
import general.api.formula.core.Atom;
import general.api.formula.core.Category;
import general.api.formula.core.Compound;
import general.api.formula.core.Formula;
import general.api.formula.core.ItemForm;
import general.api.formula.core.Material;
import general.api.formula.core.Trait;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;

/**
 * Populates the built-in {@code genapi} Formula API entries (categories, traits, atoms, compounds,
 * materials). Each method is a datapack-registry bootstrap, wired into a {@code RegistrySetBuilder}
 * by datagen; cross-references are resolved through the {@link BootstrapContext}.
 */
public final class GenFormulaBootstrap {

	private GenFormulaBootstrap () {}

	private static final int GREY = 0xFFB0B0B0;

	public static void categories (BootstrapContext<Category> ctx) {
		ctx.register(FormulaCategories.METAL, Category.builder("Metal").color(0xFFC8C8D0).build());
		ctx.register(FormulaCategories.NONMETAL, Category.builder("Nonmetal").color(0xFFB0D0B0).build());
		ctx.register(FormulaCategories.METALLOID, Category.builder("Metalloid").color(0xFFC0C0A0).build());
		ctx.register(FormulaCategories.NOBLE_GAS, Category.builder("Noble Gas").color(0xFFC8A8E0).build());
		ctx.register(FormulaCategories.ALLOY, Category.builder("Alloy").color(0xFFD0C8B0).build());
		ctx.register(FormulaCategories.POLYMER, Category.builder("Polymer").color(0xFFE0E0E0).build());
		ctx.register(FormulaCategories.ORGANIC, Category.builder("Organic").color(0xFFA8C890).build());
		ctx.register(FormulaCategories.INORGANIC, Category.builder("Inorganic").color(0xFFA8C0D0).build());
		ctx.register(FormulaCategories.SYNTHETIC, Category.builder("Synthetic").color(0xFFD0B0E0).build());
		ctx.register(FormulaCategories.FICTIONAL, Category.builder("Fictional").color(0xFFFF80C0).build());
		ctx.register(FormulaCategories.FICTIONAL_ALLOY, Category.builder("Fictional Alloy").color(0xFFFF90A0).build());
		ctx.register(FormulaCategories.FUEL, Category.builder("Fuel").color(0xFFE0A060).build());
		ctx.register(FormulaCategories.OXIDE, Category.builder("Oxide").color(0xFFD08070).build());
		ctx.register(FormulaCategories.SALT, Category.builder("Salt").color(0xFFE0E0F0).build());
		ctx.register(FormulaCategories.ACID, Category.builder("Acid").color(0xFFE0E060).build());
		ctx.register(FormulaCategories.BASE, Category.builder("Base").color(0xFF60C0E0).build());
		ctx.register(FormulaCategories.CIRCUIT, Category.builder("Circuit").color(0xFF80E0A0).build());
		ctx.register(FormulaCategories.MECHANICAL, Category.builder("Mechanical").color(0xFFB0B0B0).build());
		ctx.register(FormulaCategories.ELECTRICAL, Category.builder("Electrical").color(0xFFF0E060).build());
		ctx.register(FormulaCategories.RADIOACTIVE, Category.builder("Radioactive").color(0xFF80FF60).build());
		ctx.register(FormulaCategories.CRYSTAL, Category.builder("Crystal").color(0xFFA0E0F0).build());
		ctx.register(FormulaCategories.DUST, Category.builder("Dust").color(GREY).build());
		ctx.register(FormulaCategories.GAS, Category.builder("Gas").color(0xFFD0E0F0).build());
		ctx.register(FormulaCategories.FLUID, Category.builder("Fluid").color(0xFF70A0E0).build());
	}

	public static void traits (BootstrapContext<Trait> ctx) {
		trait(ctx, FormulaTraits.CONDUCTIVE, "Conductive", 0xFFF0E060);
		trait(ctx, FormulaTraits.MAGNETIC, "Magnetic", 0xFFC0C0D0);
		trait(ctx, FormulaTraits.REACTIVE, "Reactive", 0xFFE08060);
		trait(ctx, FormulaTraits.RADIOACTIVE, "Radioactive", 0xFF80FF60);
		trait(ctx, FormulaTraits.STABLE, "Stable", 0xFF90D090);
		trait(ctx, FormulaTraits.UNSTABLE, "Unstable", 0xFFFF7070);
		trait(ctx, FormulaTraits.ORGANIC, "Organic", 0xFFA8C890);
		trait(ctx, FormulaTraits.SYNTHETIC, "Synthetic", 0xFFD0B0E0);
		trait(ctx, FormulaTraits.BRITTLE, "Brittle", 0xFFC0A080);
		trait(ctx, FormulaTraits.DENSE, "Dense", 0xFF9090A0);
		trait(ctx, FormulaTraits.LIGHTWEIGHT, "Lightweight", 0xFFD0E0E0);
		trait(ctx, FormulaTraits.STRUCTURAL, "Structural", 0xFFB0B0C0);
		trait(ctx, FormulaTraits.ENERGETIC, "Energetic", 0xFFFF60FF);
		trait(ctx, FormulaTraits.FLAMMABLE, "Flammable", 0xFFFF8040);
		trait(ctx, FormulaTraits.INSULATING, "Insulating", 0xFFC8C8E0);
		trait(ctx, FormulaTraits.CATALYTIC, "Catalytic", 0xFF80E0C0);
		trait(ctx, FormulaTraits.TOXIC, "Toxic", 0xFF80FF60);
		trait(ctx, FormulaTraits.CORROSIVE, "Corrosive", 0xFFE0E060);
	}

	public static void atoms (BootstrapContext<Atom> ctx) {
		HolderGetter<Category> cats = ctx.lookup(GenFormula.CATEGORY_REGISTRY);
		atom(ctx, FormulaAtoms.HYDROGEN, "H", "Hydrogen", 1, 1.008, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.HELIUM, "He", "Helium", 2, 4.0026, cats, FormulaCategories.NOBLE_GAS);
		atom(ctx, FormulaAtoms.CARBON, "C", "Carbon", 6, 12.011, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.NITROGEN, "N", "Nitrogen", 7, 14.007, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.OXYGEN, "O", "Oxygen", 8, 15.999, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.FLUORINE, "F", "Fluorine", 9, 18.998, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.SODIUM, "Na", "Sodium", 11, 22.990, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ALUMINIUM, "Al", "Aluminium", 13, 26.982, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.SILICON, "Si", "Silicon", 14, 28.085, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.PHOSPHORUS, "P", "Phosphorus", 15, 30.974, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.SULFUR, "S", "Sulfur", 16, 32.06, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.CHLORINE, "Cl", "Chlorine", 17, 35.45, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.TITANIUM, "Ti", "Titanium", 22, 47.867, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.CHROMIUM, "Cr", "Chromium", 24, 51.996, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.IRON, "Fe", "Iron", 26, 55.845, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.COPPER, "Cu", "Copper", 29, 63.546, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ZINC, "Zn", "Zinc", 30, 65.38, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.GOLD, "Au", "Gold", 79, 196.97, cats, FormulaCategories.METAL);
		// Fictional
		ctx.register(FormulaAtoms.REDSTONE, Atom.builder("Rs").name("Redstone").category(cats.getOrThrow(FormulaCategories.FICTIONAL)).color(0xFFD81F1F).fictional(true).build());
	}

	public static void compounds (BootstrapContext<Compound> ctx) {
		HolderGetter<Atom> a = ctx.lookup(GenFormula.ATOM_REGISTRY);
		HolderGetter<Category> c = ctx.lookup(GenFormula.CATEGORY_REGISTRY);

		ctx.register(FormulaCompounds.WATER, Compound.builder("Water")
				.atom(a.getOrThrow(FormulaAtoms.HYDROGEN), 2)
				.atom(a.getOrThrow(FormulaAtoms.OXYGEN), 1)
				.category(c.getOrThrow(FormulaCategories.INORGANIC))
				.build());

		ctx.register(FormulaCompounds.IRON_OXIDE, Compound.builder("Iron Oxide")
				.atom(a.getOrThrow(FormulaAtoms.IRON), 2)
				.atom(a.getOrThrow(FormulaAtoms.OXYGEN), 3)
				.category(c.getOrThrow(FormulaCategories.OXIDE))
				.build());

		ctx.register(FormulaCompounds.COPPER_SULFATE, Compound.builder("Copper Sulfate")
				.atom(a.getOrThrow(FormulaAtoms.COPPER), 1)
				.atom(a.getOrThrow(FormulaAtoms.SULFUR), 1)
				.atom(a.getOrThrow(FormulaAtoms.OXYGEN), 4)
				.category(c.getOrThrow(FormulaCategories.SALT))
				.build());

		ctx.register(FormulaCompounds.GLUCOSE, Compound.builder("Glucose")
				.atom(a.getOrThrow(FormulaAtoms.CARBON), 6)
				.atom(a.getOrThrow(FormulaAtoms.HYDROGEN), 12)
				.atom(a.getOrThrow(FormulaAtoms.OXYGEN), 6)
				.category(c.getOrThrow(FormulaCategories.ORGANIC))
				.build());

		// (NH₄)₂SO₄ — nested group example.
		ctx.register(FormulaCompounds.AMMONIUM_SULFATE, Compound.builder("Ammonium Sulfate")
				.group(Compound.group()
						.atom(a.getOrThrow(FormulaAtoms.NITROGEN), 1)
						.atom(a.getOrThrow(FormulaAtoms.HYDROGEN), 4), 2)
				.atom(a.getOrThrow(FormulaAtoms.SULFUR), 1)
				.atom(a.getOrThrow(FormulaAtoms.OXYGEN), 4)
				.category(c.getOrThrow(FormulaCategories.SALT))
				.build());
	}

	public static void materials (BootstrapContext<Material> ctx) {
		HolderGetter<Atom> a = ctx.lookup(GenFormula.ATOM_REGISTRY);
		HolderGetter<Material> m = ctx.lookup(GenFormula.MATERIAL_REGISTRY);
		HolderGetter<Category> c = ctx.lookup(GenFormula.CATEGORY_REGISTRY);
		HolderGetter<Trait> t = ctx.lookup(GenFormula.TRAIT_REGISTRY);

		ctx.register(FormulaMaterials.IRON, Material.builder("Iron")
				.category(c.getOrThrow(FormulaCategories.METAL))
				.atom(a.getOrThrow(FormulaAtoms.IRON), 1)
				.trait(t.getOrThrow(FormulaTraits.CONDUCTIVE))
				.trait(t.getOrThrow(FormulaTraits.MAGNETIC))
				.trait(t.getOrThrow(FormulaTraits.STRUCTURAL))
				.color(0xFFD8D8D8)
				.form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE)
				.build());

		ctx.register(FormulaMaterials.COPPER, Material.builder("Copper")
				.category(c.getOrThrow(FormulaCategories.METAL))
				.atom(a.getOrThrow(FormulaAtoms.COPPER), 1)
				.trait(t.getOrThrow(FormulaTraits.CONDUCTIVE))
				.color(0xFFE0853F)
				.form(ItemForm.DUST, ItemForm.INGOT, ItemForm.WIRE, ItemForm.PLATE)
				.build());

		ctx.register(FormulaMaterials.STEEL, Material.builder("Steel")
				.category(c.getOrThrow(FormulaCategories.ALLOY))
				.material(m.getOrThrow(FormulaMaterials.IRON), 98)
				.atom(a.getOrThrow(FormulaAtoms.CARBON), 2)
				.trait(t.getOrThrow(FormulaTraits.STRUCTURAL))
				.trait(t.getOrThrow(FormulaTraits.CONDUCTIVE))
				.color(0xFF8C8C9C)
				.form(ItemForm.INGOT, ItemForm.PLATE, ItemForm.ROD, ItemForm.GEAR)
				.build());

		ctx.register(FormulaMaterials.REDSTONE, Material.builder("Redstone")
				.category(c.getOrThrow(FormulaCategories.ELECTRICAL))
				.atom(a.getOrThrow(FormulaAtoms.REDSTONE), 1)
				.trait(t.getOrThrow(FormulaTraits.CONDUCTIVE))
				.trait(t.getOrThrow(FormulaTraits.ENERGETIC))
				.color(0xFFD81F1F)
				.fictional(true)
				.form(ItemForm.DUST, ItemForm.CRYSTAL)
				.build());

		ctx.register(FormulaMaterials.SILICON, Material.builder("Silicon")
				.category(c.getOrThrow(FormulaCategories.METALLOID))
				.atom(a.getOrThrow(FormulaAtoms.SILICON), 1)
				.trait(t.getOrThrow(FormulaTraits.INSULATING))
				.color(0xFF4A4A52)
				.form(ItemForm.CRYSTAL, ItemForm.PLATE)
				.build());
	}

	// ------------------------------------------------------------------------------------------------

	private static void trait (BootstrapContext<Trait> ctx, net.minecraft.resources.ResourceKey<Trait> key, String name, int color) {
		ctx.register(key, Trait.builder(name).color(color).build());
	}

	private static void atom (BootstrapContext<Atom> ctx, net.minecraft.resources.ResourceKey<Atom> key, String symbol, String name,
	                          int number, double mass, HolderGetter<Category> cats, net.minecraft.resources.ResourceKey<Category> category) {
		ctx.register(key, Atom.builder(symbol).name(name).atomicNumber(number).atomicMass(mass)
				.category(cats.getOrThrow(category)).build());
	}
}
