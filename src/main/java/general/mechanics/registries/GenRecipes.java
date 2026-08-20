package general.mechanics.registries;

import general.api.crafting.MachineRecipeRegistry;
import general.api.mod.GenAPI;
import general.mechanics.common.block.CokeOvenController;
import net.neoforged.bus.api.IEventBus;

/**
 * Owns the paired machine recipe registries. Individual machines retain their
 * schema and type declaration; this bootstrap only ensures those classes are
 * initialized before the deferred registries are attached.
 */
public final class GenRecipes {

	public static final MachineRecipeRegistry REGISTRY = MachineRecipeRegistry.create(GenAPI.getModId());

	private GenRecipes () {
	}

	public static void register (IEventBus bus) {
		CokeOvenController.recipeDefinition();
		REGISTRY.register(bus);
	}
}
