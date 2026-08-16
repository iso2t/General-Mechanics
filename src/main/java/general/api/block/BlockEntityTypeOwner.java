package general.api.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Allows the block entity registry to bind a block to its registered type.
 */
public interface BlockEntityTypeOwner<T extends BlockEntity> {

	void setBlockEntity (Class<T> blockEntityClass, BlockEntityType<T> blockEntityType);

}
