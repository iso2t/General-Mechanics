package general.api.multiblock;

import general.api.definitions.BlockDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record MultiblockElement(Matcher matcher, Supplier<BlockState> placementStateSupplier) {

	public boolean matches (LevelReader level, BlockPos pos) {
		return matcher.matches(level, pos, level.getBlockState(pos));
	}

	public boolean canConstruct () {
		return placementStateSupplier != null;
	}

	public BlockState placementState () {
		return placementStateSupplier == null ? null : placementStateSupplier.get();
	}

	@FunctionalInterface
	public interface Matcher {
		boolean matches (LevelReader level, BlockPos pos, BlockState state);
	}

	public static MultiblockElement block (Block block) {
		Objects.requireNonNull(block);

		return new MultiblockElement((level, pos, state) -> state.is(block), block::defaultBlockState);
	}

	public static MultiblockElement block (BlockDefinition<? extends Block> block) {
		Objects.requireNonNull(block);

		return new MultiblockElement((level, pos, state) -> state.is(block.get()), () -> block.get().defaultBlockState());
	}

	public static MultiblockElement state (BlockState expected) {
		Objects.requireNonNull(expected);

		return new MultiblockElement((level, pos, state) -> state.equals(expected), () -> expected);
	}

	public static MultiblockElement tag (TagKey<Block> tag) {
		Objects.requireNonNull(tag);

		return new MultiblockElement((level, pos, state) -> state.is(tag), null);
	}

	/**
	 * Matches any member of {@code tag} while using {@code constructionBlock} as
	 * the representative state for previews or assisted construction. The supplied
	 * block should itself be a member of the tag.
	 */
	public static MultiblockElement tag (TagKey<Block> tag, Block constructionBlock) {
		Objects.requireNonNull(tag);
		Objects.requireNonNull(constructionBlock);

		return new MultiblockElement((level, pos, state) -> state.is(tag), constructionBlock::defaultBlockState);
	}

	/**
	 * Registry-definition form of {@link #tag(TagKey, Block)}.
	 */
	public static MultiblockElement tag (TagKey<Block> tag, BlockDefinition<? extends Block> constructionBlock) {
		Objects.requireNonNull(constructionBlock);
		return tag(tag, constructionBlock::get);
	}

	/**
	 * Deferred form of {@link #tag(TagKey, Block)} for registry-backed blocks.
	 * The supplier is resolved only when a representative placement state is
	 * requested, never while the pattern is declared.
	 */
	public static MultiblockElement tag (TagKey<Block> tag, Supplier<? extends Block> constructionBlock) {
		Objects.requireNonNull(tag);
		Objects.requireNonNull(constructionBlock);

		return new MultiblockElement((level, pos, state) -> state.is(tag), () -> Objects.requireNonNull(constructionBlock.get(), "Tagged multiblock construction block supplier returned null").defaultBlockState());
	}

	public static MultiblockElement predicate (Predicate<BlockState> predicate) {
		Objects.requireNonNull(predicate);

		return new MultiblockElement((level, pos, state) -> predicate.test(state), null);
	}

	public static MultiblockElement matcher (Matcher matcher) {
		return new MultiblockElement(Objects.requireNonNull(matcher), null);
	}

	public static MultiblockElement air () {
		return new MultiblockElement((level, pos, state) -> state.isAir(), null);
	}

	public static MultiblockElement any () {
		return new MultiblockElement((level, pos, state) -> true, null);
	}

}
