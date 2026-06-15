package general.mechanics.client;

import general.api.formula.tooltip.FormulaTooltip;
import general.mechanics.registries.GenComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Renders custom {@link net.minecraft.world.item.component.TooltipProvider} data components.
 * Vanilla only auto-renders a fixed set of built-in components in {@code ItemStack.addDetailsToTooltip},
 * so custom components such as {@link FormulaTooltip} must be emitted from {@link ItemTooltipEvent}.
 */
public class ClientTooltips {

	public static void onItemTooltip (ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();

		FormulaTooltip formula = stack.get(GenComponents.FORMULA_TOOLTIP.get());
		if (formula != null) {
			formula.addToTooltip(event.getContext(), event.getToolTip()::add, event.getFlags(), stack.getComponents());
		}
	}

}
