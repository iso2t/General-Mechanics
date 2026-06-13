package general.mechanics.api.multiblock;

import general.mechanics.api.multiblock.event.MultiblockDestroyedEvent;
import general.mechanics.api.multiblock.event.MultiblockFormedEvent;
import general.mechanics.api.multiblock.base.Multiblock;
import general.mechanics.registries.CoreRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Manages multiblock tracking and provides feedback when multiblocks are formed or destroyed.
 * This class handles automatic detection and event firing for multiblock changes.
 */
public class MultiblockManager {
    
    private static final Map<BlockPos, MultiblockInfo> activeMultiblocks = new HashMap<>();
    private static final Map<BlockPos, Multiblock> activeMultiblockObjects = new HashMap<>();
    
    
    /**
     * Initialize the multiblock manager by registering event handlers.
     */
    public static void init(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(MultiblockManager::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(MultiblockManager::onBlockPlace);

        NeoForge.EVENT_BUS.addListener(MultiblockManager::onServerTick);

        NeoForge.EVENT_BUS.addListener(MultiblockManager::onPlayerInteract);

        NeoForge.EVENT_BUS.addListener(MultiblockManager::onWorldLoad);
    }
    
    /**
     * Manually check for multiblocks at a specific position.
     * This can be called when you want to trigger a check programmatically.
     */
    public static Optional<MultiblockFormedEvent> checkForMultiblock(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) {
            return Optional.empty();
        }

        if (activeMultiblocks.containsKey(pos)) {
            return Optional.empty();
        }

        for (var definition : CoreRegistries.MULTIBLOCK_DEFINITIONS) {
            if (definition == null) continue;
            
            Optional<MultiblockValidator.ValidationResult> result = definition.findMatch(level, pos);
            if (result.isPresent() && result.get().success()) {
                MultiblockValidator.ValidationResult validation = result.get();

                if (hasMultiblockConflict(level, pos, definition, validation.facing(), validation.mirrored())) {
                    continue;
                }

                if (!isMultiblockActive(pos, definition, validation.facing(), validation.mirrored())) {
                    MultiblockFormedEvent event = new MultiblockFormedEvent(definition, level, pos, validation.facing(), validation.mirrored());
                    MultiblockInfo info = new MultiblockInfo(definition, pos, validation.facing(), validation.mirrored());
                    activeMultiblocks.put(pos, info);

                    if (level instanceof ServerLevel serverLevel) {
                        saveMultiblockToWorldData(serverLevel, pos, info);
                    }

                    if (definition.hasObject()) {
                        Multiblock multiblockObject = definition.createObject();
                        if (multiblockObject != null) {
                            activeMultiblockObjects.put(pos, multiblockObject);
                            multiblockObject.onCreate(level, pos, validation.facing(), validation.mirrored());
                        }
                    }

                    NeoForge.EVENT_BUS.post(event);
                    return Optional.of(event);
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Handle block break events and check for multiblock formation/destruction.
     */
    private static void onBlockBreak(BreakBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        BlockPos changedPos = event.getPos();
        checkForDestroyedMultiblocksBeforeBreak(serverLevel, changedPos);
    }
    
    /**
     * Handle block place events and check for multiblock formation/destruction.
     */
    private static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        BlockPos changedPos = event.getPos();

        checkForDestroyedMultiblocks(serverLevel, changedPos);
        checkAreaForMultiblocks(serverLevel, changedPos);
        notifyMultiblockObjects(serverLevel, changedPos);
    }
    
    /**
     * Handle server tick events to tick multiblock objects.
     */
    private static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().overworld() instanceof ServerLevel serverLevel) {
            tickMultiblockObjects(serverLevel);
        }
    }
    
    /**
     * Handle player interaction events for multiblock interactions.
     */
    private static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos hitPos = event.getPos();
            Player player = event.getEntity();
            InteractionHand hand = event.getHand();
            ItemStack itemInHand = player.getItemInHand(hand);

            InteractionResult result = handlePlayerInteraction(serverLevel, hitPos, player, hand, itemInHand);

            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        }
    }
    
    /**
     * Check if any existing multiblocks will be destroyed by the block break.
     * This checks BEFORE the block is actually broken.
     */
    private static void checkForDestroyedMultiblocksBeforeBreak(ServerLevel level, BlockPos changedPos) {
        var toRemove = new java.util.ArrayList<Map.Entry<BlockPos, MultiblockInfo>>();
        
        for (var entry : activeMultiblocks.entrySet()) {
            BlockPos anchorPos = entry.getKey();
            MultiblockInfo info = entry.getValue();

            if (isPositionInMultiblock(anchorPos, info, changedPos)) {

                if (wouldBreakMultiblock(level, anchorPos, info, changedPos)) {
                    Multiblock multiblockObject = activeMultiblockObjects.remove(anchorPos);
                    if (multiblockObject != null) {
                        multiblockObject.onDestroy(level, anchorPos, info.facing(), info.mirrored());
                    }

                    MultiblockDestroyedEvent destroyEvent = new MultiblockDestroyedEvent(
                        info.definition(), level, anchorPos, info.facing(), info.mirrored()
                    );
                    NeoForge.EVENT_BUS.post(destroyEvent);
                    toRemove.add(entry);
                }
            }
        }

        for (var entry : toRemove) {
            BlockPos pos = entry.getKey();
            activeMultiblocks.remove(pos);
            activeMultiblockObjects.remove(pos);

            if (level instanceof ServerLevel serverLevel) {
                removeMultiblockFromWorldData(serverLevel, pos);
            }
        }
    }
    
    /**
     * Check if any existing multiblocks were destroyed by the block change.
     */
    private static void checkForDestroyedMultiblocks(ServerLevel level, BlockPos changedPos) {
        var toRemove = new java.util.ArrayList<Map.Entry<BlockPos, MultiblockInfo>>();
        
        for (var entry : activeMultiblocks.entrySet()) {
            BlockPos anchorPos = entry.getKey();
            MultiblockInfo info = entry.getValue();

            if (isPositionInMultiblock(anchorPos, info, changedPos)) {
                Optional<MultiblockValidator.ValidationResult> result = info.definition().findMatch(level, anchorPos);

                if (result.isEmpty() || !result.get().success()) {
                    MultiblockDestroyedEvent destroyEvent = new MultiblockDestroyedEvent(
                        info.definition(), level, anchorPos, info.facing(), info.mirrored()
                    );
                    NeoForge.EVENT_BUS.post(destroyEvent);
                    toRemove.add(entry);
                }
            }
        }

        for (var entry : toRemove) {
            BlockPos pos = entry.getKey();
            activeMultiblocks.remove(pos);
            activeMultiblockObjects.remove(pos);
            removeMultiblockFromWorldData(level, pos);
        }
    }
    
    /**
     * Check the area around a changed position for new multiblocks.
     * Only checks positions that could potentially be anchor points for multiblocks.
     */
    private static void checkAreaForMultiblocks(ServerLevel level, BlockPos centerPos) {
        int checkRadius = 2;
        
        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int y = -checkRadius; y <= checkRadius; y++) {
                for (int z = -checkRadius; z <= checkRadius; z++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    if (activeMultiblocks.containsKey(checkPos)) {
                        continue;
                    }

                    if (level.getBlockState(checkPos).isAir()) {
                        continue;
                    }
                    
                    checkForMultiblock(level, checkPos);
                }
            }
        }
    }
    
    /**
     * Check if a position is within a multiblock's bounds by checking the actual layout pattern.
     */
    private static boolean isPositionInMultiblock(BlockPos anchorPos, MultiblockInfo info, BlockPos checkPos) {
        Layout layout = info.definition().layout();
        BlockPos anchorOffset = layout.anchorOffset();
        BlockPos origin = anchorPos.subtract(anchorOffset);
        BlockPos relativePos = checkPos.subtract(origin);
        BlockPos layoutPos = RotationUtil.rotateAndMirrorInverse(
            relativePos.getX(), relativePos.getY(), relativePos.getZ(), 
            info.facing(), info.mirrored()
        );


        if (layoutPos.getX() < 0 || layoutPos.getX() >= layout.width() ||
            layoutPos.getY() < 0 || layoutPos.getY() >= layout.height() ||
            layoutPos.getZ() < 0 || layoutPos.getZ() >= layout.depth()) {
            return false;
        }

        char layoutChar = layout.getCharAt(layoutPos.getX(), layoutPos.getY(), layoutPos.getZ());

        return layoutChar != ' ' && (layout.anchorChar().isEmpty() || layoutChar != layout.anchorChar().get());
    }
    
    /**
     * Check if any position in the given multiblock layout would conflict with existing multiblocks.
     * Returns true if there's a conflict (meaning the multiblock cannot be formed).
     */
    private static boolean hasMultiblockConflict(Level level, BlockPos anchorPos, MultiblockDefinition definition, Direction facing, boolean mirrored) {
        Layout layout = definition.layout();
        BlockPos anchorOffset = layout.anchorOffset();
        BlockPos origin = anchorPos.subtract(anchorOffset);

        for (int y = 0; y < layout.height(); y++) {
            for (int z = 0; z < layout.depth(); z++) {
                for (int x = 0; x < layout.width(); x++) {
                    char layoutChar = layout.getCharAt(x, y, z);

                    if (layoutChar == ' ' || (layout.anchorChar().isPresent() && layoutChar == layout.anchorChar().get())) {
                        continue;
                    }

                    BlockPos layoutPos = new BlockPos(x, y, z);
                    BlockPos worldPos = origin.offset(RotationUtil.rotateAndMirror(layoutPos.getX(), layoutPos.getY(), layoutPos.getZ(), facing, mirrored));

                    if (isPositionInAnyMultiblock(level, worldPos)) {
                        return true; // Conflict found
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if a position is part of any existing multiblock.
     */
    private static boolean isPositionInAnyMultiblock(Level level, BlockPos pos) {
        for (MultiblockInfo info : activeMultiblocks.values()) {
            if (isPositionInMultiblock(info.anchorPos(), info, pos)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if breaking a block at the given position would make the multiblock invalid.
     * This simulates the block break and validates the multiblock.
     */
    private static boolean wouldBreakMultiblock(ServerLevel level, BlockPos anchorPos, MultiblockInfo info, BlockPos breakPos) {
        var currentState = level.getBlockState(breakPos);

        level.setBlock(breakPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 0);
        
        try {
            Optional<MultiblockValidator.ValidationResult> result = info.definition().findMatch(level, anchorPos);
            return result.isEmpty() || !result.get().success();
        } finally {
            level.setBlock(breakPos, currentState, 0);
        }
    }
    
    /**
     * Check if a multiblock is already being tracked.
     */
    private static boolean isMultiblockActive(BlockPos pos, MultiblockDefinition definition, Direction facing, boolean mirrored) {
        MultiblockInfo info = activeMultiblocks.get(pos);
        return info != null && 
               info.definition().equals(definition) && 
               info.facing() == facing && 
               info.mirrored() == mirrored;
    }
    
    /**
     * Get all currently active multiblocks.
     */
    public static Map<BlockPos, MultiblockInfo> getActiveMultiblocks() {
        return new HashMap<>(activeMultiblocks);
    }
    
    /**
     * Clear all tracked multiblocks (useful for server restart, etc.).
     */
    public static void clearAll() {
        activeMultiblocks.clear();
        activeMultiblockObjects.clear();
    }
    
    /**
     * Ticks all active multiblock objects.
     */
    private static void tickMultiblockObjects(ServerLevel level) {
        for (Map.Entry<BlockPos, Multiblock> entry : activeMultiblockObjects.entrySet()) {
            BlockPos anchorPos = entry.getKey();
            Multiblock multiblockObject = entry.getValue();
            
            if (multiblockObject.shouldTick()) {
                MultiblockInfo info = activeMultiblocks.get(anchorPos);
                if (info != null) {
                    multiblockObject.onTick(level, anchorPos, info.facing(), info.mirrored());
                }
            }
        }
    }
    
    /**
     * Notifies multiblock objects about block changes.
     */
    private static void notifyMultiblockObjects(Level level, BlockPos changedPos) {
        for (Map.Entry<BlockPos, Multiblock> entry : activeMultiblockObjects.entrySet()) {
            BlockPos anchorPos = entry.getKey();
            Multiblock multiblockObject = entry.getValue();
            
            // Get the multiblock info to check bounds properly
            MultiblockInfo info = activeMultiblocks.get(anchorPos);
            if (info != null && isPositionInMultiblock(anchorPos, info, changedPos)) {
                multiblockObject.onBlockChange(level, anchorPos, info.facing(), info.mirrored(), changedPos);
            }
        }
    }
    
    /**
     * Handles player interaction with multiblocks.
     * This should be called when a player right-clicks on a block that might be part of a multiblock.
     * 
     * @param level The level where the interaction occurred
     * @param hitPos The position that was clicked
     * @param player The player who interacted
     * @param hand The hand the player used
     * @param itemInHand The item the player was holding
     * @return The result of the interaction, or PASS if no multiblock was found
     */
    public static InteractionResult handlePlayerInteraction(Level level, BlockPos hitPos, Player player, InteractionHand hand, ItemStack itemInHand) {
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }

        for (Map.Entry<BlockPos, MultiblockInfo> entry : activeMultiblocks.entrySet()) {
            BlockPos anchorPos = entry.getKey();
            MultiblockInfo info = entry.getValue();

            if (isPositionInMultiblock(anchorPos, info, hitPos)) {
                Multiblock multiblockObject = activeMultiblockObjects.get(anchorPos);
                if (multiblockObject != null) {
                    InteractionResult result = multiblockObject.onInteract(
                        level, anchorPos, info.facing(), info.mirrored(), 
                        player, hand, itemInHand, hitPos
                    );

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
            }
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * Handle world load event to restore multiblocks from saved data.
     */
    public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            loadMultiblocksFromWorldData(level);
        }
    }
    
    /**
     * Save a multiblock to world data.
     */
    private static void saveMultiblockToWorldData(ServerLevel level, BlockPos pos, MultiblockInfo info) {
        MultiblockWorldData worldData = MultiblockWorldData.get(level);
        worldData.addMultiblock(pos, info);
    }
    
    /**
     * Remove a multiblock from world data.
     */
    private static void removeMultiblockFromWorldData(ServerLevel level, BlockPos pos) {
        MultiblockWorldData worldData = MultiblockWorldData.get(level);
        worldData.removeMultiblock(pos);
    }
    
    /**
     * Load all multiblocks from world data and validate them.
     */
    private static void loadMultiblocksFromWorldData(ServerLevel level) {
        MultiblockWorldData worldData = MultiblockWorldData.get(level);
        
        var snapshot = new ArrayList<>(worldData.getMultiblocks().entrySet());
        var toRemove = new ArrayList<BlockPos>();
        for (Map.Entry<BlockPos, MultiblockInfo> entry : snapshot) {
            BlockPos pos = entry.getKey();
            MultiblockInfo info = entry.getValue();

            Optional<MultiblockValidator.ValidationResult> result = info.definition().findMatch(level, pos);
            if (result.isPresent() && result.get().success()) {
                activeMultiblocks.put(pos, info);

                if (info.definition().hasObject()) {
                    Multiblock multiblockObject = info.definition().createObject();
                    if (multiblockObject != null) {
                        activeMultiblockObjects.put(pos, multiblockObject);
                        multiblockObject.onCreate(level, pos, info.facing(), info.mirrored());
                    }
                }
            } else {
                // Multiblock is no longer valid, schedule removal after iteration
                toRemove.add(pos);
            }
        }

        for (BlockPos pos : toRemove) {
            worldData.removeMultiblock(pos);
        }
    }
}
