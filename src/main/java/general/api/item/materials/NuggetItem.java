package general.api.item.materials;

import general.api.formula.core.Material;
import general.api.formula.tooltip.FormulaTooltip;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class NuggetItem extends Item implements IMaterialItem {

	@Getter
	private final IngotItem parent;

	public NuggetItem (IngotItem parent, Properties properties) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(parent.getMaterial())));
		this.parent = parent;
		parent.setNuggetItem(this);
	}

	@Override
	public ResourceKey<Material> getMaterial () {
		return parent.getMaterial();
	}

}
