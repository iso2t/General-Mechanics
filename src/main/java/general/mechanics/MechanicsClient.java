package general.mechanics;

import general.api.mod.GeneralMod;
import general.mechanics.client.ClientFluidRegistration;
import general.mechanics.client.color.ClientColors;
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
		bus.addListener(ClientFluidRegistration::registerExtensions);
		bus.addListener(ClientFluidRegistration::registerModels);
		bus.addListener(ClientColors::registerItemColors);
		bus.addListener(ClientColors::registerBlockColors);
	}

	@Override
	public Level getClientLevel () {
		return Minecraft.getInstance().level;
	}

}
