package general.mechanics.gui.screen;

import general.mechanics.GM;
import general.mechanics.gui.menu.MatterFabricatorMenu;
import general.mechanics.gui.screen.base.BaseScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MatterFabricatorScreen extends BaseScreen<MatterFabricatorMenu> {

	public MatterFabricatorScreen (MatterFabricatorMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	public Identifier getGuiTexture () {
		return GM.getResource("textures/gui/matter_fabricator.png");
	}

	@Override
	public void addAdditionalTabElements (GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
	}
}
