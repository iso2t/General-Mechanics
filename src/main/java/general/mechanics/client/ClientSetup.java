package general.mechanics.client;

import general.api.screens.renderers.MachineConfigurationRenderState;
import general.api.screens.renderers.MachineConfigurationRenderer;
import general.mechanics.Mechanics;
import general.mechanics.client.model.CableModelLoader;
import general.mechanics.client.model.ConfigurableMachineModelLoader;
import general.mechanics.client.screens.CokeOvenScreen;
import general.mechanics.client.screens.ElectricFurnaceScreen;
import general.mechanics.client.screens.MaceratorScreen;
import general.mechanics.client.screens.StampingPressScreen;
import general.mechanics.registries.GenMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@EventBusSubscriber(modid = Mechanics.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

	@SubscribeEvent
	public static void modelInit (RegisterBlockStateModels event) {
		CableModelLoader.register(event);
		ConfigurableMachineModelLoader.register(event);
	}

	@SubscribeEvent
	public static void registerBlockColor (RegisterColorHandlersEvent.BlockTintSources event) {
		//event.register(new FacadeBlockColor(), GenBlocks.FACADE.get());
	}

	@SubscribeEvent
	public static void registerMenuScreens (RegisterMenuScreensEvent event) {
		event.register(GenMenus.COKE_OVEN.get(), CokeOvenScreen::new);
		event.register(GenMenus.ELECTRIC_FURNACE.get(), ElectricFurnaceScreen::new);
		event.register(GenMenus.STAMPING_PRESS.get(), StampingPressScreen::new);
		event.register(GenMenus.MACERATOR.get(), MaceratorScreen::new);
	}

	@SubscribeEvent
	public static void registerPictureInPictureRenderers (RegisterPictureInPictureRenderersEvent event) {
		event.register(MachineConfigurationRenderState.class, MachineConfigurationRenderer::new);
	}

}
