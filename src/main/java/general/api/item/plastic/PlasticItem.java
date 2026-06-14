package general.api.item.plastic;

import general.api.client.tooltip.FormulaTooltip;
import general.api.item.ITooltipProvider;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

@Getter
public class PlasticItem extends Item implements ITooltipProvider {

	@Getter
	private final PlasticTypeItem parent;

	@Getter
	private final DyeColor color;

	public PlasticItem (PlasticTypeItem parent, DyeColor color, Properties properties) {
		super(properties);
		this.parent = parent;
		this.color = color;
		parent.addColoredVariant(this);
	}

	@Override
	public void addTooltipComponents (DataComponentMap.Builder builder) {
		PlasticType type = parent.getPlasticType();
		builder.set(GenComponents.FORMULA_TOOLTIP.get(), new FormulaTooltip(type.getAbbreviation(), type.getFormula()));
	}

}
