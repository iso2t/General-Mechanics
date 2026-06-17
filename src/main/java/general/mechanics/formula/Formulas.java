package general.mechanics.formula;

import general.api.formula.GenFormula;
import general.api.formula.builtin.GenFormulaBootstrap;
import general.api.formula.codec.FormulaCodecs;
import general.mechanics.materials.Materials;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class Formulas {

	private Formulas () {}

	public static void onNewDataPackRegistry (DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(GenFormula.CATEGORY_REGISTRY, FormulaCodecs.CATEGORY, FormulaCodecs.CATEGORY);
		event.dataPackRegistry(GenFormula.TRAIT_REGISTRY, FormulaCodecs.TRAIT, FormulaCodecs.TRAIT);
		event.dataPackRegistry(GenFormula.ATOM_REGISTRY, FormulaCodecs.ATOM, FormulaCodecs.ATOM);
		event.dataPackRegistry(GenFormula.COMPOUND_REGISTRY, FormulaCodecs.COMPOUND, FormulaCodecs.COMPOUND);
		event.dataPackRegistry(GenFormula.MATERIAL_REGISTRY, FormulaCodecs.MATERIAL, FormulaCodecs.MATERIAL);
	}

	public static RegistrySetBuilder formulaRegistrySet () {
		return new RegistrySetBuilder()
				.add(GenFormula.CATEGORY_REGISTRY, GenFormulaBootstrap::categories)
				.add(GenFormula.TRAIT_REGISTRY, GenFormulaBootstrap::traits)
				.add(GenFormula.ATOM_REGISTRY, GenFormulaBootstrap::atoms)
				.add(GenFormula.COMPOUND_REGISTRY, GenFormulaBootstrap::compounds)
				.add(GenFormula.MATERIAL_REGISTRY, ctx -> {
					GenFormulaBootstrap.materials(ctx);
					Materials.bootstrap(ctx);
				});
	}
}
