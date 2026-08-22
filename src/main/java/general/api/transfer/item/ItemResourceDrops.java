package general.api.transfer.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Objects;

/**
 * Server-side helpers for releasing the contents of transactional item storage.
 *
 * <p>Each slot is drained through the supplied handler in a root transaction, so
 * normal extraction rules, snapshots, and committed-change callbacks are
 * preserved. The handler should therefore be the owner's unrestricted backing
 * handler rather than a capability view.</p>
 */
public final class ItemResourceDrops {

	private ItemResourceDrops () {
	}

	/**
	 * Extracts and drops every item the handler permits extraction from.
	 * Item resources are split at their intrinsic maximum stack size before they
	 * are passed to Minecraft's normal block-resource drop path.
	 */
	public static void dropContents (ServerLevel level, BlockPos pos, ResourceHandler<ItemResource> handler) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(pos, "pos");
		Objects.requireNonNull(handler, "handler");

		for (int slot = 0; slot < handler.size(); slot++) {
			ItemResource resource = handler.getResource(slot);
			int stored = handler.getAmountAsInt(slot);
			if (resource.isEmpty() || stored <= 0) continue;

			int extracted;
			try (Transaction transaction = Transaction.openRoot()) {
				extracted = handler.extract(slot, resource, stored, transaction);
				if (extracted <= 0) continue;
				transaction.commit();
			}

			int stackLimit = Math.max(1, resource.getMaxStackSize());
			while (extracted > 0) {
				int count = Math.min(extracted, stackLimit);
				ItemStack stack = resource.toStack(count);
				Block.popResource(level, pos, stack);
				extracted -= count;
			}
		}
	}
}
