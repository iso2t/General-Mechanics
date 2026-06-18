package general.mechanics.datagen;

import general.api.formula.GenFormula;
import general.mechanics.GenMech;
import general.mechanics.datagen.data.SoundProvider;
import general.mechanics.datagen.lang.GenMechEnLangProvider;
import general.mechanics.datagen.loot.GenLootTableProvider;
import general.mechanics.datagen.model.BlockModelProvider;
import general.mechanics.datagen.model.ItemModelProvider;
import general.mechanics.datagen.recipe.GenRecipeProvider;
import general.mechanics.datagen.tags.GenBlockTagGenerator;
import general.mechanics.datagen.tags.GenItemTagGenerator;
import general.mechanics.formula.Formulas;
import general.mechanics.worldgen.GenFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = GenMech.MOD_ID)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherClient (@NotNull GatherDataEvent.Client event) {
		var generator = event.getGenerator();
		var registries = event.getLookupProvider();
		var pack = generator.getVanillaPack(true);
		var localization = new GenMechEnLangProvider(generator);
		var packOutput = generator.getPackOutput();

		// Sounds
		pack.addProvider(SoundProvider::new);

		pack.addProvider(bindRegistries(GenLootTableProvider::new, registries));

		// Formula API datapack registries + worldgen (rubber tree configured feature)
		var registrySet = Formulas.formulaRegistrySet().add(Registries.CONFIGURED_FEATURE, GenFeatures::bootstrap);
		pack.addProvider(output -> new DatapackBuiltinEntriesProvider(output, registries, registrySet, Set.of(GenFormula.NAMESPACE, GenMech.MOD_ID)));

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
