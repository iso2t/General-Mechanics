package general.api.machine;

import general.api.transfer.energy.ProfiledEnergyHandler;
import general.api.transfer.energy.SidedEnergyHandlers;
import general.api.transfer.energy.SidedEnergyResourceProvider;

/**
 * Opt-in energy-storage feature for a machine block entity.
 */
public interface MachineEnergy extends MachineHost, SidedEnergyResourceProvider {

	@Override
	default ProfiledEnergyHandler getEnergyHandler () {
		return machine().requireEnergyHandler();
	}

	@Override
	default SidedEnergyHandlers getSidedEnergyHandlers () {
		return machine().requireSidedEnergyHandlers();
	}
}
