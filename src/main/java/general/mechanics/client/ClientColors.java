package general.mechanics.client;

import general.api.block.plastic.ColoredPlasticBlock;
import general.api.block.plastic.PlasticTypeBlock;
import general.api.definitions.BlockDefinition;
import general.api.resources.Resource;
import general.mechanics.client.color.PlasticBlockTintSource;
import general.mechanics.client.color.PlasticTintSource;
import general.mechanics.registries.GenBlocks;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

public class ClientColors {

	public static void registerItemColors (RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Resource.get("plastic"), PlasticTintSource.MAP_CODEC);
	}

	public static void registerBlockColors (RegisterColorHandlersEvent.BlockTintSources event) {
		Block[] blocks = GenBlocks.INSTANCE.getBlocks().stream()
				.map(BlockDefinition::get)
				.filter(block -> block instanceof PlasticTypeBlock || block instanceof ColoredPlasticBlock)
				.toArray(Block[]::new);
		if (blocks.length > 0) {
			event.register(List.of(PlasticBlockTintSource.INSTANCE), blocks);
		}
	}

}
