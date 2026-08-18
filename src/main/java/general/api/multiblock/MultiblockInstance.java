package general.api.multiblock;

import general.api.definitions.MultiblockDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Map;

public record MultiblockInstance(MultiblockDefinition definition, BlockPos anchor, Direction facing, Map<BlockPos, MultiblockElement> blocks) {

}
