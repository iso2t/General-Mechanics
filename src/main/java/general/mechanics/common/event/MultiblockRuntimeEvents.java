package general.mechanics.common.event;

import general.api.multiblock.MultiblockHandler;
import general.mechanics.Mechanics;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Bridges NeoForge's world lifecycle events into the generic multiblock runtime.
 */
@EventBusSubscriber(modid = Mechanics.MOD_ID)
public final class MultiblockRuntimeEvents {

	private MultiblockRuntimeEvents () {
	}

	@SubscribeEvent
	public static void onBlockPlaced (BlockEvent.EntityPlaceEvent event) {
		if (event.getLevel() instanceof ServerLevel level) {
			MultiblockHandler.onBlockChanged(level, event.getPos());
		}
	}

	@SubscribeEvent
	public static void onBlockBroken (BreakBlockEvent event) {
		if (event.getLevel() instanceof ServerLevel level) {
			MultiblockHandler.onBlockChanged(level, event.getPos());
		}
	}

	@SubscribeEvent
	public static void onFluidPlaced (BlockEvent.FluidPlaceBlockEvent event) {
		if (event.getLevel() instanceof ServerLevel level) {
			MultiblockHandler.onBlockChanged(level, event.getPos());
		}
	}

	@SubscribeEvent
	public static void onBlockStateChanged (BlockEvent.NeighborNotifyEvent event) {
		if (event.getLevel() instanceof ServerLevel level) {
			MultiblockHandler.onBlockChanged(level, event.getPos());
		}
	}

	@SubscribeEvent
	public static void onChunkLoaded (ChunkEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel level) {
			var chunk = event.getChunk();
			MultiblockHandler.onChunkLoaded(level, chunk);
		}
	}

	@SubscribeEvent
	public static void onLevelTick (LevelTickEvent.Post event) {
		if (event.getLevel() instanceof ServerLevel level) {
			MultiblockHandler.tick(level);
		}
	}

	@SubscribeEvent
	public static void onLevelUnloaded (LevelEvent.Unload event) {
		if (event.getLevel() instanceof ServerLevel level) {
			MultiblockHandler.onLevelUnloaded(level);
		}
	}
}
