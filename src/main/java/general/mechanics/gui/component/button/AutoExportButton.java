package general.mechanics.gui.component.button;

import general.mechanics.GM;
import general.mechanics.api.gui.MachineUiState;
import general.mechanics.network.ToggleExportC2S;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public class AutoExportButton extends TabButtonComponent {

    private static final Identifier EXPORT_ON  = GM.getResource("textures/gui/elements/export_on.png");
    private static final Identifier EXPORT_OFF = GM.getResource("textures/gui/elements/export_off.png");

    private final Supplier<MachineUiState> state;
    private final Supplier<BlockPos> pos;

    public AutoExportButton(int positionX, int positionY, Supplier<MachineUiState> state, Supplier<BlockPos> pos) {
        super(positionX, positionY, "gui.gm.auto_export", EXPORT_OFF);
        this.state = state;
        this.pos = pos;
        setOnPress(this::toggle);
        getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.auto_export")));
        refresh();
    }

    public void refresh() {
        MachineUiState ui = state.get();
        getBuilder().setIcon(ui.exportEnabled() ? EXPORT_ON : EXPORT_OFF);
    }

    private void toggle() {
        MachineUiState ui = state.get();
        PacketDistributor.sendToServer(new ToggleExportC2S(pos.get(), !ui.exportEnabled()));
        refresh();
    }
}
