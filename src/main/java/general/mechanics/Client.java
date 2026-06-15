package general.mechanics;

import general.api.mod.GeneralMod;
import general.mechanics.client.ClientColors;
import general.mechanics.client.ClientTooltips;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = GenMech.MOD_ID, dist = Dist.CLIENT)
@GeneralMod(GenMech.MOD_ID)
public class Client extends Base {

	public Client (ModContainer container, IEventBus bus) {
		super(container, bus);
		registerClientColors();
		registerTooltips();
	}

	private void registerClientColors () {
		getBus().addListener(ClientColors::registerItemColors);
		getBus().addListener(ClientColors::registerBlockColors);
	}

	private void registerTooltips () {
		NeoForge.EVENT_BUS.addListener(ClientTooltips::onItemTooltip);
	}

	@Override
	public Level getClientLevel () {
		return Minecraft.getInstance().level;
	}

}
