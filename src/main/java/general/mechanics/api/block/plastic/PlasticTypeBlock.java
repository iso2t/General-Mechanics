package general.mechanics.api.block.plastic;

import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.item.plastic.PlasticType;
import general.mechanics.registries.CoreSounds;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PlasticTypeBlock extends BaseBlock {

    private final PlasticType plasticType;
    private final List<ColoredPlasticBlock> coloredVariants;
    private final Properties properties;

    public PlasticTypeBlock(Properties properties, PlasticType plasticType) {
        super(properties.sound(CoreSounds.PLASTIC_BLOCK));
        this.properties = properties;
        this.plasticType = plasticType;
        this.coloredVariants = new ArrayList<>();

        for (DyeColor color : PlasticType.getAllColors()) {
            coloredVariants.add(new ColoredPlasticBlock(this, color));
        }
    }

	// TODO: Not Supported anymore
    /*@Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§o" + plasticType.getAbbreviation()));
        tooltipComponents.add(Component.literal(String.format("§e" + plasticType.getFormula())));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }*/

    public ColoredPlasticBlock getColoredVariant(DyeColor color) {
        return coloredVariants.stream()
                .filter(variant -> variant.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    public static int getColor (BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
        Block block = state.getBlock();

        if (block instanceof PlasticTypeBlock plasticTypeBlock) {
            int defaultColor = plasticTypeBlock.getPlasticType().getDefaultColor();
            return defaultColor == -1 ? 0xFFFFFFFF : defaultColor;
        }
        return -1;
    }

    public static int getColorForItemStack(ItemStack stack, int index) {
        Item item = stack.getItem();

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof PlasticTypeBlock coloredBlock) {
            return coloredBlock.getPlasticType().getDefaultColor();
        }

        return -1;
    }
}
