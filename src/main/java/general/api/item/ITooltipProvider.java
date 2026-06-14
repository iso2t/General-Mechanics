package general.api.item;

import net.minecraft.core.component.DataComponentMap;

public interface ITooltipProvider {

	void addTooltipComponents (DataComponentMap.Builder builder);

}
