package general.mechanics.gui.component.button;

import general.mechanics.GM;
import general.mechanics.api.gui.MachineUiState;
import general.mechanics.gui.component.TabComponent;
import general.mechanics.gui.util.IconButton;
import general.mechanics.network.ToggleEnabledC2S;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class EnabledToggleButton extends TabComponent<IconButton> {

    private static final Identifier ENABLED_ON  = GM.getResource("textures/gui/elements/enabled.png");
    private static final Identifier DISABLED_ON = GM.getResource("textures/gui/elements/disabled.png");

    private final Supplier<MachineUiState> state;
    private final Supplier<BlockPos> pos;

    public EnabledToggleButton(int positionX, int positionY, Supplier<MachineUiState> state, Supplier<BlockPos> pos) {
        this(positionX, positionY, state, pos, new AtomicReference<>(() -> {
        }));
        this.onPress.set(this::toggle);
        refresh();
    }

    private final AtomicReference<Runnable> onPress;

    private EnabledToggleButton(int positionX, int positionY, Supplier<MachineUiState> state, Supplier<BlockPos> pos, AtomicReference<Runnable> onPressRef) {
        super(positionX, positionY, "gui.gm.enabled", new IconButton(positionX, positionY, BUTTON_WIDTH, BUTTON_HEIGHT, ENABLED_ON, 0, 0, 16, 16, 16, 16, 0, 0.9f, button -> onPressRef.get().run()));
        this.state = state;
        this.pos = pos;
        this.onPress = onPressRef;
    }

    public void refresh() {
        MachineUiState ui = state.get();
        getBuilder().setIcon(ui.enabled() ? ENABLED_ON : DISABLED_ON);
        getBuilder().setTooltip(ui.enabled()
                ? Tooltip.create(Component.translatable("gui.gm.enabled"))
                : Tooltip.create(Component.translatable("gui.gm.disabled")));
    }

    private void toggle() {
        MachineUiState ui = state.get();
        PacketDistributor.sendToServer(new ToggleEnabledC2S(pos.get(), !ui.enabled()));
        refresh();
    }
}
