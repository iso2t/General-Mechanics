package general.mechanics.gui.component.button;

import general.mechanics.gui.component.TabComponent;
import general.mechanics.gui.util.IconButton;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public abstract class TabButtonComponent extends TabComponent<IconButton> {

    @FunctionalInterface
    protected interface IconButtonFactory {
        IconButton create(int x, int y, Runnable onPress);
    }

    private final AtomicReference<Runnable> onPress;

    protected TabButtonComponent(int positionX, int positionY, @Nullable String tooltip, Identifier icon) {
        this(positionX, positionY, tooltip, icon, new AtomicReference<>(() -> {}));
    }

    protected TabButtonComponent(int positionX, int positionY, @Nullable String tooltip, Identifier icon, Runnable onPress) {
        this(positionX, positionY, tooltip, icon);
        setOnPress(onPress);
    }

    protected TabButtonComponent(int positionX, int positionY, @Nullable String tooltip, IconButtonFactory factory) {
        this(positionX, positionY, tooltip, factory, new AtomicReference<>(() -> {}));
    }

    protected TabButtonComponent(int positionX, int positionY, @Nullable String tooltip, IconButtonFactory factory, Runnable onPress) {
        this(positionX, positionY, tooltip, factory);
        setOnPress(onPress);
    }

    private TabButtonComponent(int positionX, int positionY, @Nullable String tooltip, Identifier icon, AtomicReference<Runnable> onPressRef) {
        super(positionX, positionY, tooltip, createButton(positionX, positionY, icon, onPressRef));
        this.onPress = onPressRef;
    }

    private TabButtonComponent(int positionX, int positionY, @Nullable String tooltip, IconButtonFactory factory, AtomicReference<Runnable> onPressRef) {
        super(positionX, positionY, tooltip, factory.create(positionX, positionY, () -> onPressRef.get().run()));
        this.onPress = onPressRef;
    }

    protected static IconButton createButton (int positionX, int positionY, Identifier icon, AtomicReference<Runnable> onPressRef) {
        return new IconButton(positionX, positionY, BUTTON_WIDTH, BUTTON_HEIGHT, icon, 0, 0, 16, 16, 16, 16, 0, button -> onPressRef.get().run());
    }

    protected final void setOnPress(Runnable onPress) {
        this.onPress.set(onPress);
    }

}
