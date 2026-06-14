package general.api.block.plastic;

import general.api.block.BaseBlock;
import general.api.item.IBlockTooltipProvider;
import general.api.item.plastic.PlasticType;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class PlasticTypeBlock extends BaseBlock implements IBlockTooltipProvider {

	private final PlasticType                          plasticType;
	private final Map<DyeColor, ColoredPlasticBlock>   coloredVariants = new EnumMap<>(DyeColor.class);
	private final Properties                           properties;

	public PlasticTypeBlock(Properties properties, PlasticType plasticType) {
		super(properties/*.sound(CoreSounds.PLASTIC_BLOCK)*/);
		this.properties = properties;
		this.plasticType = plasticType;
	}

    @Override
    public void appendHoverText (@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
        builder.accept(Component.literal("§o" + plasticType.getAbbreviation()));
        builder.accept(Component.literal(String.format("§e" + plasticType.getFormula())));
        super.appendHoverText(stack, context, toolTip, builder, tooltipFlag);
    }

	void addColoredVariant (ColoredPlasticBlock variant) {
		coloredVariants.put(variant.getColor(), variant);
	}

	public ColoredPlasticBlock getColoredVariant(DyeColor color) {
		return coloredVariants.get(color);
	}

	public static int getColor (BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
		Block block = state.getBlock();

		if (block instanceof PlasticTypeBlock plasticTypeBlock) {
			int defaultColor = plasticTypeBlock.getPlasticType().getDefaultColor();
			return defaultColor == -1 ? 0xFFFFFFFF : defaultColor;
		}
		return -1;
	}

	public static int getColorForItemStack (ItemStack stack, int index) {
		Item item = stack.getItem();

		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof PlasticTypeBlock coloredBlock) {
			return coloredBlock.getPlasticType().getDefaultColor();
		}

		return -1;
	}
}
