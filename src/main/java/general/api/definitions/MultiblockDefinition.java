package general.api.definitions;

import general.api.multiblock.Multiblock;
import general.api.registry.IRegistryNameProvider;
import general.api.registry.RegistryString;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public record MultiblockDefinition(RegistryString localizedName, DeferredHolder<Multiblock, Multiblock> holder) implements Supplier<Multiblock>, IRegistryNameProvider {

	public MultiblockDefinition (String localizedName, DeferredHolder<Multiblock, Multiblock> holder) {
		this(new RegistryString(localizedName), holder);
	}

	public Identifier getId () {
		return holder.getId();
	}

	@Override
	public Multiblock get () {
		return holder.get();
	}

	@Override
	public RegistryString getRegistryString () {
		return localizedName;
	}
}
