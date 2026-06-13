package general.mechanics.api.item.plastic;

import general.mechanics.api.item.base.BaseItem;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
public class ColoredPlasticItem extends BaseItem {

    @Getter
    private final PlasticTypeItem parentPlastic;

    @Getter
    private final DyeColor color;

    public ColoredPlasticItem(PlasticTypeItem parentPlastic, DyeColor color) {
        super(parentPlastic.getProperties());
        this.parentPlastic = parentPlastic;
        this.color = color;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.literal("§o" + getParentPlastic().getPlasticType().getAbbreviation()));
        tooltipComponents.accept(Component.literal(String.format("§e" + getParentPlastic().getPlasticType().getFormula())));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }

	public static int getColor (ItemStack stack, int index) {
        Item item = stack.getItem();

        if (item instanceof ColoredPlasticItem coloredItem) {
            return coloredItem.getColor().getTextureDiffuseColor();
        }
        return -1;
    }
}
