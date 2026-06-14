package general.mechanics.datagen.data;

import general.api.mod.GenAPI;
import general.mechanics.registries.GenSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SoundProvider extends SoundDefinitionsProvider {

	public SoundProvider (PackOutput output) {
		super(output, GenAPI.getModId());
	}

	@Override
	public void registerSounds () {
		add(GenSounds.PLASTIC_BLOCK_PLACE.get(), SoundDefinition.definition()
				.with(sound(GenAPI.getModId() + ":plastic_block_place", SoundDefinition.SoundType.SOUND)
						.volume(1.f).pitch(1.f).weight(1).stream(true))
				.subtitle("subtitles.gm.plastic_block_place"));

		add(GenSounds.PLASTIC_BLOCK_BREAK.get(), SoundDefinition.definition()
				.with(sound(GenAPI.getModId() + ":plastic_block_break", SoundDefinition.SoundType.SOUND)
						.volume(1.f).pitch(1.f).weight(1).stream(true))
				.subtitle("subtitles.gm.plastic_block_break"));
	}
}
