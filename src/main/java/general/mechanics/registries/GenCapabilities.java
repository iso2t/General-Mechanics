package general.mechanics.registries;

import general.api.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class GenCapabilities {

	public static void register (RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.NETWORK_HANDLER_BLOCK, GenBlockEntities.CABLE.get(), (cable, side) -> cable);
		event.registerBlockEntity(Capabilities.NETWORK_HANDLER_BLOCK, GenBlockEntities.NETWORK_CONNECTOR.get(), (connector, side) -> side != Direction.UP ? connector : null);
	}

}
