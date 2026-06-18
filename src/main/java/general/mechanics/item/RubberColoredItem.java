package general.mechanics.item;

import general.api.formula.core.Material;
import general.api.formula.tooltip.FormulaTooltip;
import general.api.item.materials.IMaterialItem;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class RubberColoredItem extends Item implements IMaterialItem {

	@Getter
	private final RubberItem parent;

	@Getter
	private final DyeColor color;

	public RubberColoredItem (RubberItem parent, DyeColor color, Properties properties) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(parent.getMaterial())));
		this.parent = parent;
		this.color = color;
		parent.addColoredVariant(this);
	}

	@Override
	public ResourceKey<Material> getMaterial () {
		return parent.getMaterial();
	}
}
