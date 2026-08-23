package general.api.machine;

import general.api.capabilities.GeneralCapabilities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Objects;

/**
 * Registers every optional machine capability through its runtime.
 */
public final class MachineCapabilities {

	private MachineCapabilities () {
	}

	public static <T extends MachineBlockEntity> void register (RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		Objects.requireNonNull(event, "event");
		Objects.requireNonNull(type, "type");
		event.registerBlockEntity(Capabilities.Item.BLOCK, type, (machine, side) -> machine.machine().getItemCapability(side));
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, (machine, side) -> machine.machine().getFluidCapability(side));
		event.registerBlockEntity(Capabilities.Energy.BLOCK, type, (machine, side) -> machine.machine().getEnergyCapability(side));
		event.registerBlockEntity(GeneralCapabilities.NETWORK_HANDLER_BLOCK, type, (machine, side) -> machine.machine().getNetworkCapability(side));
	}
}
