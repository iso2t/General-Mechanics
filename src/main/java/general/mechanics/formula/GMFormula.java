package general.mechanics.formula;

import general.api.formula.GenFormula;
import general.api.formula.builtin.GenFormulaBootstrap;
import general.api.formula.codec.FormulaCodecs;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/**
 * Mod-side wiring for the GenAPI Formula API: registers the datapack registries (synced) on the mod
 * bus and exposes the built-in {@link RegistrySetBuilder} used by datagen to emit the {@code genapi}
 * JSON entries.
 */
public final class GMFormula {

	private GMFormula () {}

	/** Registers the five Formula API datapack registries with element + network codecs (synced). */
	public static void onNewDataPackRegistry (DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(GenFormula.CATEGORY_REGISTRY, FormulaCodecs.CATEGORY, FormulaCodecs.CATEGORY);
		event.dataPackRegistry(GenFormula.TRAIT_REGISTRY, FormulaCodecs.TRAIT, FormulaCodecs.TRAIT);
		event.dataPackRegistry(GenFormula.ATOM_REGISTRY, FormulaCodecs.ATOM, FormulaCodecs.ATOM);
		event.dataPackRegistry(GenFormula.COMPOUND_REGISTRY, FormulaCodecs.COMPOUND, FormulaCodecs.COMPOUND);
		event.dataPackRegistry(GenFormula.MATERIAL_REGISTRY, FormulaCodecs.MATERIAL, FormulaCodecs.MATERIAL);
	}

	/** A fresh {@link RegistrySetBuilder} with the built-in {@code genapi} entries + this mod's materials. */
	public static RegistrySetBuilder formulaRegistrySet () {
		return new RegistrySetBuilder()
				.add(GenFormula.CATEGORY_REGISTRY, GenFormulaBootstrap::categories)
				.add(GenFormula.TRAIT_REGISTRY, GenFormulaBootstrap::traits)
				.add(GenFormula.ATOM_REGISTRY, GenFormulaBootstrap::atoms)
				.add(GenFormula.COMPOUND_REGISTRY, GenFormulaBootstrap::compounds)
				.add(GenFormula.MATERIAL_REGISTRY, ctx -> {
					GenFormulaBootstrap.materials(ctx);
					GMMaterials.bootstrap(ctx);
				});
	}
}
