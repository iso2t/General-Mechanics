package general.mechanics.api.item.base;

import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.item.IBlockTooltipProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BaseBlockItem extends BlockItem {

    private final BaseBlock blockType;

	private final IBlockTooltipProvider tooltipProvider;

    public BaseBlockItem (Block id, Properties properties, @Nullable IBlockTooltipProvider tooltipProvider) {
        super(id, properties);
        this.blockType = (BaseBlock) id;
		this.tooltipProvider = tooltipProvider;
    }

	public BaseBlockItem (Block id, Properties properties) {
        this(id, properties, null);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void appendHoverText (@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
        this.addCheckedInformation(stack, context, toolTip, builder, tooltipFlag);
    }

    @OnlyIn(Dist.CLIENT)
    public final void addCheckedInformation (@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
        this.blockType.appendHoverText(stack, context, toolTip, builder, tooltipFlag);
    }

	@Override
	public boolean supportsEnchantment (ItemStack stack, Holder<Enchantment> enchantment) {
		return false;
	}

}
