package general.api.definitions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public record BlockEntityDefinition<T extends BlockEntity>(Class<T> blockEntityClass, DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder) implements Supplier<BlockEntityType<T>> {

	@Override
	public BlockEntityType<T> get () {
		return holder.get();
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public T getBlockEntity (BlockGetter level, BlockPos pos) {
		BlockEntity block = level.getBlockEntity(pos);
		return (T) (block != null && block.getType() == holder.get() ? block : null);
	}

}
