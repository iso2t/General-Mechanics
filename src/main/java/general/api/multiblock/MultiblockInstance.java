package general.api.multiblock;

import general.api.definitions.MultiblockDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record MultiblockInstance(MultiblockDefinition definition, BlockPos anchor, Direction facing, Map<BlockPos, MultiblockElement> blocks, Map<BlockPos, HatchKey> hatches) {

	public MultiblockInstance {
		Objects.requireNonNull(definition, "definition");
		anchor = Objects.requireNonNull(anchor, "anchor").immutable();
		Objects.requireNonNull(facing, "facing");
		blocks = immutableMap(blocks, "blocks");
		hatches = immutableMap(hatches, "hatches");
	}

	public MultiblockInstance (MultiblockDefinition definition, BlockPos anchor, Direction facing, Map<BlockPos, MultiblockElement> blocks) {
		this(definition, anchor, facing, blocks, Map.of());
	}

	public HatchKey hatchAt (BlockPos position) {
		return hatches.get(position);
	}

	private static <K, V> Map<K, V> immutableMap (Map<K, V> values, String name) {
		Objects.requireNonNull(values, name);
		var copy = new LinkedHashMap<K, V>();
		values.forEach((key, value) -> copy.put(Objects.requireNonNull(key, name + " key"), Objects.requireNonNull(value, name + " value")));
		return Collections.unmodifiableMap(copy);
	}

}
