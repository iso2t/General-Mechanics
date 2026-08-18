package general.api.definitions;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public record SoundDefinition<T extends SoundEvent>(DeferredHolder<T, T> holder) implements Supplier<T> {
	@Override
	public T get () {
		return holder.get();
	}
}
