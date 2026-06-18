package general.api.formula.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import general.api.formula.GenFormula;
import general.api.formula.core.Formula;
import general.api.formula.core.Material;
import general.api.formula.core.Trait;
import general.api.formula.render.TextColors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * A data component carrying formula tooltip information. Two modes:
 *
 * <ul>
 *     <li><b>Material</b> — references a registered {@link Material} by key; renders a multi-line,
 *         block (formula, material, category, mass, traits) resolved from the synced
 *         material registry.</li>
 *     <li><b>Text</b> — a pre-rendered abbreviation + formula string (used by simple cases such as the
 *         plastic items); renders two styled lines.</li>
 * </ul>
 *
 * <p>Custom {@link TooltipProvider} components are not auto-rendered by vanilla; the lines are emitted
 * by the mod's {@code ItemTooltipEvent} handler, which calls {@link #addToTooltip}.
 */
public record FormulaTooltip(Optional<ResourceKey<Material>> material, Optional<String> abbreviation, Optional<String> formula) implements TooltipProvider {

	public static final Codec<FormulaTooltip> CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceKey.codec(GenFormula.MATERIAL_REGISTRY).optionalFieldOf("material").forGetter(FormulaTooltip::material), Codec.STRING.optionalFieldOf("abbreviation").forGetter(FormulaTooltip::abbreviation), Codec.STRING.optionalFieldOf("formula").forGetter(FormulaTooltip::formula)).apply(instance, FormulaTooltip::new));

	/**
	 * Full material tooltip referencing a registered {@link Material}.
	 */
	public static FormulaTooltip ofMaterial (ResourceKey<Material> material) {
		return new FormulaTooltip(Optional.of(material), Optional.empty(), Optional.empty());
	}

	/**
	 * Im lazy and needed to easily register an abbreviation for a material.
	 */
	public static FormulaTooltip ofMaterial (ResourceKey<Material> material, String abbreviation) {
		return new FormulaTooltip(Optional.of(material), Optional.of(abbreviation), Optional.empty());
	}

	/**
	 * Simple two-line tooltip from pre-rendered strings (e.g. plastics).
	 */
	public static FormulaTooltip ofText (String abbreviation, String formula) {
		return new FormulaTooltip(Optional.empty(), Optional.ofNullable(abbreviation), Optional.ofNullable(formula));
	}

	/**
	 * Single-line tooltip from an inline {@link Formula} (rendered unicode).
	 */
	public static FormulaTooltip ofFormula (Formula formula) {
		return new FormulaTooltip(Optional.empty(), Optional.empty(), Optional.of(formula.unicode()));
	}

	@Override
	public void addToTooltip (Item.@NonNull TooltipContext tooltipContext, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag, @NonNull DataComponentGetter dataComponentGetter) {
		HolderLookup.Provider registries = tooltipContext.registries();
		if (material.isPresent() && registries != null) {
			registries.lookup(GenFormula.MATERIAL_REGISTRY).flatMap(lookup -> lookup.get(material.get())).ifPresent(holder -> renderMaterial(holder.value(), consumer, abbreviation.orElse(null)));
			return;
		}
		abbreviation.ifPresent(s -> consumer.accept(Component.literal("§o" + s)));
		formula.ifPresent(s -> consumer.accept(Component.literal("§e" + s)));
	}

	private static void renderMaterial (Material mat, Consumer<Component> consumer, @Nullable String abbreviation) {
		if (!isShiftDown()) {
			if (abbreviation != null) consumer.accept(Component.literal("§o" + abbreviation));
			consumer.accept(Component.literal(TextColors.FORMULA + mat.formula().unicode()));
			consumer.accept(Component.empty());
			consumer.accept(Component.translatable("genapi.hold_shift"));
		} else {
			consumer.accept(Component.translatable("genapi.formulas.tooltip.formula", TextColors.FORMULA + mat.formula().unicode()));
			consumer.accept(Component.translatable("genapi.formulas.tooltip.material", mat.name()));
			consumer.accept(Component.translatable("genapi.formulas.tooltip.category", mat.category().value().name()));

			OptionalDouble mass = mat.mass();
			if (mass.isPresent()) {
				consumer.accept(Component.translatable("genapi.formulas.tooltip.mass", String.format("%.2f", mass.getAsDouble())));
			}

			if (!mat.traits().isEmpty()) {
				StringJoiner joiner = new StringJoiner(", ");
				for (Holder<Trait> trait : mat.traits()) {
					joiner.add(trait.value().name());
				}
				consumer.accept(Component.translatable("genapi.formulats.tooltip.traits", joiner.toString()));
			}
		}
	}

	private static boolean isShiftDown () {
		return Minecraft.getInstance().hasShiftDown();
	}

}
