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
		ctx.register(FormulaCategories.ALKALI_METAL, Category.builder("Alkali Metal").color(0xFFC0C0D0).build());
		ctx.register(FormulaCategories.ALKALINE_EARTH_METAL, Category.builder("Alkaline Earth Metal").color(0xFFC0D0C0).build());
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

		// Standard atomic weights from the IUPAC periodic table (g/mol); bracketed/[n] masses use the
		// most-stable-isotope value for elements with no stable isotope. Categories map to the closest
		// available bucket: group categories where one exists (alkali/alkaline-earth/noble-gas/metalloid/
		// nonmetal), METAL for transition/post-transition/lanthanide metals, and RADIOACTIVE for every
		// element with Z >= 89 (actinides + superheavies), which lack a dedicated block category.

		// Period 1
		atom(ctx, FormulaAtoms.HYDROGEN, "H", "Hydrogen", 1, 1.008, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.HELIUM, "He", "Helium", 2, 4.0026, cats, FormulaCategories.NOBLE_GAS);

		// Period 2
		atom(ctx, FormulaAtoms.LITHIUM, "Li", "Lithium", 3, 6.941, cats, FormulaCategories.ALKALI_METAL);
		atom(ctx, FormulaAtoms.BERYLLIUM, "Be", "Beryllium", 4, 9.0122, cats, FormulaCategories.ALKALINE_EARTH_METAL);
		atom(ctx, FormulaAtoms.BORON, "B", "Boron", 5, 10.811, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.CARBON, "C", "Carbon", 6, 12.011, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.NITROGEN, "N", "Nitrogen", 7, 14.007, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.OXYGEN, "O", "Oxygen", 8, 15.999, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.FLUORINE, "F", "Fluorine", 9, 18.998, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.NEON, "Ne", "Neon", 10, 20.180, cats, FormulaCategories.NOBLE_GAS);

		// Period 3
		atom(ctx, FormulaAtoms.SODIUM, "Na", "Sodium", 11, 22.990, cats, FormulaCategories.ALKALI_METAL);
		atom(ctx, FormulaAtoms.MAGNESIUM, "Mg", "Magnesium", 12, 24.305, cats, FormulaCategories.ALKALINE_EARTH_METAL);
		atom(ctx, FormulaAtoms.ALUMINIUM, "Al", "Aluminium", 13, 26.982, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.SILICON, "Si", "Silicon", 14, 28.085, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.PHOSPHORUS, "P", "Phosphorus", 15, 30.974, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.SULFUR, "S", "Sulfur", 16, 32.06, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.CHLORINE, "Cl", "Chlorine", 17, 35.45, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.ARGON, "Ar", "Argon", 18, 39.948, cats, FormulaCategories.NOBLE_GAS);

		// Period 4
		atom(ctx, FormulaAtoms.POTASSIUM, "K", "Potassium", 19, 39.098, cats, FormulaCategories.ALKALI_METAL);
		atom(ctx, FormulaAtoms.CALCIUM, "Ca", "Calcium", 20, 40.078, cats, FormulaCategories.ALKALINE_EARTH_METAL);
		atom(ctx, FormulaAtoms.SCANDIUM, "Sc", "Scandium", 21, 44.956, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.TITANIUM, "Ti", "Titanium", 22, 47.867, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.VANADIUM, "V", "Vanadium", 23, 50.942, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.CHROMIUM, "Cr", "Chromium", 24, 51.996, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.MANGANESE, "Mn", "Manganese", 25, 54.938, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.IRON, "Fe", "Iron", 26, 55.845, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.COBALT, "Co", "Cobalt", 27, 58.933, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.NICKEL, "Ni", "Nickel", 28, 58.6934, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.COPPER, "Cu", "Copper", 29, 63.546, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ZINC, "Zn", "Zinc", 30, 65.38, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.GALLIUM, "Ga", "Gallium", 31, 69.723, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.GERMANIUM, "Ge", "Germanium", 32, 72.630, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.ARSENIC, "As", "Arsenic", 33, 74.922, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.SELENIUM, "Se", "Selenium", 34, 78.971, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.BROMINE, "Br", "Bromine", 35, 79.904, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.KRYPTON, "Kr", "Krypton", 36, 83.798, cats, FormulaCategories.NOBLE_GAS);

		// Period 5 (ruthenium, Z=44, has no FormulaAtoms key and is intentionally skipped)
		atom(ctx, FormulaAtoms.RUBIDIUM, "Rb", "Rubidium", 37, 85.468, cats, FormulaCategories.ALKALI_METAL);
		atom(ctx, FormulaAtoms.STRONTIUM, "Sr", "Strontium", 38, 87.62, cats, FormulaCategories.ALKALINE_EARTH_METAL);
		atom(ctx, FormulaAtoms.YTTRIUM, "Y", "Yttrium", 39, 88.906, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ZIRCONIUM, "Zr", "Zirconium", 40, 91.224, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.NIOBIUM, "Nb", "Niobium", 41, 92.906, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.MOLYBDENUM, "Mo", "Molybdenum", 42, 95.95, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.TECHNETIUM, "Tc", "Technetium", 43, 98, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.RHODIUM, "Rh", "Rhodium", 45, 102.91, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.PALLADIUM, "Pd", "Palladium", 46, 106.42, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.SILVER, "Ag", "Silver", 47, 107.87, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.CADMIUM, "Cd", "Cadmium", 48, 112.41, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.INDIUM, "In", "Indium", 49, 114.82, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.TIN, "Sn", "Tin", 50, 118.71, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ANTIMONY, "Sb", "Antimony", 51, 121.76, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.TELLURIUM, "Te", "Tellurium", 52, 127.60, cats, FormulaCategories.METALLOID);
		atom(ctx, FormulaAtoms.IODINE, "I", "Iodine", 53, 126.90, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.XENON, "Xe", "Xenon", 54, 131.29, cats, FormulaCategories.NOBLE_GAS);

		// Period 6
		atom(ctx, FormulaAtoms.CAESIUM, "Cs", "Caesium", 55, 132.91, cats, FormulaCategories.ALKALI_METAL);
		atom(ctx, FormulaAtoms.BARIUM, "Ba", "Barium", 56, 137.33, cats, FormulaCategories.ALKALINE_EARTH_METAL);
		// Lanthanides (57-71)
		atom(ctx, FormulaAtoms.LANTHANUM, "La", "Lanthanum", 57, 138.91, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.CERIUM, "Ce", "Cerium", 58, 140.12, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.PRASEODYMIUM, "Pr", "Praseodymium", 59, 140.91, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.NEODYMIUM, "Nd", "Neodymium", 60, 144.24, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.PROMETHIUM, "Pm", "Promethium", 61, 145, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.SAMARIUM, "Sm", "Samarium", 62, 150.36, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.EUROPIUM, "Eu", "Europium", 63, 151.96, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.GADOLINIUM, "Gd", "Gadolinium", 64, 157.25, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.TERBIUM, "Tb", "Terbium", 65, 158.93, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.DYSPROSIUM, "Dy", "Dysprosium", 66, 162.50, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.HOLMIUM, "Ho", "Holmium", 67, 164.93, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ERBIUM, "Er", "Erbium", 68, 167.26, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.THULIUM, "Tm", "Thulium", 69, 168.93, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.YTTERBIUM, "Yb", "Ytterbium", 70, 173.05, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.LUTETIUM, "Lu", "Lutetium", 71, 174.97, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.HAFNIUM, "Hf", "Hafnium", 72, 178.49, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.TANTALUM, "Ta", "Tantalum", 73, 180.95, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.TUNGSTEN, "W", "Tungsten", 74, 183.84, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.RHENIUM, "Re", "Rhenium", 75, 186.21, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.OSMIUM, "Os", "Osmium", 76, 190.23, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.IRIDIUM, "Ir", "Iridium", 77, 192.22, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.PLATINUM, "Pt", "Platinum", 78, 195.08, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.GOLD, "Au", "Gold", 79, 196.97, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.MERCURY, "Hg", "Mercury", 80, 200.59, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.THALLIUM, "Tl", "Thallium", 81, 204.38, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.LEAD, "Pb", "Lead", 82, 207.2, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.BISMUTH, "Bi", "Bismuth", 83, 208.98, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.POLONIUM, "Po", "Polonium", 84, 209, cats, FormulaCategories.METAL);
		atom(ctx, FormulaAtoms.ASTATINE, "At", "Astatine", 85, 210, cats, FormulaCategories.NONMETAL);
		atom(ctx, FormulaAtoms.RADON, "Rn", "Radon", 86, 222, cats, FormulaCategories.NOBLE_GAS);

		// Period 7
		atom(ctx, FormulaAtoms.FRANCIUM, "Fr", "Francium", 87, 223, cats, FormulaCategories.ALKALI_METAL);
		atom(ctx, FormulaAtoms.RADIUM, "Ra", "Radium", 88, 226, cats, FormulaCategories.ALKALINE_EARTH_METAL);
		// Actinides (89-103) — all radioactive
		atom(ctx, FormulaAtoms.ACTINIUM, "Ac", "Actinium", 89, 227, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.THORIUM, "Th", "Thorium", 90, 232.04, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.PROTACTINIUM, "Pa", "Protactinium", 91, 231.04, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.URANIUM, "U", "Uranium", 92, 238.03, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.NEPTUNIUM, "Np", "Neptunium", 93, 237, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.PLUTONIUM, "Pu", "Plutonium", 94, 244, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.AMERICIUM, "Am", "Americium", 95, 243, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.CURIUM, "Cm", "Curium", 96, 247, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.BERKELIUM, "Bk", "Berkelium", 97, 247, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.CALIFORNIUM, "Cf", "Californium", 98, 251, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.EINSTEINIUM, "Es", "Einsteinium", 99, 252, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.FERMIUM, "Fm", "Fermium", 100, 257, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.MENDELEVIUM, "Md", "Mendelevium", 101, 258, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.NOBELIUM, "No", "Nobelium", 102, 259, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.LAWRENCIUM, "Lr", "Lawrencium", 103, 266, cats, FormulaCategories.RADIOACTIVE);
		// Transactinides / superheavy synthetic elements (104-118) — all radioactive
		atom(ctx, FormulaAtoms.RUTHERFORDIUM, "Rf", "Rutherfordium", 104, 267, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.DUBNIUM, "Db", "Dubnium", 105, 268, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.SEABORGIUM, "Sg", "Seaborgium", 106, 269, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.BOHRIUM, "Bh", "Bohrium", 107, 270, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.HASSIUM, "Hs", "Hassium", 108, 269, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.MEITNERIUM, "Mt", "Meitnerium", 109, 278, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.DARMSTADTIUM, "Ds", "Darmstadtium", 110, 281, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.ROENTGENIUM, "Rg", "Roentgenium", 111, 282, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.COPERNICIUM, "Cn", "Copernicium", 112, 285, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.NIHONIUM, "Nh", "Nihonium", 113, 286, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.FLEROVIUM, "Fl", "Flerovium", 114, 289, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.MOSCOVIUM, "Mc", "Moscovium", 115, 290, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.LIVERMORIUM, "Lv", "Livermorium", 116, 293, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.TENNESSINE, "Ts", "Tennessine", 117, 294, cats, FormulaCategories.RADIOACTIVE);
		atom(ctx, FormulaAtoms.OGANESSON, "Og", "Oganesson", 118, 294, cats, FormulaCategories.RADIOACTIVE);

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

	private static void trait (BootstrapContext<Trait> ctx, net.minecraft.resources.ResourceKey<Trait> key, String name, int color) {
		ctx.register(key, Trait.builder(name).color(color).build());
	}

	private static void atom (BootstrapContext<Atom> ctx, net.minecraft.resources.ResourceKey<Atom> key, String symbol, String name, int number, double mass, HolderGetter<Category> cats, net.minecraft.resources.ResourceKey<Category> category) {
		ctx.register(key, Atom.builder(symbol).name(name).atomicNumber(number).atomicMass(mass)
				.category(cats.getOrThrow(category)).build());
	}
}
