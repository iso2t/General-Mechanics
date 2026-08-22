package general.api.screens.screen.widget;

import general.api.resources.Resource;
import net.minecraft.resources.Identifier;

/**
 * Marker widget for a recipe viewer's externally registered clickable area.
 * This API widget intentionally has no dependency on a particular viewer mod.
 */
public final class WidgetRecipeViewerButton extends WidgetTextureButton {

	public static final int DEFAULT_RENDER_SIZE = 16;
	public static final int TEXTURE_SIZE        = 16;

	private static final Identifier TEXTURE = Resource.getMainMod("textures/gui/elements/info.png");

	public WidgetRecipeViewerButton () {
		this(DEFAULT_RENDER_SIZE, DEFAULT_RENDER_SIZE);
	}

	public WidgetRecipeViewerButton (int renderWidth, int renderHeight) {
		super(renderWidth, renderHeight, TEXTURE_SIZE, TEXTURE_SIZE, () -> TEXTURE, null, null);
	}
}
