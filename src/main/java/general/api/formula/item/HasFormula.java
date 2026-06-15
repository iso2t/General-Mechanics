package general.api.formula.item;

import general.api.formula.tooltip.FormulaTooltip;

/**
 * Implemented by custom {@link net.minecraft.world.item.Item}s (or {@link net.minecraft.world.level.block.Block}s)
 * that carry a formula. The returned {@link FormulaTooltip} is attached as a default data component
 * during registration, so the item shows its formula tooltip without bespoke tooltip code.
 *
 * <pre>{@code
 * public class FluxiumItem extends Item implements HasFormula {
 *     public FormulaTooltip getFormula() { return FormulaTooltip.ofMaterial(GMMaterials.FLUXIUM); }
 * }
 * }</pre>
 */
public interface HasFormula {

	FormulaTooltip getFormula ();
}
