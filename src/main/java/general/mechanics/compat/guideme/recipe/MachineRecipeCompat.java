package general.mechanics.compat.guideme.recipe;

import general.api.crafting.MachineRecipeCatalog;
import guideme.compiler.tags.RecipeTypeMappingSupplier;

/**
 * Registers every declarative machine recipe type with GuideME.
 */
public final class MachineRecipeCompat implements RecipeTypeMappingSupplier {

	@Override
	public void collect (RecipeTypeMappings mappings) {
		for (var definition : MachineRecipeCatalog.definitions()) {
			mappings.add(definition.type(), holder -> MachineRecipeGuideRenderer.create(definition, holder));
		}
	}

}
