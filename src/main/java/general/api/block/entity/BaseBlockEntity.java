package general.api.block.entity;

import general.api.transfer.item.ItemResourceDrops;
import general.api.transfer.item.ItemResourceProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Common block-entity lifecycle integration for General API owners.
 *
 * <p>When an instance also implements {@link ItemResourceProvider}, its
 * unrestricted item handler is drained automatically when Minecraft removes the
 * block entity. Property-only state transitions keep the same block entity and
 * do not invoke this removal path, so states such as lit/unlit are safe.</p>
 *
 * <p>Minecraft already handles {@link Container} implementations in
 * {@link BlockEntity#preRemoveSideEffects(BlockPos, BlockState)}. Such block
 * entities are deliberately excluded from the resource-provider path to prevent
 * the same backing inventory from being dropped twice.</p>
 */
public abstract class BaseBlockEntity extends BlockEntity {

	protected BaseBlockEntity (BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void preRemoveSideEffects (BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		if (level instanceof ServerLevel serverLevel && !(this instanceof Container) && this instanceof ItemResourceProvider provider) {
			ItemResourceDrops.dropContents(serverLevel, pos, provider.getItemHandler());
		}
	}
}
