package general.mechanics.api.block.base;

import general.mechanics.api.item.IBlockTooltipProvider;
import general.mechanics.api.item.element.ElementType;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OreBlock extends DropExperienceBlock implements IBlockTooltipProvider {

    @Getter
    private final ElementType type;

    public OreBlock(IntProvider xpRange, ElementType type, Properties properties) {
        super(xpRange, properties);
        this.type = type;
    }

    public OreBlock(IntProvider xpRange, ElementType type) {
        this(xpRange, type, Blocks.IRON_ORE.properties());
    }

    public OreBlock(ElementType type) {
        this(UniformInt.of(0, 2), type);
    }

    @Override
    public void appendHoverText (@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
        builder.accept(Component.literal(String.format("§e" + type.getSymbol())));
    }

    public static int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
        Block block = state.getBlock();

        if (block instanceof OreBlock ore && tintIndex == 1) {
            return ore.getType().getTintColor();
        }
        return -1;
    }

    public static int getColorForItemStack(ItemStack stack, int index) {
        Item item = stack.getItem();

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof OreBlock ore && index == 1) {
            return ore.getType().getTintColor();
        }

        return -1;
    }

}
