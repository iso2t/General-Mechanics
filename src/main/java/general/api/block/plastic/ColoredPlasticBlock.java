package general.api.block.plastic;

import general.api.block.BaseBlock;
import general.api.formula.tooltip.FormulaTooltip;
import general.api.item.ITooltipProvider;
import general.api.item.plastic.PlasticType;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Getter
public class ColoredPlasticBlock extends BaseBlock implements ITooltipProvider {

	private final PlasticTypeBlock parentPlastic;
	private final DyeColor         color;

	public ColoredPlasticBlock (PlasticTypeBlock parentPlastic, DyeColor color, Properties properties) {
		super(properties);
		this.parentPlastic = parentPlastic;
		this.color = color;
		parentPlastic.addColoredVariant(this);
	}

	@Override
	public void addTooltipComponents (DataComponentMap.Builder builder) {
		PlasticType type = getPlasticType();
		builder.set(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(type.getMaterial(), type.getAbbreviation()));
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
