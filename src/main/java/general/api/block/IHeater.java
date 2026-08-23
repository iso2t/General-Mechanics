package general.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IHeater {

	/**
	 * Return true if this heater should heat the furnace at furnacePos right now.
	 */
	boolean isHeating (Level level, BlockPos furnacePos);

	/**
	 * How many ticks to (re)grant when heating. 200 = 10 seconds.
	 */
	int burnChunkTicks (Level level, BlockPos furnacePos);

}
