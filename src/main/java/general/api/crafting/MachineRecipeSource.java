package general.api.crafting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

/**
 * Resolves native or adapted recipes into the common machine execution shape.
 *
 * <p>Sources are evaluated in declaration order. The first source returning at
 * least one matching recipe owns that tick, preventing a lower-priority fallback
 * from bypassing a blocked higher-priority recipe.</p>
 */
@FunctionalInterface
public interface MachineRecipeSource {

	List<RecipeHolder<MachineRecipe>> findMatching (MachineRecipeInput input, ServerLevel level);
}
