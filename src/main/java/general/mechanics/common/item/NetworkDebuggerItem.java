package general.mechanics.common.item;

import general.api.capabilities.GeneralCapabilities;
import general.api.network.INetworkInterface;
import general.api.network.NetworkEndpoint;
import general.api.network.util.NetworkHelper;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.CableBlockEntity;
import guideme.GuidesCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class NetworkDebuggerItem extends Item {

	private static final Identifier GUIDE_ID = Resource.getMainMod("guide");

	public NetworkDebuggerItem (Properties properties) {
		super(properties);
	}

	@Override
	public @NonNull InteractionResult use (@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
		GuidesCommon.openGuide(player, GUIDE_ID);
		return super.use(level, player, hand);
	}

	@Override
	public @NonNull InteractionResult useOn (UseOnContext context) {
		var level = context.getLevel();

		if (level.isClientSide()) return InteractionResult.SUCCESS;
		if (context.getPlayer() == null) return InteractionResult.PASS;

		var origin = context.getClickedPos();
		if (!context.getPlayer().isCrouching() || !isNetworkBlock(level, origin)) {
			//context.getPlayer().sendSystemMessage(Component.literal("No network block found at " + origin)); // TODO: Probably wont need this going further.
			return InteractionResult.SUCCESS;
		}

		Set<BlockPos> network = findNetwork(level, origin);
		context.getPlayer().sendSystemMessage(Component.literal("\u00A76Network contains \u00A7e" + network.size() + " \u00A76block" + (network.size() == 1 ? "" : "s") + ":"));

		for (var pos : network) {
			var state = level.getBlockState(pos);
			var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
			INetworkInterface networkInterface = getInterface(level, pos);
			String services = networkInterface == null ? "none" : networkInterface.getNetworkNode().getServices().getAll().stream().map(service -> service.getType().toString()).sorted().reduce((first, second) -> first + ", " + second).orElse("none");
			context.getPlayer().sendSystemMessage(Component.literal("\u00A77 - \u00A7f" + id + " \u00A78[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] \u00A77services: \u00A7b" + services));

			if (networkInterface != null) {
				for (NetworkEndpoint endpoint : networkInterface.getNetworkEndpoints()) {
					var endpointState = level.getBlockState(endpoint.pos());
					var endpointId = BuiltInRegistries.BLOCK.getKey(endpointState.getBlock());
					var endpointPos = endpoint.pos();
					context.getPlayer().sendSystemMessage(Component.literal("\u00A78   -> \u00A7f" + endpointId + " \u00A78[" + endpointPos.getX() + ", " + endpointPos.getY() + ", " + endpointPos.getZ() + "]"));
				}
			}
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

				if (visited.contains(neighbor) || !NetworkHelper.canConnect(level, current, direction)) {
					continue;
				}

				visited.add(neighbor);
				queue.addLast(neighbor);
			}
		}

		return networkBlocks;
	}

	private static boolean isNetworkBlock (Level level, BlockPos pos) {
		return getInterface(level, pos) != null;
	}

	private static INetworkInterface getInterface (Level level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			INetworkInterface networkInterface = level.getCapability(GeneralCapabilities.NETWORK_HANDLER_BLOCK, pos, direction);
			if (networkInterface != null) {
				return networkInterface;
			}
		}
		return null;
	}
}
