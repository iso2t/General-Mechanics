package general.mechanics.datagen.lang;

import general.api.mod.GenAPI;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class GenMechEnLangProvider extends LanguageProvider {

	public GenMechEnLangProvider (DataGenerator generator) {
		super(generator.getPackOutput(), "en_us", "en_us");
	}

	@Override
	protected void addTranslations () {
		for (var item : GenItems.INSTANCE.getItems()) {
			add(item.get(), item.localizedName().getRawString());
		}

		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			add(block.get(), block.localizedName().getRawString());
		}

		addManualTranslations();
	}

	protected void addManualTranslations () {
		add("itemGroup." + GenAPI.getModId() + ".items", "General Mechanics");
	}
}
