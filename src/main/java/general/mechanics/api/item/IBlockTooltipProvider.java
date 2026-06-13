package general.mechanics.api.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface IBlockTooltipProvider {

	void appendHoverText (@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag);

}
