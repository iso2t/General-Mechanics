package general.api.formula;

import general.api.formula.core.*;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

/**
 * Central facade for the GenAPI Formula API: the library namespace, the five datapack-registry keys
 * (atoms, compounds, materials, categories, traits), and id/lookup helpers.
 *
 * <p>The registries are NeoForge datapack registries (synced to clients, datapack-overridable). Built
 * in entries live under the {@code genapi} {@linkplain #NAMESPACE namespace}; modder entries use their
 * own mod id. Entries are authored in code via a {@code BootstrapContext} and emitted to JSON by datagen.
 */
public final class GenFormula {

	public static final String NAMESPACE = "genapi";

	public static final ResourceKey<Registry<Category>> CATEGORY_REGISTRY = registryKey("category");
	public static final ResourceKey<Registry<Trait>>    TRAIT_REGISTRY    = registryKey("trait");
	public static final ResourceKey<Registry<Atom>>     ATOM_REGISTRY     = registryKey("atom");
	public static final ResourceKey<Registry<Compound>> COMPOUND_REGISTRY = registryKey("compound");
	public static final ResourceKey<Registry<Material>> MATERIAL_REGISTRY = registryKey("material");

	private GenFormula () {
	}

	public static Identifier id (String path) {
		return Identifier.fromNamespaceAndPath(NAMESPACE, path);
	}

	public static ResourceKey<Category> categoryKey (String path) {
		return ResourceKey.create(CATEGORY_REGISTRY, id(path));
	}

	public static ResourceKey<Trait> traitKey (String path) {
		return ResourceKey.create(TRAIT_REGISTRY, id(path));
	}

	public static ResourceKey<Atom> atomKey (String path) {
		return ResourceKey.create(ATOM_REGISTRY, id(path));
	}

	public static ResourceKey<Compound> compoundKey (String path) {
		return ResourceKey.create(COMPOUND_REGISTRY, id(path));
	}

	public static ResourceKey<Material> materialKey (String path) {
		return ResourceKey.create(MATERIAL_REGISTRY, id(path));
	}

	public static Optional<Material> material (RegistryAccess access, ResourceKey<Material> key) {
		return access.lookup(MATERIAL_REGISTRY).flatMap(registry -> registry.getOptional(key));
	}

	public static Optional<Registry<Material>> materials (RegistryAccess access) {
		return access.lookup(MATERIAL_REGISTRY);
	}

	private static <T> ResourceKey<Registry<T>> registryKey (String path) {
		return ResourceKey.createRegistryKey(id(path));
	}
}
