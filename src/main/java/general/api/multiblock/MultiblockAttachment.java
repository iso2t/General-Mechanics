package general.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by a block entity that can be attached to one formed multiblock.
 *
 * <p>The attachment owns only a persistent controller position. Discovery and
 * validation are handled by {@link MultiblockHandler}; keeping the binding as a
 * position avoids retaining block entities or forcing controller chunks to load.</p>
 */
public interface MultiblockAttachment {

	String ATTACHMENT_TAG = "multiblock_attachment";

	MultiblockAttachmentBinding getMultiblockBinding ();

	/**
	 * Creates the binding owned by this attachment. Implementations should use this
	 * once when initializing the value returned by {@link #getMultiblockBinding()}.
	 *
	 * <p>Every runtime binding change marks the block entity dirty, invalidates its
	 * block capabilities, notifies its neighbors, and finally calls
	 * {@link #onMultiblockBindingChanged(BlockPos, BlockPos)}.</p>
	 */
	default MultiblockAttachmentBinding createAttachmentBinding () {
		if (!(this instanceof BlockEntity blockEntity)) {
			throw new IllegalStateException("A multiblock attachment must be implemented by a block entity.");
		}

		return new MultiblockAttachmentBinding((previous, current) -> {
			blockEntity.setChanged();
			Level level = blockEntity.getLevel();
			if (level != null) {
				BlockPos position = blockEntity.getBlockPos();
				level.invalidateCapabilities(position);
				level.updateNeighborsAt(position, blockEntity.getBlockState().getBlock());
			}
			onMultiblockBindingChanged(previous, current);
		});
	}

	/**
	 * Optional attachment-specific behavior after the standard binding-change updates.
	 */
	default void onMultiblockBindingChanged (@Nullable BlockPos previous, @Nullable BlockPos current) {
	}

	default @Nullable BlockPos getBoundController () {
		return getMultiblockBinding().getController();
	}

	default boolean isBound () {
		return getMultiblockBinding().isBound();
	}

	default boolean isBoundTo (BlockPos controllerPos) {
		return getMultiblockBinding().isBoundTo(controllerPos);
	}

	/**
	 * Binds this attachment to a controller position.
	 *
	 * @return {@code true} when the binding changed
	 */
	default boolean bindController (BlockPos controllerPos) {
		return getMultiblockBinding().bind(controllerPos);
	}

	/**
	 * Clears the current controller binding.
	 *
	 * @return {@code true} when a binding was cleared
	 */
	default boolean unbindController () {
		return getMultiblockBinding().unbind();
	}

	/**
	 * Writes the attachment data under the API-owned attachment tag. Implementing
	 * block entities should call this from their {@code saveAdditional} method.
	 */
	default void saveMultiblockAttachment (ValueOutput output) {
		getMultiblockBinding().save(output.child(ATTACHMENT_TAG));
	}

	/**
	 * Reads attachment data written by {@link #saveMultiblockAttachment(ValueOutput)}.
	 * Implementing block entities should call this from their {@code loadAdditional}
	 * method.
	 */
	default void loadMultiblockAttachment (ValueInput input) {
		getMultiblockBinding().load(input.childOrEmpty(ATTACHMENT_TAG));
	}
}
