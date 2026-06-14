package general.mechanics;

import general.api.mod.GeneralMod;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = GenMech.MOD_ID, dist = Dist.DEDICATED_SERVER)
@GeneralMod(GenMech.MOD_ID)
public class Server extends Base {

	public Server (ModContainer container, IEventBus bus) {
		super(container, bus);
	}

	@Override
	public Level getClientLevel () {
		return null;
	}

}
