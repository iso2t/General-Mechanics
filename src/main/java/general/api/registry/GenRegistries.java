package general.api.registry;

import general.api.multiblock.Multiblock;
import general.api.resources.Resource;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class GenRegistries {

	public static final ResourceKey<Registry<Multiblock>> MULTIBLOCKS = createRegistryKey("multiblocks");

	private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
		return ResourceKey.createRegistryKey(Resource.getMainMod(name));
	}

}
