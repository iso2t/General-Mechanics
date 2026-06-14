package general.api.item.plastic;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class PlasticTypeItem extends Item {

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
	public void appendHoverText (@NotNull ItemStack stack, @NotNull TooltipContext context, @NonNull TooltipDisplay display, Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
		builder.accept(Component.literal("§o" + plasticType.getAbbreviation()));
		builder.accept(Component.literal(String.format("§e" + plasticType.getFormula())));
		super.appendHoverText(stack, context, display, builder, tooltipFlag);
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
