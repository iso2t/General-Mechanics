package general.api.transfer.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;

/**
 * Controller-owned energy storage that can be routed through a multiblock hatch.
 */
public interface EnergyResourceProvider {

	EnergyHandler getEnergyHandler ();
}
