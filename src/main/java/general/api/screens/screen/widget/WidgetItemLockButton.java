package general.api.screens.screen.widget;

import general.api.resources.Resource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Shared machine-input lock button. Its texture and tooltip follow synchronized
 * menu state while its action remains supplied by the owning screen.
 */
public final class WidgetItemLockButton extends WidgetTextureButton {

	public static final int DEFAULT_RENDER_SIZE = 12;
	public static final int TEXTURE_SIZE        = 16;

	private static final Identifier LOCKED   = Resource.getMainMod("textures/gui/elements/locked.png");
	private static final Identifier UNLOCKED = Resource.getMainMod("textures/gui/elements/unlocked.png");

	public WidgetItemLockButton (BooleanSupplier locked, ClickAction clickAction) {
		this(DEFAULT_RENDER_SIZE, DEFAULT_RENDER_SIZE, locked, clickAction);
	}

	public WidgetItemLockButton (int renderWidth, int renderHeight, BooleanSupplier locked, ClickAction clickAction) {
		super(renderWidth, renderHeight, TEXTURE_SIZE, TEXTURE_SIZE, () -> locked.getAsBoolean() ? LOCKED : UNLOCKED, () -> tooltip(locked.getAsBoolean()), Objects.requireNonNull(clickAction, "clickAction"));
		Objects.requireNonNull(locked, "locked");
	}

	private static Component tooltip (boolean locked) {
		String key = locked ? "gui.generalmechanics.machine.item_lock.locked" : "gui.generalmechanics.machine.item_lock.unlocked";
		String fallback = locked ? "Input Lock: Locked" : "Input Lock: Unlocked";
		return Component.translatableWithFallback(key, fallback);
	}
}
