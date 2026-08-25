package general.mechanics.client.screens;

import general.api.resources.Resource;
import general.api.screens.renderers.GuiFluidRenderer;
import general.api.screens.renderers.GuiPowerRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import general.api.screens.screen.AbstractScreen;
import general.mechanics.common.menus.FluidInfuserMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FluidInfuserScreen extends AbstractScreen<FluidInfuserMenu> {

	private static final Identifier TEXTURE = Resource.getMainMod("textures/gui/fluid_infuser.png");

	public FluidInfuserScreen (FluidInfuserMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		setFluidRenderer(new GuiFluidRenderer(80, 9, 16, 64, menu::getFluidCapacity, menu::getFluidStack, GuiFluidRenderer.TooltipMode.SHOW_AMOUNT_AND_CAPACITY));
		setProgressBarRenderer(new GuiProgressBarRenderer(114, 31, menu::getProgress, menu::getMaxProgress));
		setPowerRenderer(new GuiPowerRenderer(GuiPowerRenderer.RenderLocation.getDefault(), menu::getEnergyStored, menu::getEnergyCapacity));
	}

	@Override
	public Identifier getTexture () {
		return TEXTURE;
	}
}
