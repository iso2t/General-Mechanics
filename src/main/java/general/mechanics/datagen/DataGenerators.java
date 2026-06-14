package general.mechanics.datagen;

import general.mechanics.GenMech;
import general.mechanics.datagen.data.SoundProvider;
import general.mechanics.datagen.lang.GenMechEnLangProvider;
import general.mechanics.datagen.model.BlockModelProvider;
import general.mechanics.datagen.model.ItemModelProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

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

		// Models
		pack.addProvider(BlockModelProvider::new);
		pack.addProvider(ItemModelProvider::new);

		pack.addProvider(_ -> localization);
	}

}
