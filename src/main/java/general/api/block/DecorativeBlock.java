package general.api.block;

import net.minecraft.world.level.block.Blocks;

public class DecorativeBlock extends BaseBlock {

	public DecorativeBlock (Properties properties) {
		super(properties);
	}

	public DecorativeBlock () {
		this(Blocks.IRON_BLOCK.properties());
	}

}
