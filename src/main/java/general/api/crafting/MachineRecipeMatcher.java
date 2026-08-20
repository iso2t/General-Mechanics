package general.api.crafting;

import net.minecraft.world.level.Level;

/**
 * Optional type-specific predicate evaluated after all declared resource inputs
 * match. The payload is strongly typed by its {@link MachineRecipeDefinition}.
 */
@FunctionalInterface
public interface MachineRecipeMatcher<D> {

	boolean matches (D data, MachineRecipeInput input, Level level);
}
