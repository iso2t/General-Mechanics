package general.mechanics.datagen;

import general.mechanics.Mechanics;
import general.mechanics.datagen.data.SoundProvider;
import general.mechanics.datagen.lang.GenMechEnLangProvider;
import general.mechanics.datagen.loot.GenLootTableProvider;
import general.mechanics.datagen.model.BlockModelProvider;
import general.mechanics.datagen.model.ItemModelProvider;
import general.mechanics.datagen.recipe.GenRecipeProvider;
import general.mechanics.datagen.tags.GenBlockTagGenerator;
import general.mechanics.datagen.tags.GenItemTagGenerator;
import general.mechanics.datagen.world.WorldGenProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = Mechanics.MOD_ID)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherClient (@NotNull GatherDataEvent.Client event) {
		var generator = event.getGenerator();
		var registries = event.getLookupProvider();
		var pack = generator.getVanillaPack(true);
		var localization = new GenMechEnLangProvider(generator);
		var packOutput = generator.getPackOutput();

		// World Gen
		pack.addProvider(output -> new WorldGenProvider(output, registries));

		// Sounds
		pack.addProvider(SoundProvider::new);

		// Loot tables
		pack.addProvider(bindRegistries(GenLootTableProvider::new, registries));

		// Tags
		var blockTagsProvider = pack.addProvider(output -> new GenBlockTagGenerator(output, registries));
		pack.addProvider(output -> new GenItemTagGenerator(output, registries));

		// Models
		pack.addProvider(BlockModelProvider::new);
		pack.addProvider(ItemModelProvider::new);

		// Recipes
		generator.addProvider(true, new GenRecipeProvider.Runner(packOutput, registries));

		// Localization has to run last
		pack.addProvider(_ -> localization);
	}

	@Contract(pure = true)
	private static <T extends DataProvider> DataProvider.@NotNull Factory<T> bindRegistries (BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory, CompletableFuture<HolderLookup.Provider> factories) {
		return pOutput -> factory.apply(pOutput, factories);
	}

}
