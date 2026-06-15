package general.api.item.plastic;

import general.api.formula.tooltip.FormulaTooltip;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

@Getter
public class PlasticItem extends Item {

	@Getter
	private final PlasticTypeItem parent;

	@Getter
	private final DyeColor color;

	public PlasticItem (PlasticTypeItem parent, DyeColor color, Properties properties) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(parent.getPlasticType().getMaterial(), parent.getPlasticType().getAbbreviation())));
		this.parent = parent;
		this.color = color;
		parent.addColoredVariant(this);
	}

}
