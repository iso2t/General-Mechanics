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

import java.util.function.Consumer;

@Getter
public class ColoredPlasticBlock extends BaseBlock implements IBlockTooltipProvider {

	private final PlasticTypeBlock parentPlastic;
	private final DyeColor         color;

	public ColoredPlasticBlock(PlasticTypeBlock parentPlastic, DyeColor color, Properties properties) {
		super(properties);
		this.parentPlastic = parentPlastic;
		this.color = color;
		parentPlastic.addColoredVariant(this);
	}

	@Override
	public void appendHoverText (@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
		builder.accept(Component.literal("§o" + parentPlastic.getPlasticType().getAbbreviation()));
		builder.accept(Component.literal(String.format("§e" + parentPlastic.getPlasticType().getFormula())));
		super.appendHoverText(stack, context, toolTip, builder, tooltipFlag);
	}

	public PlasticType getPlasticType () {
		return parentPlastic.getPlasticType();
	}

	public static int getColor (BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
		Block block = state.getBlock();

		if (block instanceof ColoredPlasticBlock coloredBlock) {
			return coloredBlock.getColor().getTextureDiffuseColor();
		}
		return -1;
	}

	public static int getColorForItemStack (ItemStack stack, int index) {
		Item item = stack.getItem();

		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ColoredPlasticBlock coloredBlock) {
			return coloredBlock.getColor().getTextureDiffuseColor();
		}

		return -1;
	}
}
