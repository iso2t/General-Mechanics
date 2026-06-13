package general.mechanics.gui.component.button;

import general.mechanics.GM;
import general.mechanics.api.gui.MachineUiState;
import general.mechanics.network.ToggleImportC2S;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public class AutoImportButton extends TabButtonComponent {

    private static final Identifier IMPORT_ON  = GM.getResource("textures/gui/elements/import_on.png");
    private static final Identifier IMPORT_OFF = GM.getResource("textures/gui/elements/import_off.png");

    private final Supplier<MachineUiState> state;
    private final Supplier<BlockPos> pos;

    public AutoImportButton(int positionX, int positionY, Supplier<MachineUiState> state, Supplier<BlockPos> pos) {
        super(positionX, positionY, "gui.gm.auto_import", IMPORT_OFF);
        this.state = state;
        this.pos = pos;
        setOnPress(this::toggle);
        getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.auto_import")));
        refresh();
    }

    public void refresh() {
        MachineUiState ui = state.get();
        getBuilder().setIcon(ui.importEnabled() ? IMPORT_ON : IMPORT_OFF);
    }

    private void toggle() {
        MachineUiState ui = state.get();
        PacketDistributor.sendToServer(new ToggleImportC2S(pos.get(), !ui.importEnabled()));
        refresh();
    }
}
