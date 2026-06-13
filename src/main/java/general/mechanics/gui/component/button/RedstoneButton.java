package general.mechanics.gui.component.button;

import general.mechanics.GM;
import general.mechanics.api.entity.block.BasePoweredBlockEntity;
import general.mechanics.api.gui.MachineUiState;
import general.mechanics.network.ToggleRedstoneModeC2S;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RedstoneButton extends TabButtonComponent {

    private static final Identifier REDSTONE_HIGH = GM.getMinecraftResource("textures/item/redstone.png");
    private static final Identifier REDSTONE_LOW  = GM.getResource("textures/gui/elements/redstone_low.png");
    private static final Identifier REDSTONE_DISABLED = GM.getMinecraftResource("textures/item/gunpowder.png");

    private final Supplier<MachineUiState> state;
    private final Supplier<BlockPos> pos;

    public RedstoneButton(int positionX, int positionY, Supplier<MachineUiState> state, Supplier<BlockPos> pos) {
        super(positionX, positionY, "gui.gm.redstone_mode", REDSTONE_HIGH);
        this.state = state;
        this.pos = pos;
        setOnPress(this::toggleRedstoneMode);
        refresh();
    }

    public void refresh() {
        MachineUiState ui = state.get();
        BasePoweredBlockEntity.RedstoneMode mode = ui.redstoneMode();

        getBuilder().setIcon(switch (mode) {
            case LOW -> REDSTONE_LOW;
            case HIGH -> REDSTONE_HIGH;
            case IGNORED -> REDSTONE_DISABLED;
        });

        getBuilder().setTooltip(Tooltip.create(Component.translatable("gui.gm.redstone_mode", Component.translatable("gui.gm.redstone_mode." +
                switch (mode) {
                    case LOW -> BasePoweredBlockEntity.RedstoneMode.LOW.name().toLowerCase();
                    case HIGH -> BasePoweredBlockEntity.RedstoneMode.HIGH.name().toLowerCase();
                    case IGNORED -> BasePoweredBlockEntity.RedstoneMode.IGNORED.name().toLowerCase();
                }
        ))));
    }

    private void toggleRedstoneMode() {
        MachineUiState ui = state.get();
        int next = ui.redstoneMode().next().id();
        PacketDistributor.sendToServer(new ToggleRedstoneModeC2S(pos.get(), next));
        refresh();
    }

}
