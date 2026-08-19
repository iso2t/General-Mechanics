package general.mechanics.registries;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * We register capabilities during the definition such that I dont have to keep scrolling to find this class every
 * time a new block is added.
 */
public class GenCapabilities {

	public static void register (RegisterCapabilitiesEvent event) {
		for (var blockEntity : GenBlockEntities.getBlockEntities()) {
			blockEntity.registerCapabilities(event);
		}
	}

}
