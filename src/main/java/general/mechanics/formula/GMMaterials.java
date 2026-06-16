package general.mechanics.formula;

import general.api.formula.GenFormula;
import general.api.formula.builtin.FormulaAtoms;
import general.api.formula.builtin.FormulaCategories;
import general.api.formula.builtin.FormulaTraits;
import general.api.formula.core.*;
import general.api.resources.Resource;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public final class GMMaterials {

	private GMMaterials () {
	}

	public static final ResourceKey<Material> POLYETHYLENE                    = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polyethylene"));
	public static final ResourceKey<Material> POLYPROPYLENE                   = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polypropylene"));
	public static final ResourceKey<Material> POLYSTYRENE                     = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polystyrene"));
	public static final ResourceKey<Material> POLYVINYL_CHLORIDE              = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polyvinyl_chloride"));
	public static final ResourceKey<Material> POLYETHYLENE_TEREPHTHALATE      = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polyethylene_terephthalate"));
	public static final ResourceKey<Material> ACRYLONITRILE_BUTADIENE_STYRENE = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("acrylonitrile_butadiene_styrene"));
	public static final ResourceKey<Material> POLYCARBONATE                   = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polycarbonate"));
	public static final ResourceKey<Material> NYLON                           = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("nylon"));
	public static final ResourceKey<Material> POLYURETHANE                    = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polyurethane"));
	public static final ResourceKey<Material> POLYTETRAFLUOROETHYLENE         = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polytetrafluoroethylene"));
	public static final ResourceKey<Material> POLYETHERETHERKETONE            = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("polyetheretherketone"));

	public static final ResourceKey<Material> STEEL                           = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("steel"));

	/**
	 * Standard materials.
	 */
	public static final ResourceKey<Material> WATER = ResourceKey.create(GenFormula.MATERIAL_REGISTRY, Resource.get("water"));

	/**
	 * Bootstraps this mod's materials. Combined with the genapi materials in {@link GMFormula}.
	 */
	public static void bootstrap (BootstrapContext<Material> ctx) {
		HolderGetter<Atom> atoms = ctx.lookup(GenFormula.ATOM_REGISTRY);
		HolderGetter<Category> categories = ctx.lookup(GenFormula.CATEGORY_REGISTRY);
		HolderGetter<Trait> traits = ctx.lookup(GenFormula.TRAIT_REGISTRY);

		ctx.register(POLYETHYLENE, Material.builder("Polyethylene").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 2).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 4)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYPROPYLENE, Material.builder("Polypropylene").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 3).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 6)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYSTYRENE, Material.builder("Polystyrene").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 8).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 8)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYVINYL_CHLORIDE, Material.builder("Polyvinyl chloride").category(categories.getOrThrow(FormulaCategories.SYNTHETIC)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 2).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 3).atom(atoms.getOrThrow(FormulaAtoms.CHLORINE), 1)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYETHYLENE_TEREPHTHALATE, Material.builder("Polyethylene Terephthalate").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 10).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 8).atom(atoms.getOrThrow(FormulaAtoms.OXYGEN), 4)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(ACRYLONITRILE_BUTADIENE_STYRENE, Material.builder("Acrylonitrile Butadiene Styrene").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().group(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 8).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 8), 1).group(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 4).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 6), 1).group(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 3).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 3).atom(atoms.getOrThrow(FormulaAtoms.NITROGEN), 1), 1)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYCARBONATE, Material.builder("Polycarbonate").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 16).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 14).atom(atoms.getOrThrow(FormulaAtoms.OXYGEN), 3)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(NYLON, Material.builder("Nylon").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 6).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 11).atom(atoms.getOrThrow(FormulaAtoms.NITROGEN), 1).atom(atoms.getOrThrow(FormulaAtoms.OXYGEN), 1)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYURETHANE, Material.builder("Polyurethane").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 3).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 8).atom(atoms.getOrThrow(FormulaAtoms.NITROGEN), 2).atom(atoms.getOrThrow(FormulaAtoms.OXYGEN), 1)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYTETRAFLUOROETHYLENE, Material.builder("Polytetrafluoroethylene").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 2).atom(atoms.getOrThrow(FormulaAtoms.FLUORINE), 4)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());
		ctx.register(POLYETHERETHERKETONE, Material.builder("Polyetheretherketone").category(categories.getOrThrow(FormulaCategories.POLYMER)).formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.CARBON), 19).atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 12).atom(atoms.getOrThrow(FormulaAtoms.OXYGEN), 3)).trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.ORGANIC)).color(0xFFD0D0D0).form(ItemForm.DUST, ItemForm.INGOT, ItemForm.NUGGET, ItemForm.PLATE).build());

		ctx.register(WATER, Material.builder("Water").category(categories.getOrThrow(FormulaCategories.FLUID))
				.formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.HYDROGEN), 2).atom(atoms.getOrThrow(FormulaAtoms.OXYGEN), 1))
				.trait(traits.getOrThrow(FormulaTraits.ORGANIC)).build());

		ctx.register(STEEL, Material.builder("Steel").category(categories.getOrThrow(FormulaCategories.ALLOY))
				.formula(Formula.builder().atom(atoms.getOrThrow(FormulaAtoms.IRON), 106).atom(atoms.getOrThrow(FormulaAtoms.CARBON), 1))
				.trait(traits.getOrThrow(FormulaTraits.STRUCTURAL)).trait(traits.getOrThrow(FormulaTraits.CONDUCTIVE)).color(0xFF8C8C9C).form(ItemForm.INGOT, ItemForm.PLATE, ItemForm.ROD, ItemForm.GEAR).build());
	}
}
