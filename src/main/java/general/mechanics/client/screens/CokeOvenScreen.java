package general.mechanics.client.screens;

import general.api.resources.Resource;
import general.api.screens.renderers.GuiFluidRenderer;
import general.api.screens.renderers.GuiProgressBarRenderer;
import general.api.screens.screen.AbstractScreen;
import general.mechanics.common.block.entity.CokeOvenControllerBlockEntity;
import general.mechanics.common.menus.CokeOvenMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CokeOvenScreen extends AbstractScreen<CokeOvenMenu> {

	private static final Identifier TEXTURE = Resource.getMainMod("textures/gui/coke_oven.png");

	public CokeOvenScreen (CokeOvenMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		setProgressBarRenderer(new GuiProgressBarRenderer(103, 32, menu::getProgress, menu::getMaxProgress));
		setFluidRenderer(new GuiFluidRenderer(149, 9, 17, 64, CokeOvenControllerBlockEntity.FLUIDS.get(CokeOvenControllerBlockEntity.CREOSOTE_TANK).capacity(), menu::getFluidStack, GuiFluidRenderer.TooltipMode.SHOW_AMOUNT_AND_CAPACITY));
	}

	@Override
	public Identifier getTexture () {
		return TEXTURE;
	}
}
