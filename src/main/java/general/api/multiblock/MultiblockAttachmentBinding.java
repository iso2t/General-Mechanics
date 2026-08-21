package general.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Persistent, level-local reference from a multiblock attachment to its owning
 * controller.
 *
 * <p>Runtime binding changes invoke the supplied listener. Loading saved data does
 * not invoke it, preventing a block entity from being marked dirty while it is
 * being deserialized.</p>
 */
public final class MultiblockAttachmentBinding {

	private static final String BOUND_TAG = "bound";
	private static final String X_TAG     = "x";
	private static final String Y_TAG     = "y";
	private static final String Z_TAG     = "z";

	private final BindingChangeListener changeListener;

	private @Nullable BlockPos controller;

	MultiblockAttachmentBinding (BindingChangeListener changeListener) {
		this.changeListener = Objects.requireNonNull(changeListener, "changeListener");
	}

	public @Nullable BlockPos getController () {
		return controller;
	}

	public boolean isBound () {
		return controller != null;
	}

	public boolean isBoundTo (BlockPos controllerPos) {
		return Objects.requireNonNull(controllerPos, "controllerPos").equals(controller);
	}

	/**
	 * Replaces the current controller position with an immutable copy.
	 *
	 * @return {@code true} when the binding changed
	 */
	public boolean bind (BlockPos controllerPos) {
		BlockPos next = Objects.requireNonNull(controllerPos, "controllerPos").immutable();
		if (next.equals(controller)) return false;

		BlockPos previous = controller;
		controller = next;
		changeListener.onBindingChanged(previous, next);
		return true;
	}

	/**
	 * @return {@code true} when a binding was cleared
	 */
	public boolean unbind () {
		if (controller == null) return false;

		BlockPos previous = controller;
		controller = null;
		changeListener.onBindingChanged(previous, null);
		return true;
	}

	public void save (ValueOutput output) {
		BlockPos controller = this.controller;
		output.putBoolean(BOUND_TAG, controller != null);
		if (controller == null) return;

		output.putInt(X_TAG, controller.getX());
		output.putInt(Y_TAG, controller.getY());
		output.putInt(Z_TAG, controller.getZ());
	}

	public void load (ValueInput input) {
		controller = input.getBooleanOr(BOUND_TAG, false) ? new BlockPos(input.getIntOr(X_TAG, 0), input.getIntOr(Y_TAG, 0), input.getIntOr(Z_TAG, 0)) : null;
	}

	@FunctionalInterface
	interface BindingChangeListener {

		void onBindingChanged (@Nullable BlockPos previous, @Nullable BlockPos current);
	}
}
