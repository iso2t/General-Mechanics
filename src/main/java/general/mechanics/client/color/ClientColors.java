package general.mechanics.client.color;

import general.api.resources.Resource;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ClientColors {

	public static void registerItemColors (RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Resource.getMainMod("fluid_tint"), FluidItemTintSource.CODEC);
	}

	public static void registerBlockColors (RegisterColorHandlersEvent.BlockTintSources event) {
		// Terrain fluid tinting is registered with the FluidModel in ClientFluidRegistration.
	}

}
