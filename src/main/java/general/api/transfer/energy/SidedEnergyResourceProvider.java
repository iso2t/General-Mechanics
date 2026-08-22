package general.api.transfer.energy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.Nullable;

/**
 * Energy capability provider backed by cached, live side views.
 */
public interface SidedEnergyResourceProvider extends EnergyResourceProvider {

	SidedEnergyHandlers getSidedEnergyHandlers ();

	default @Nullable EnergyHandler getEnergyHandler (@Nullable Direction side) {
		return getSidedEnergyHandlers().forSide(side);
	}
}
