package general.mechanics.client.color;

import general.api.resources.Resource;
import general.mechanics.registries.GenBlocks;
import net.minecraft.client.color.block.BlockTintSources;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

public class ClientColors {

	public static void registerItemColors (RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Resource.getMainMod("fluid_tint"), FluidItemTintSource.CODEC);
		event.register(Resource.getMainMod("element_tint"), ElementItemTintSource.CODEC);
	}

	public static void registerBlockColors (RegisterColorHandlersEvent.BlockTintSources event) {
		event.register(List.of(BlockTintSources.foliage()), GenBlocks.RUBBER_LEAVES.get());
	}

}
