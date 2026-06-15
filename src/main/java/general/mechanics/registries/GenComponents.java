package general.mechanics.registries;

import general.api.formula.tooltip.FormulaTooltip;
import general.api.mod.GenAPI;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GenComponents {

	public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GenAPI.getModId());

	public static final Supplier<DataComponentType<FormulaTooltip>> FORMULA_TOOLTIP = REGISTRY.registerComponentType("formula_tooltip", builder -> builder.persistent(FormulaTooltip.CODEC));

}
