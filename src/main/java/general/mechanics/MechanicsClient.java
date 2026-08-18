package general.mechanics;

import general.api.mod.GeneralMod;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Mechanics.MOD_ID, dist = Dist.CLIENT)
@GeneralMod(Mechanics.MOD_ID)
public class MechanicsClient extends MechanicsBase {

	public MechanicsClient (ModContainer container, IEventBus bus) {
		super(container, bus);
	}

	@Override
	public Level getClientLevel () {
		return Minecraft.getInstance().level;
	}

}
