package general.api.multiblock;

import general.api.definitions.MultiblockDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Implemented by a block entity that owns the formed state of a multiblock.
 * Validation, discovery, and runtime indexing remain the responsibility of
 * {@link MultiblockHandler}.
 */
public interface MultiblockController {

	String FORMED_TAG = "formed";

	MultiblockDefinition getMultiblockDefinition ();

	/**
	 * The controller's anchor in the world. Implementing block entities must return
	 * their own block position; the anchor symbol in the definition is validated at
	 * this exact position.
	 */
	BlockPos getMultiblockPosition ();

	/**
	 * The pattern's horizontal orientation. This is the direction the structure
	 * extends from its anchor and may be the opposite of a controller's visual front.
	 */
	Direction getMultiblockFacing ();

	boolean isMultiblockFormed ();

	void setMultiblockFormed (boolean formed);

	default void onMultiblockFormed (MultiblockInstance instance) {
	}

	default void onMultiblockInvalidated () {
	}

	/**
	 * Validates on interaction, reports malformed structures, and delegates valid
	 * interactions to {@link #onFormedMultiblockUse(Player, BlockHitResult, MultiblockInstance)}.
	 */
	default InteractionResult useMultiblock (Player player, BlockHitResult hitResult) {
		if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || !isMultiblockFormed()) return InteractionResult.PASS;
		if (!(this instanceof BlockEntity blockEntity)) return InteractionResult.PASS;
		Level level = blockEntity.getLevel();
		if (level == null) return InteractionResult.PASS;
		if (level.isClientSide()) return InteractionResult.SUCCESS;
		if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;

		MultiblockValidationResult result = MultiblockHandler.revalidate(serverLevel, this);
		if (result.valid()) return onFormedMultiblockUse(player, hitResult, result.instance());

		player.sendSystemMessage(malformedMessage(result));
		return InteractionResult.CONSUME;
	}

	/**
	 * Override in a controller to open a menu or perform other valid-machine behavior.
	 */
	default InteractionResult onFormedMultiblockUse (Player player, BlockHitResult hitResult, MultiblockInstance instance) {
		return InteractionResult.SUCCESS;
	}

	private static Component malformedMessage (MultiblockValidationResult result) {
		if (result.unloaded()) return Component.literal("§eMultiblock cannot be validated because a required chunk is unloaded.");
		BlockPos failed = result.failedPosition();
		if (failed == null) return Component.literal("§cMultiblock is malformed.");
		return Component.literal("§cMultiblock is malformed at [" + failed.getX() + ", " + failed.getY() + ", " + failed.getZ() + "].");
	}
}
