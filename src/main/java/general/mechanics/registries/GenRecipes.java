package general.mechanics.registries;

import general.api.crafting.*;
import general.api.mod.GenAPI;
import general.mechanics.common.block.CokeOvenController;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

/**
 * Owns the paired machine recipe registries. Individual machines retain their
 * schema and type declaration; this bootstrap only ensures those classes are
 * initialized before the deferred registries are attached.
 */
public final class GenRecipes {

	public static final MachineRecipeRegistry REGISTRY = MachineRecipeRegistry.create(GenAPI.getModId());

	public static final MachineRecipeDefinition<NoRecipeData> COKE_OVEN = REGISTRY.register("coke_oven", MachineRecipeSchema.builder().itemInput(CokeOvenController.RecipeSlots.INPUT).itemOutput(CokeOvenController.RecipeSlots.OUTPUT).fluidOutput(CokeOvenController.RecipeSlots.CREOSOTE).build()).craftingStation(() -> GenBlocks.COKE_OVEN_CONTROLLER);

	private GenRecipes () {
	}

	public static void register (IEventBus bus) {
		REGISTRY.register(bus);
	}

	/**
	 * Requests client synchronization for every registered machine recipe type.
	 */
	public static void syncRecipes (OnDatapackSyncEvent event) {
		for (var definition : REGISTRY.definitions()) event.sendRecipes(definition.type());
	}
}
