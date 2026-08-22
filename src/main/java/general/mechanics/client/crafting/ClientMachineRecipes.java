package general.mechanics.client.crafting;

import general.api.crafting.MachineRecipe;
import general.api.crafting.MachineRecipeDefinition;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side snapshot of the full machine recipes requested during datapack sync.
 */
public final class ClientMachineRecipes {

	private static RecipeMap recipes = RecipeMap.EMPTY;

	private ClientMachineRecipes () {
	}

	public static void receive (RecipesReceivedEvent event) {
		recipes = event.getRecipeMap();
	}

	public static void logout (ClientPlayerNetworkEvent.LoggingOut event) {
		recipes = RecipeMap.EMPTY;
	}

	public static List<RecipeHolder<MachineRecipe>> get (MachineRecipeDefinition<?> definition) {
		var result = new ArrayList<RecipeHolder<MachineRecipe>>();
		result.addAll(recipes.byType(definition.type()));
		result.addAll(definition.importedRecipes(recipes));
		return List.copyOf(result);
	}
}
