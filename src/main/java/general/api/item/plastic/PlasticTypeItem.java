package general.api.item.plastic;

import general.api.formula.core.Material;
import general.api.formula.tooltip.FormulaTooltip;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class PlasticTypeItem extends Item {

	@Getter
	private final PlasticType plasticType;

	@Getter
	private final Map<DyeColor, PlasticItem> coloredVariants = new EnumMap<>(DyeColor.class);

	@Getter
	private final Properties properties;

	@Getter
	private final ResourceKey<Material> material;

	public PlasticTypeItem(Properties properties, PlasticType plasticType, ResourceKey<Material> material) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(material, plasticType.getAbbreviation())));
		this.properties = properties;
		this.plasticType = plasticType;
		this.material = material;
	}

	void addColoredVariant (PlasticItem variant) {
		coloredVariants.put(variant.getColor(), variant);
	}

	public PlasticItem getColoredVariant (DyeColor color) {
		return coloredVariants.get(color);
	}

	public static int getColor(ItemStack stack, int index) {
		Item item = stack.getItem();

		if (item instanceof PlasticTypeItem plasticTypeItem) {
			int defaultColor = plasticTypeItem.getPlasticType().getDefaultColor();
			return defaultColor == -1 ? 0xFFFFFFFF : defaultColor;
		}
		return -1;
	}

}
