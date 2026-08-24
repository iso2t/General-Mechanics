package general.mechanics.client.screens;

import general.api.resources.Resource;
import general.api.screens.renderers.GuiPowerRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import general.api.screens.screen.AbstractScreen;
import general.mechanics.common.menus.MaceratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MaceratorScreen extends AbstractScreen<MaceratorMenu> {

	private static final Identifier TEXTURE = Resource.getMainMod("textures/gui/macerator.png");

	public MaceratorScreen (MaceratorMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		setProgressBarRenderer(new GuiProgressBarRenderer(103, 32, menu::getProgress, menu::getMaxProgress));
		setPowerRenderer(new GuiPowerRenderer(GuiPowerRenderer.RenderLocation.getDefault(), menu::getEnergyStored, menu::getEnergyCapacity));
	}

	@Override
	public Identifier getTexture () {
		return TEXTURE;
	}
}
