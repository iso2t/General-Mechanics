package general.mechanics.client.color;

import general.api.block.materials.MetalBlock;
import general.api.definitions.BlockDefinition;
import general.api.resources.Resource;
import general.mechanics.registries.GenBlocks;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

public class ClientColors {
	private static final BlockTintSource METAL_BLOCK_TINT = state -> state.getBlock() instanceof MetalBlock metal ? metal.getColor() : 0xFFFFFFFF;

	public static void registerItemColors (RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Resource.getMainMod("fluid_tint"), FluidItemTintSource.CODEC);
		event.register(Resource.getMainMod("element_tint"), ElementItemTintSource.CODEC);
	}

	public static void registerBlockColors (RegisterColorHandlersEvent.BlockTintSources event) {
		event.register(List.of(BlockTintSources.foliage()), GenBlocks.RUBBER_LEAVES.get());

		Block[] metalBlocks = GenBlocks.INSTANCE.getBlocks().stream().map(BlockDefinition::get).filter(MetalBlock.class::isInstance).toArray(Block[]::new);
		event.register(List.of(METAL_BLOCK_TINT), metalBlocks);
	}

}
