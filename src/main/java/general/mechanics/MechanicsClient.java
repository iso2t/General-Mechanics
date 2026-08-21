package general.mechanics;

import general.api.mod.GeneralMod;
import general.mechanics.client.ClientFluidRegistration;
import general.mechanics.client.color.ClientColors;
import general.mechanics.client.crafting.ClientMachineRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Mechanics.MOD_ID, dist = Dist.CLIENT)
@GeneralMod(Mechanics.MOD_ID)
public class MechanicsClient extends MechanicsBase {

	public MechanicsClient (ModContainer container, IEventBus bus) {
		super(container, bus);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientMachineRecipes::receive);
		NeoForge.EVENT_BUS.addListener(ClientMachineRecipes::logout);
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
