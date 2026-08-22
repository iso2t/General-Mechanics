package general.mechanics.datagen.world;

import general.mechanics.Mechanics;
import general.mechanics.worldgen.GenFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WorldGenProvider extends DatapackBuiltinEntriesProvider {

	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.CONFIGURED_FEATURE, GenFeatures::bootstrap)
			.add(Registries.PLACED_FEATURE, GenFeatures::placedFeatures)
			.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, GenFeatures::biomeModifiers);

	public WorldGenProvider (PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(Mechanics.MOD_ID));
	}

}
