package general.api.item.materials;

import general.api.formula.core.Material;
import general.api.formula.tooltip.FormulaTooltip;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class PileItem extends Item implements IMaterialItem {

	@Getter
	private final IngotItem parent;

	public PileItem (IngotItem parent, Properties properties) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(parent.getMaterial())));
		this.parent = parent;
		parent.setPileItem(this);
	}

	@Override
	public ResourceKey<Material> getMaterial () {
		return parent.getMaterial();
	}

}
