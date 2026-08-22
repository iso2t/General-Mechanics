package general.mechanics.client.screens;

import general.api.resources.Resource;
import general.api.screens.renderers.GuiPowerRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import general.api.screens.screen.AbstractScreen;
import general.mechanics.common.menus.ElectricFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ElectricFurnaceScreen extends AbstractScreen<ElectricFurnaceMenu> {

	private static final Identifier TEXTURE = Resource.getMainMod("textures/gui/electric_furnace.png");

	private static final int PROGRESS_X = 76;
	private static final int PROGRESS_Y = 42;

	public ElectricFurnaceScreen (ElectricFurnaceMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		setProgressBarRenderer(new GuiProgressBarRenderer(PROGRESS_X, PROGRESS_Y, GuiProgressBarRenderer.RenderMode.HORIZONTAL, menu::getProgress, menu::getMaxProgress));
		setPowerRenderer(new GuiPowerRenderer(GuiPowerRenderer.RenderLocation.getDefault(), menu::getEnergyStored, menu::getEnergyCapacity));
	}

	@Override
	public Identifier getTexture () {
		return TEXTURE;
	}
}
