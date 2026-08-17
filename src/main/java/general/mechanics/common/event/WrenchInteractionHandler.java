package general.mechanics.common.event;

import general.api.block.IWrenchable;
import general.api.rotation.IRotatableBlock;
import general.mechanics.GenMech;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Applies the shared wrench contract to every item in NeoForge's wrench tag. */
@EventBusSubscriber(modid = GenMech.MOD_ID)
public final class WrenchInteractionHandler {

	private WrenchInteractionHandler () {
	}

	@SubscribeEvent
	public static void onRightClickBlock (PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();
		ItemStack wrench = player.getItemInHand(event.getHand());
		if (!wrench.is(Tags.Items.TOOLS_WRENCH)) return;

		Level level = event.getLevel();
		BlockState state = level.getBlockState(event.getPos());
		if (!(state.getBlock() instanceof IWrenchable)) return;

		if (level.isClientSide()) {
			if (player.isCrouching() || state.getBlock() instanceof IRotatableBlock) {
				handle(event);
			}
			return;
		}

		if (player.isCrouching()) {
			if (player instanceof ServerPlayer serverPlayer && serverPlayer.gameMode.destroyBlock(event.getPos())) {
				handle(event);
			}
			return;
		}

		if (state.getBlock() instanceof IRotatableBlock rotatableBlock) {
			BlockState rotated = rotatableBlock.rotateBlock(state, false);
			if (!rotated.equals(state)) {
				level.setBlock(event.getPos(), rotated, Block.UPDATE_ALL);
				wrench.hurtAndBreak(1, player, event.getHand().asEquipmentSlot());
				handle(event);
			}
		}
	}

	private static void handle (PlayerInteractEvent.RightClickBlock event) {
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}
}
