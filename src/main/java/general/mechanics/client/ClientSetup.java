package general.mechanics.client;

import general.mechanics.GenMech;
import general.mechanics.client.model.CableModelLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;

@EventBusSubscriber(modid = GenMech.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

	@SubscribeEvent
	public static void modelInit (RegisterBlockStateModels event) {
		CableModelLoader.register(event);
	}

	@SubscribeEvent
	public static void registerBlockColor (RegisterColorHandlersEvent.BlockTintSources event) {
		//event.register(new FacadeBlockColor(), GenBlocks.FACADE.get());
	}

}
