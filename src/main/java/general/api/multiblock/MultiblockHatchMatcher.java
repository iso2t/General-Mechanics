package general.api.multiblock;

import general.api.block.IOBlock;
import general.api.definitions.BlockDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Supplier;

@FunctionalInterface
public interface MultiblockHatchMatcher {

	boolean matches (LevelReader level, BlockPos pos, BlockState state, MultiblockHatch hatch);

	default MultiblockHatchMatcher and (MultiblockHatchMatcher other) {
		Objects.requireNonNull(other, "other");
		return (level, pos, state, hatch) -> matches(level, pos, state, hatch) && other.matches(level, pos, state, hatch);
	}

	static MultiblockHatchMatcher block (Block block) {
		Objects.requireNonNull(block, "block");
		return (level, pos, state, hatch) -> state.is(block);
	}

	static MultiblockHatchMatcher block (BlockDefinition<? extends Block> block) {
		Objects.requireNonNull(block, "block");
		return block(block::get);
	}

	static MultiblockHatchMatcher block (Supplier<? extends Block> block) {
		Objects.requireNonNull(block, "block");
		return (level, pos, state, hatch) -> state.is(Objects.requireNonNull(block.get(), "Hatch block supplier returned null"));
	}

	static MultiblockHatchMatcher tag (TagKey<Block> tag) {
		Objects.requireNonNull(tag, "tag");
		return (level, pos, state, hatch) -> state.is(tag);
	}

	static MultiblockHatchMatcher descriptor (IOBlock.IOType type, IOBlock.IOMode mode) {
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(mode, "mode");
		return (level, pos, state, hatch) -> (type == IOBlock.IOType.ANY || hatch.getHatchType() == type) && (mode == IOBlock.IOMode.ANY || hatch.getHatchMode() == mode);
	}
}
