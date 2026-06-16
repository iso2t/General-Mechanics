package general.api.item.materials;

import general.api.formula.core.Material;
import net.minecraft.resources.ResourceKey;

/**
 * Implemented by items that belong to a registered {@link Material} (ingots and their element forms).
 * Exposes the material key so clients can resolve the material's data — e.g.
 * {@link general.mechanics.client.color.MaterialTintSource} reads {@code Material.color()} to tint the item.
 */
public interface IMaterialItem {

	ResourceKey<Material> getMaterial ();
}
