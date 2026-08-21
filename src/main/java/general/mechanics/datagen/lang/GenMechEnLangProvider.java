package general.mechanics.datagen.lang;

import general.api.crafting.MachineRecipeCatalog;
import general.api.mod.GenAPI;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenFluids;
import general.mechanics.registries.GenItems;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class GenMechEnLangProvider extends LanguageProvider {

	public GenMechEnLangProvider (DataGenerator generator) {
		super(generator.getPackOutput(), GenAPI.getModId(), "en_us");
	}

	@Override
	protected void addTranslations () {
		for (var item : GenItems.INSTANCE.getItems()) {
			add(item.get(), item.localizedName().getRawString());
		}

		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			add(block.get(), block.localizedName().getRawString());
		}

		for (var fluid : GenFluids.getFluids()) {
			add(fluid.type().get().getDescriptionId(), fluid.englishName());
		}

		for (var definition : MachineRecipeCatalog.definitions(GenAPI.getModId())) {
			add(definition.descriptionId(), definition.defaultEnglishName());
		}

		addManualTranslations();
		addSubtitles();
		addTooltips();
	}

	protected void addManualTranslations () {
		add("itemGroup." + GenAPI.getModId() + ".items", "General Mechanics");
		add("itemGroup." + GenAPI.getModId() + ".blocks", "General Mechanics - Blocks");
		add("guide.generalmechanics.machine_recipe.duration", "Time: %s s");
	}

	protected void addSubtitles () {
		add("subtitles.generalmechanics.plastic_block_place", "Block Placed");
		add("subtitles.generalmechanics.plastic_block_break", "Block Break");
		add("subtitles.generalmechanics.wrench_use", "Wrench");
	}

	protected void addTooltips () {
		add("genapi.formulas.tooltip.formula", "§7Formula: %s");
		add("genapi.formulas.tooltip.material", "§7Material: §f%s");
		add("genapi.formulas.tooltip.category", "§7Category: §f%s");
		add("genapi.formulas.tooltip.mass", "§7Mass: §f%s g/mol");
		add("genapi.formulats.tooltip.traits", "§7Traits: §f%s");
		add("genapi.hold_shift", "§o§8Hold §e[SHIFT]§8 for more info");
		add("genapi.fluid.empty", "Empty");
	}

}
