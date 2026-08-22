package general.api.crafting;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global discovery index for every machine recipe definition created through
 * the API, including definitions owned by other mods.
 *
 * <p>Mods still own separate {@link MachineRecipeRegistry} instances so their
 * recipe types and serializers attach to the correct mod event bus. The catalog
 * is a read-only integration view used by synchronization, recipe viewers, and
 * other consumers that need to discover all participating definitions.</p>
 */
public final class MachineRecipeCatalog {

	private static final Map<Identifier, MachineRecipeDefinition<?>> DEFINITIONS = new ConcurrentHashMap<>();

	private MachineRecipeCatalog () {
	}

	/**
	 * Returns every known definition in stable identifier order.
	 */
	public static List<MachineRecipeDefinition<?>> definitions () {
		return DEFINITIONS.values().stream().sorted(Comparator.comparing(definition -> definition.id().toString())).toList();
	}

	/**
	 * Returns definitions owned by one namespace in stable identifier order.
	 */
	public static List<MachineRecipeDefinition<?>> definitions (String namespace) {
		Objects.requireNonNull(namespace, "namespace");
		return definitions().stream().filter(definition -> definition.id().getNamespace().equals(namespace)).toList();
	}

	public static Optional<MachineRecipeDefinition<?>> find (Identifier id) {
		return Optional.ofNullable(DEFINITIONS.get(Objects.requireNonNull(id, "id")));
	}

	/**
	 * Requests full client synchronization for every catalogued recipe type.
	 */
	public static void syncRecipes (OnDatapackSyncEvent event) {
		Objects.requireNonNull(event, "event");
		for (MachineRecipeDefinition<?> definition : definitions()) {
			event.sendRecipes(definition.type());
			event.sendRecipes(definition.importedRecipeTypes());
		}
	}

	static void add (MachineRecipeDefinition<?> definition) {
		Objects.requireNonNull(definition, "definition");
		MachineRecipeDefinition<?> existing = DEFINITIONS.putIfAbsent(definition.id(), definition);
		if (existing != null && existing != definition) {
			throw new IllegalArgumentException("Duplicate machine recipe definition '" + definition.id() + "'");
		}
	}
}
