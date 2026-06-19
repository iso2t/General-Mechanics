package general.mechanics.client;

import general.api.block.plastic.ColoredPlasticBlock;
import general.api.block.plastic.PlasticTypeBlock;
import general.api.definitions.BlockDefinition;
import general.api.resources.Resource;
import general.mechanics.client.color.MaterialTintSource;
import general.mechanics.client.color.PlasticBlockTintSource;
import general.mechanics.client.color.PlasticTintSource;
import general.mechanics.client.color.RubberTintSource;
import general.mechanics.registries.GenBlocks;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

public class ClientColors {

	public static void registerItemColors (RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Resource.get("plastic"), PlasticTintSource.MAP_CODEC);
		event.register(Resource.get("material"), MaterialTintSource.MAP_CODEC);
		event.register(Resource.get("rubber"), RubberTintSource.MAP_CODEC);
	}

	public static void registerBlockColors (RegisterColorHandlersEvent.BlockTintSources event) {
		Block[] blocks = GenBlocks.INSTANCE.getBlocks().stream()
				.map(BlockDefinition::get)
				.filter(block -> block instanceof PlasticTypeBlock || block instanceof ColoredPlasticBlock)
				.toArray(Block[]::new);
		if (blocks.length > 0) {
			event.register(List.of(PlasticBlockTintSource.INSTANCE), blocks);
		}

		// Biome foliage tint for rubber leaves (tintindex 0), matching vanilla oak/jungle leaves.
		event.register(List.of(BlockTintSources.foliage()), GenBlocks.RUBBER_LEAVES.get());
	}

}
