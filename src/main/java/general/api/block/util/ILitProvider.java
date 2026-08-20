package general.api.block.util;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface ILitProvider {

	BooleanProperty LIT = BlockStateProperties.LIT;

	Identifier getLitTexture ();

}
