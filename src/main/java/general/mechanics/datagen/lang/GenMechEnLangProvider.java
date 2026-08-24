package general.mechanics.datagen.lang;

import general.api.crafting.MachineRecipeCatalog;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideMode;
import general.api.mod.GenAPI;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenFluids;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenMultiblocks;
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

		for (var multiblock : GenMultiblocks.getMultiblocks()) {
			add(multiblock.toString(), multiblock.localizedName().getRawString());
		}

		addManualTranslations();
		addSubtitles();
		addTooltips();
	}

	protected void addManualTranslations () {
		add("itemGroup." + GenAPI.getModId() + ".items", "General Mechanics");
		add("itemGroup." + GenAPI.getModId() + ".blocks", "General Mechanics - Blocks");
		add("gui.generalmechanics.machine_recipe.time.seconds", "%ss");
		add("gui.generalmechanics.machine_recipe.time.minutes_seconds", "%sm %ss");
		add("gui.generalmechanics.machine.item_lock.locked", "Input Lock: Locked");
		add("gui.generalmechanics.machine.item_lock.unlocked", "Input Lock: Unlocked");
		add("gui.generalmechanics.machine.side_config.open", "Configure Sides");
		add("gui.generalmechanics.machine.side_config.close", "Close Side Configuration");
		add("gui.generalmechanics.machine.side_config.locked", "Locked");
		add("gui.generalmechanics.machine.side_config.face_mode", "%s: %s");
		//add("gui.generalmechanics.machine.side_config.controls", "Left: next, Right: previous; drag the block to rotate");
		for (MachineFace face : MachineFace.values()) {
			add("gui.generalmechanics.machine.side_config.face." + face.getSerializedName(), titleCase(face.getSerializedName()));
		}
		for (MachineSideMode mode : MachineSideMode.values()) {
			add("gui.generalmechanics.machine.side_config.mode." + mode.getSerializedName(), modeName(mode));
			add("gui.generalmechanics.machine.side_config.mode_short." + mode.getSerializedName(), shortModeName(mode));
		}
		add("guide.generalmechanics.machine_recipe.duration", "Time: %s s");
		add("death.attack.heating_element", "Turns out we weren't lying. Heating Element is hot. GenTech™ thanks you for continuing our research.");
	}

	private static String titleCase (String value) {
		return Character.toUpperCase(value.charAt(0)) + value.substring(1).replace('_', ' ');
	}

	private static String modeName (MachineSideMode mode) {
		return switch (mode) {
			case NONE -> "Disabled";
			case ITEM_INPUT -> "Item Input";
			case ITEM_OUTPUT -> "Item Output";
			case FLUID_INPUT -> "Fluid Input";
			case FLUID_OUTPUT -> "Fluid Output";
			case ENERGY_INPUT -> "Energy Input";
			case NETWORK -> "Network";
		};
	}

	private static String shortModeName (MachineSideMode mode) {
		return switch (mode) {
			case NONE -> "Disabled";
			case ITEM_INPUT -> "Item In";
			case ITEM_OUTPUT -> "Item Out";
			case FLUID_INPUT -> "Fluid In";
			case FLUID_OUTPUT -> "Fluid Out";
			case ENERGY_INPUT -> "Energy";
			case NETWORK -> "Network";
		};
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
