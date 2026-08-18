package general.mechanics.registries;

import general.api.definitions.SoundDefinition;
import general.api.mod.GenAPI;
import general.api.resources.Resource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GenSounds {

	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, GenAPI.getModId());

	public static final SoundDefinition<SoundEvent> PLASTIC_BLOCK_PLACE = register("plastic_block_place");
	public static final SoundDefinition<SoundEvent> PLASTIC_BLOCK_BREAK = register("plastic_block_break");
	public static final SoundDefinition<SoundEvent> WRENCH              = register("wrench_use");

	public static final DeferredSoundType PLASTIC_BLOCK = new DeferredSoundType(1f, 1f, PLASTIC_BLOCK_BREAK, PLASTIC_BLOCK_BREAK, PLASTIC_BLOCK_PLACE, PLASTIC_BLOCK_BREAK, PLASTIC_BLOCK_BREAK);

	private static SoundDefinition<SoundEvent> register (String name) {
		return new SoundDefinition<>(REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(Resource.get(name))));
	}

}
