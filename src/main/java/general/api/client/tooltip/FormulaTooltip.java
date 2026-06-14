package general.api.client.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record FormulaTooltip(String abbreviation, String formula) implements TooltipProvider {

	public static final Codec<FormulaTooltip> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("abbreviation").forGetter(FormulaTooltip::abbreviation),
			Codec.STRING.fieldOf("formula").forGetter(FormulaTooltip::formula)
	).apply(instance, FormulaTooltip::new));

	@Override
	public void addToTooltip (Item.@NonNull TooltipContext tooltipContext, Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag, @NonNull DataComponentGetter dataComponentGetter) {
		consumer.accept(Component.literal("§o" + abbreviation));
		consumer.accept(Component.literal("§e" + formula));
	}
}
