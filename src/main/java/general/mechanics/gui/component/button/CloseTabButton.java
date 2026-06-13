package general.mechanics.gui.component.button;

import general.mechanics.GM;
import general.mechanics.gui.component.TabComponent;
import general.mechanics.gui.util.IconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CloseTabButton extends TabComponent<IconButton> {

    private static final Identifier CLOSE = GM.getResource("textures/gui/elements/close_button.png");

    public CloseTabButton(int positionX, int positionY, Runnable onClose) {
        super(positionX, positionY, "gui.gm.close_button", new IconButton(positionX, positionY, 8, 8, CLOSE, 0, 0, 8, 8, 8, 8, 0, 0.5f, button -> onClose.run()));
        getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.close_button")));
    }
}
