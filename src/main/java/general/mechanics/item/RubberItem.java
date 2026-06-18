package general.mechanics.item;

import general.api.formula.core.Material;
import general.api.formula.tooltip.FormulaTooltip;
import general.api.item.materials.IMaterialItem;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.EnumMap;
import java.util.Map;

public class RubberItem extends Item implements IMaterialItem {

	@Getter
	private final Map<DyeColor, RubberColoredItem> coloredVariants = new EnumMap<>(DyeColor.class);

	@Getter
	private final Properties properties;

	@Getter
	private final ResourceKey<Material> material;

	public RubberItem (Properties properties, ResourceKey<Material> material) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(material)));
		this.properties = properties;
		this.material = material;
	}

	public void addColoredVariant (RubberColoredItem variant) {
		coloredVariants.put(variant.getColor(), variant);
	}

	public RubberColoredItem getColoredVariant (DyeColor color) {
		return coloredVariants.get(color);
	}

}
