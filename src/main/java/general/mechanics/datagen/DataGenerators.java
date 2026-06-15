package general.mechanics.datagen;

import general.api.formula.GenFormula;
import general.mechanics.GenMech;
import general.mechanics.datagen.data.SoundProvider;
import general.mechanics.datagen.lang.GenMechEnLangProvider;
import general.mechanics.datagen.model.BlockModelProvider;
import general.mechanics.datagen.model.ItemModelProvider;
import general.mechanics.datagen.tags.GenBlockTagGenerator;
import general.mechanics.formula.GMFormula;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@EventBusSubscriber(modid = GenMech.MOD_ID)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherClient (@NotNull GatherDataEvent.Client event) {
		var generator = event.getGenerator();
		var registries = event.getLookupProvider();
		var pack = generator.getVanillaPack(true);
		var localization = new GenMechEnLangProvider(generator);

		// Sounds
		pack.addProvider(SoundProvider::new);

		// Formula API datapack registries (genapi built-ins + this mod's materials)
		pack.addProvider(output -> new DatapackBuiltinEntriesProvider(output, registries, GMFormula.formulaRegistrySet(), Set.of(GenFormula.NAMESPACE, GenMech.MOD_ID)));

		// Tags
		var blockTagsProvider = pack.addProvider(output -> new GenBlockTagGenerator(output, registries));

		// Models
		pack.addProvider(BlockModelProvider::new);
		pack.addProvider(ItemModelProvider::new);

		// Localization has to run last
		pack.addProvider(_ -> localization);
	}

}
