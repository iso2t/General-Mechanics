package general.api.multiblock;

import general.api.definitions.BlockDefinition;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Supplier;

public final class MultiblockPattern {

	@Getter
	private final Map<Character, MultiblockElement> palette;

	@Getter
	private final List<List<String>> layers;

	@Getter
	private final int width;

	@Getter
	private final int height;

	@Getter
	private final int depth;

	@Getter
	private final BlockPos anchor;

	public MultiblockPattern (Map<Character, MultiblockElement> palette, List<List<String>> layers, BlockPos anchor) {
		this.palette = Map.copyOf(palette);
		this.layers = List.copyOf(layers);
		this.anchor = anchor;

		this.height = layers.size();
		this.depth = layers.getFirst().size();
		this.width = layers.getFirst().getFirst().length();
	}

	public MultiblockElement getElementAt (int x, int y, int z) {
		var symbol = getLayers().get(y).get(z).charAt(x);

		if (symbol == ' ') return null;
		return getPalette().get(symbol);
	}

	public static Builder builder () {
		return new Builder();
	}

	public static final class Builder {

		private final Map<Character, MultiblockElement> palette = new HashMap<>();

		private final List<List<String>> layers = new ArrayList<>();

		private Character anchorSymbol;

		public Builder where (char symbol, MultiblockElement element) {
			if (symbol == ' ') {
				throw new IllegalArgumentException("Space is reserved for ignored positions.");
			}

			palette.put(symbol, Objects.requireNonNull(element, "element"));

			return this;
		}

		public Builder where (char symbol, Block block) {
			return where(symbol, MultiblockElement.block(block));
		}

		public Builder whereHatchable (char symbol, MultiblockElement casing) {
			return where(symbol, MultiblockElement.hatchable(casing));
		}

		public Builder whereHatchable (char symbol, Block casing) {
			return whereHatchable(symbol, MultiblockElement.block(casing));
		}

		public Builder whereHatchable (char symbol, BlockDefinition<? extends Block> casing) {
			return whereHatchable(symbol, MultiblockElement.block(casing));
		}

		public Builder whereHatchable (char symbol, TagKey<Block> casing) {
			return whereHatchable(symbol, MultiblockElement.tag(casing));
		}

		public Builder whereHatchable (char symbol, TagKey<Block> casing, Block constructionBlock) {
			return whereHatchable(symbol, MultiblockElement.tag(casing, constructionBlock));
		}

		public Builder whereHatchable (char symbol, TagKey<Block> casing, BlockDefinition<? extends Block> constructionBlock) {
			return whereHatchable(symbol, MultiblockElement.tag(casing, constructionBlock));
		}

		/**
		 * Defines a symbol that accepts any block in the supplied block tag. Tag-only
		 * elements are intentionally not constructible because no tag member can be
		 * selected unambiguously as the representative block.
		 */
		public Builder where (char symbol, TagKey<Block> tag) {
			return where(symbol, MultiblockElement.tag(tag));
		}

		/**
		 * Defines a tagged symbol with an explicit representative block for previews
		 * and assisted construction.
		 */
		public Builder where (char symbol, TagKey<Block> tag, Block constructionBlock) {
			return where(symbol, MultiblockElement.tag(tag, constructionBlock));
		}

		/**
		 * Registry-definition form of {@link #where(char, TagKey, Block)}.
		 */
		public Builder where (char symbol, TagKey<Block> tag, BlockDefinition<? extends Block> constructionBlock) {
			return where(symbol, MultiblockElement.tag(tag, constructionBlock));
		}

		/**
		 * Deferred registry-backed form of {@link #where(char, TagKey, Block)}.
		 */
		public Builder where (char symbol, TagKey<Block> tag, Supplier<? extends Block> constructionBlock) {
			return where(symbol, MultiblockElement.tag(tag, constructionBlock));
		}

		public Builder anchor (char symbol) {
			this.anchorSymbol = symbol;
			return this;
		}

		public Builder layer (String... rows) {
			if (rows.length == 0) {
				throw new IllegalArgumentException("A multiblock layer cannot be empty.");
			}

			layers.add(List.of(rows));

			return this;
		}

		public MultiblockPattern build () {

			if (layers.isEmpty()) {
				throw new IllegalStateException("Multiblock must contain at least one layer.");
			}

			int expectedDepth = layers.getFirst().size();
			int expectedWidth = layers.getFirst().getFirst().length();

			BlockPos anchor = null;

			for (int y = 0; y < layers.size(); y++) {
				List<String> layer = layers.get(y);

				if (layer.size() != expectedDepth) {
					throw new IllegalStateException("Every multiblock layer must have the same depth.");
				}

				for (int z = 0; z < layer.size(); z++) {
					String row = layer.get(z);

					if (row.length() != expectedWidth) {
						throw new IllegalStateException("Every multiblock row must have the same width.");
					}

					for (int x = 0; x < row.length(); x++) {
						char symbol = row.charAt(x);

						if (symbol == ' ') {
							continue;
						}

						if (!palette.containsKey(symbol)) {
							throw new IllegalStateException("Undefined multiblock symbol: '" + symbol + "'");
						}

						if (anchorSymbol != null && symbol == anchorSymbol) {

							if (anchor != null) {
								throw new IllegalStateException("Multiblock anchor occurs more than once.");
							}

							anchor = new BlockPos(x, y, z);
						}
					}
				}
			}

			if (anchorSymbol == null) {
				throw new IllegalStateException("Multiblock has no anchor symbol.");
			}

			if (anchor == null) {
				throw new IllegalStateException("Multiblock anchor symbol '" + anchorSymbol + "' was not found.");
			}

			return new MultiblockPattern(palette, layers, anchor);
		}
	}

}
