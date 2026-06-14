package general.api.block.plastic;

import general.api.block.BaseBlock;
import general.api.client.tooltip.FormulaTooltip;
import general.api.item.ITooltipProvider;
import general.mechanics.registries.GenComponents;
import general.mechanics.registries.GenSounds;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class PlasticTypeBlock extends BaseBlock implements ITooltipProvider {

	private final general.api.item.plastic.PlasticType plasticType;
	private final Map<DyeColor, ColoredPlasticBlock>   coloredVariants = new EnumMap<>(DyeColor.class);
	private final Properties                           properties;

	public PlasticTypeBlock (Properties properties, general.api.item.plastic.PlasticType plasticType) {
		super(properties.sound(GenSounds.PLASTIC_BLOCK));
		this.properties = properties;
		this.plasticType = plasticType;
	}

	@Override
	public void addTooltipComponents (DataComponentMap.Builder builder) {
		builder.set(GenComponents.FORMULA_TOOLTIP.get(), new FormulaTooltip(plasticType.getAbbreviation(), plasticType.getFormula()));
	}

	void addColoredVariant (ColoredPlasticBlock variant) {
		coloredVariants.put(variant.getColor(), variant);
	}

	public ColoredPlasticBlock getColoredVariant (DyeColor color) {
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
