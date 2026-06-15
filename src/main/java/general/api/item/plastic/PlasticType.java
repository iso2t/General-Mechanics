package general.api.item.plastic;

import general.api.formula.core.Material;
import general.mechanics.formula.GMMaterials;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;

/**
 * The plastic types. Each holds a reference to its registered {@link Material} — the formula,
 * mass, traits, etc. live in the Formula API registry (see {@link GMMaterials}) rather than being
 * predefined here, so tooltips can be driven entirely from the material.
 */
@Getter
public enum PlasticType {
	POLYETHYLENE("Polyethylene", "PE", 0xFFD9E0E0, GMMaterials.POLYETHYLENE),
	POLYPROPYLENE("Polypropylene", "PP", 0xFFF0EAD6, GMMaterials.POLYPROPYLENE),
	POLYSTYRENE("Polystyrene", "PS", 0x80E0E8F0, GMMaterials.POLYSTYRENE),
	POLYVINYL_CHLORIDE("Polyvinyl Chloride", "PVC", 0xFFE0E0E5, GMMaterials.POLYVINYL_CHLORIDE),
	POLYETHYLENE_TEREPHTHALATE("Polyethylene Terephthalate", "PET", 0x80E3ECF5, GMMaterials.POLYETHYLENE_TEREPHTHALATE),
	ACRYLONITRILE_BUTADIENE_STYRENE("Acrylonitrile Butadiene Styrene", "ABS", 0xFFF2E6D5, GMMaterials.ACRYLONITRILE_BUTADIENE_STYRENE),
	POLYCARBONATE("Polycarbonate", "PC", 0x80E0E8F0, GMMaterials.POLYCARBONATE),
	NYLON("Nylon", "PA", 0xFFE6DAB8, GMMaterials.NYLON),
	POLYURETHANE("Polyurethane", "PU", 0xFFF5E8C8, GMMaterials.POLYURETHANE),
	POLYTETRAFLUOROETHYLENE("Polytetrafluoroethylene", "PTFE", 0xFFE8EBF0, GMMaterials.POLYTETRAFLUOROETHYLENE),
	POLYETHERETHERKETONE("Polyetheretherketone", "PEEK", 0xFF9C8468, GMMaterials.POLYETHERETHERKETONE);

	private final String              displayName;
	private final String              abbreviation;
	private final int                 defaultColor;
	private final ResourceKey<Material> material;

	PlasticType(String displayName, String abbreviation, int defaultColor, ResourceKey<Material> material) {
		this.displayName = displayName;
		this.abbreviation = abbreviation;
		this.defaultColor = defaultColor;
		this.material = material;
	}

	/**
	 * Get all 16 dye colors for plastic coloring
	 */
	public static DyeColor[] getAllColors () {
		return DyeColor.values();
	}
}
