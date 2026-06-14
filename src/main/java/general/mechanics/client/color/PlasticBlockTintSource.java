package general.mechanics.client.color;

import general.api.block.plastic.ColoredPlasticBlock;
import general.api.block.plastic.PlasticTypeBlock;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * In-world tint source for plastic blocks. The color is read from the block itself:
 * {@link ColoredPlasticBlock#getColor()} for color variants and the parent
 * {@link general.api.item.plastic.PlasticType#getDefaultColor()} for the base type block.
 */
public final class PlasticBlockTintSource implements BlockTintSource {

	public static final PlasticBlockTintSource INSTANCE = new PlasticBlockTintSource();

	private PlasticBlockTintSource () {}

	@Override
	public int color (BlockState state) {
		Block block = state.getBlock();
		if (block instanceof ColoredPlasticBlock colored) {
			return colored.getColor().getTextureDiffuseColor();
		}
		if (block instanceof PlasticTypeBlock type) {
			return type.getPlasticType().getDefaultColor();
		}
		return -1;
	}

}
