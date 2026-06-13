package general.mechanics.api.block.plastic;

import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.item.plastic.PlasticType;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class ColoredPlasticBlock extends BaseBlock {

    private final PlasticTypeBlock parentPlastic;
    private final DyeColor color;

    public ColoredPlasticBlock(PlasticTypeBlock parentPlastic, DyeColor color) {
        super(parentPlastic.getProperties());
        this.parentPlastic = parentPlastic;
        this.color = color;
    }

	// TODO: Not Supported anymore
    /*@Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§o" + getParentPlastic().getPlasticType().getAbbreviation()));
        tooltipComponents.add(Component.literal(String.format("§e" + getParentPlastic().getPlasticType().getFormula())));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }*/

    public PlasticType getPlasticType() {
        return parentPlastic.getPlasticType();
    }

    public static int getColor (BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
        Block block = state.getBlock();

        if (block instanceof ColoredPlasticBlock coloredBlock) {
            return coloredBlock.getColor().getTextureDiffuseColor();
        }
        return -1;
    }

    public static int getColorForItemStack(ItemStack stack, int index) {
        Item item = stack.getItem();

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ColoredPlasticBlock coloredBlock) {
            return coloredBlock.getColor().getTextureDiffuseColor();
        }

        return -1;
    }
}
