package general.api.item.plastic;

import lombok.Getter;
import net.minecraft.world.item.DyeColor;

@Getter
public enum PlasticType {
	POLYETHYLENE("Polyethylene", "PE", "C₂H₄", 0xFFD9E0E0),
	POLYPROPYLENE("Polypropylene", "PP", "C₃H₆", 0xFFF0EAD6),
	POLYSTYRENE("Polystyrene", "PS", "C₈H₈", 0x80E0E8F0),
	POLYVINYL_CHLORIDE("Polyvinyl Chloride", "PVC", "C₂H₃Cl", 0xFFE0E0E5),
	POLYETHYLENE_TEREPHTHALATE("Polyethylene Terephthalate", "PET", "C₁₀H₈O₄", 0x80E3ECF5),
	ACRYLONITRILE_BUTADIENE_STYRENE("Acrylonitrile Butadiene Styrene", "ABS", "C₈H₈·C₄H₆·C₃H₃N", 0xFFF2E6D5),
	POLYCARBONATE("Polycarbonate", "PC", "C₁₆H₁₄O₃", 0x80E0E8F0),
	NYLON("Nylon", "PA", "C₆H₁₁NO", 0xFFE6DAB8),
	POLYURETHANE("Polyurethane", "PU", "C₃H₈N₂O", 0xFFF5E8C8),
	POLYTETRAFLUOROETHYLENE("Polytetrafluoroethylene", "PTFE", "C₂F₄", 0xFFE8EBF0),
	POLYETHERETHERKETONE("Polyetheretherketone", "PEEK", "C₁₉H₁₂O₃", 0xFF9C8468);

	private final String displayName;
	private final String abbreviation;
	private final String formula;
	private final int defaultColor;

	PlasticType(String displayName, String abbreviation, String formula, int defaultColor) {
		this.displayName = displayName;
		this.abbreviation = abbreviation;
		this.formula = formula;
		this.defaultColor = defaultColor;
	}

	/**
	 * Get all 16 dye colors for plastic coloring
	 */
	public static DyeColor[] getAllColors () {
		return DyeColor.values();
	}
}
