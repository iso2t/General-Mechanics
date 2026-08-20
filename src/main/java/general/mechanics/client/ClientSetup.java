package general.mechanics.client;

import general.mechanics.Mechanics;
import general.mechanics.client.model.CableModelLoader;
import general.mechanics.client.screens.CokeOvenScreen;
import general.mechanics.registries.GenMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Mechanics.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

	@SubscribeEvent
	public static void modelInit (RegisterBlockStateModels event) {
		CableModelLoader.register(event);
	}

	@SubscribeEvent
	public static void registerBlockColor (RegisterColorHandlersEvent.BlockTintSources event) {
		//event.register(new FacadeBlockColor(), GenBlocks.FACADE.get());
	}

	@SubscribeEvent
	public static void registerMenuScreens (RegisterMenuScreensEvent event) {
		event.register(GenMenus.COKE_OVEN.get(), CokeOvenScreen::new);
	}

}
