package general.api.item.plastic;

import general.api.client.tooltip.FormulaTooltip;
import general.api.item.ITooltipProvider;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class PlasticTypeItem extends Item implements ITooltipProvider {

	@Getter
	private final PlasticType plasticType;

	@Getter
	private final Map<DyeColor, PlasticItem> coloredVariants = new EnumMap<>(DyeColor.class);

	@Getter
	private final Properties properties;

	public PlasticTypeItem(Properties properties, PlasticType plasticType) {
		super(properties);
		this.properties = properties;
		this.plasticType = plasticType;
	}

	@Override
	public void addTooltipComponents (DataComponentMap.Builder builder) {
		builder.set(GenComponents.FORMULA_TOOLTIP.get(), new FormulaTooltip(plasticType.getAbbreviation(), plasticType.getFormula()));
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
