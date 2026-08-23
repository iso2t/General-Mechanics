package general.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Typed base for blocks whose deferred block-entity type is bound after block
 * registration.
 */
public abstract class BaseEntityBlock<T extends BlockEntity> extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<T> {

	private final Class<T>              blockEntityClass;
	private @Nullable BlockEntityType<T> blockEntityType;

	protected BaseEntityBlock (Properties properties, Class<T> blockEntityClass) {
		super(properties);
		this.blockEntityClass = Objects.requireNonNull(blockEntityClass, "blockEntityClass");
	}

	@Override
	public final void setBlockEntity (Class<T> blockEntityClass, BlockEntityType<T> blockEntityType) {
		if (this.blockEntityClass != Objects.requireNonNull(blockEntityClass, "blockEntityClass")) {
			throw new IllegalArgumentException("Cannot bind " + blockEntityClass.getName() + " to block expecting " + this.blockEntityClass.getName());
		}
		BlockEntityType<T> checkedType = Objects.requireNonNull(blockEntityType, "blockEntityType");
		if (this.blockEntityType != null && this.blockEntityType != checkedType) {
			throw new IllegalStateException("Block entity type is already bound for " + this.blockEntityClass.getName());
		}
		this.blockEntityType = checkedType;
	}

	@Override
	public final @Nullable T newBlockEntity (@NonNull BlockPos pos, @NonNull BlockState state) {
		return requireBlockEntityType().create(pos, state);
	}

	public final Class<T> getBlockEntityClass () {
		return blockEntityClass;
	}

	public final @Nullable BlockEntityType<T> getBlockEntityType () {
		return blockEntityType;
	}

	public final BlockEntityType<T> requireBlockEntityType () {
		if (blockEntityType == null) throw new IllegalStateException(blockEntityClass.getSimpleName() + " block entity type has not been bound yet");
		return blockEntityType;
	}

	public final @Nullable T getBlockEntity (BlockGetter level, BlockPos pos) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(pos, "pos");
		BlockEntity blockEntity = level.getBlockEntity(pos);
		return blockEntityType != null && blockEntity != null && blockEntity.getType() == blockEntityType ? blockEntityClass.cast(blockEntity) : null;
	}
}
