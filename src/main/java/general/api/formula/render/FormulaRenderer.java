package general.api.formula.render;

import general.api.formula.core.Formula;
import net.minecraft.network.chat.Component;

/**
 * Converts a {@link Formula} into a rendered representation. Implementations are stateless and
 * registry-friendly, so mods can supply their own styles.
 */
public interface FormulaRenderer {

	/**
	 * Renders the formula to a plain {@link String}.
	 */
	String renderString (Formula formula);

	/**
	 * Renders the formula to a styled Minecraft {@link Component}. Defaults to wrapping the string.
	 */
	default Component renderComponent (Formula formula) {
		return Component.literal(renderString(formula));
	}
}
