package general.mechanics.common.item;

import general.api.capabilities.Capabilities;
import general.api.network.INetworkInterface;
import general.api.network.util.NetworkHelper;
import general.mechanics.common.block.entity.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class NetworkDebuggerItem extends Item {

	public NetworkDebuggerItem (Properties properties) {
		super(properties);
	}

	@Override
	public @NonNull InteractionResult useOn (UseOnContext context) {
		var level = context.getLevel();

		if (level.isClientSide()) return InteractionResult.SUCCESS;
		if (context.getPlayer() == null) return InteractionResult.PASS;

		var origin = context.getClickedPos();
		if (!isNetworkBlock(level, origin)) {
			context.getPlayer().sendSystemMessage(Component.literal("No network block found at " + origin));
			return InteractionResult.SUCCESS;
		}

		Set<BlockPos> network = findNetwork(level, origin);
		context.getPlayer().sendSystemMessage(Component.literal("§6Network contains §e" + network.size() + " §6block" + (network.size() == 1 ? "" : "s") + ":"));

		for (var pos : network) {
			var state = level.getBlockState(pos);
			var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
			context.getPlayer().sendSystemMessage(Component.literal("§7 - §f" + id + " §8[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));
		}

		return InteractionResult.SUCCESS;
	}

	private static Set<BlockPos> findNetwork (Level level, BlockPos origin) {
		Set<BlockPos> visited = new HashSet<>();
		Set<BlockPos> networkBlocks = new LinkedHashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();

		visited.add(origin);
		queue.add(origin);

		while (!queue.isEmpty()) {
			BlockPos current = queue.removeFirst();

			if (!(level.getBlockEntity(current) instanceof CableBlockEntity)) {
				networkBlocks.add(current);
			}

			for (Direction direction : Direction.values()) {
				BlockPos neighbor = current.relative(direction);

				if (visited.contains(neighbor)) {
					continue;
				}

				if (!NetworkHelper.canConnect(level, current, direction)) {
					continue;
				}

				visited.add(neighbor);
				queue.addLast(neighbor);
			}
		}

		return networkBlocks;
	}

	private static boolean isNetworkBlock (Level level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			INetworkInterface networkInterface = level.getCapability(Capabilities.NETWORK_HANDLER_BLOCK, pos, direction);

			if (networkInterface != null && networkInterface.isNetworkEnabled()) {
				return true;
			}
		}

		return false;
	}
}
