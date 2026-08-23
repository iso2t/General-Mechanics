package general.api.multiblock;

import general.api.definitions.BlockDefinition;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	@Getter
	private final Set<Character> uniformSymbols;

	public MultiblockPattern (Map<Character, MultiblockElement> palette, List<List<String>> layers, BlockPos anchor) {
		this(palette, layers, anchor, Set.of());
	}

	public MultiblockPattern (Map<Character, MultiblockElement> palette, List<List<String>> layers, BlockPos anchor, Set<Character> uniformSymbols) {
		this.palette = Map.copyOf(palette);
		this.layers = List.copyOf(layers);
		this.anchor = anchor;
		this.uniformSymbols = Set.copyOf(uniformSymbols);
		for (char symbol : this.uniformSymbols) {
			if (!this.palette.containsKey(symbol)) throw new IllegalArgumentException("Uniform multiblock symbol is not defined: '" + symbol + "'");
		}

		this.height = layers.size();
		this.depth = layers.getFirst().size();
		this.width = layers.getFirst().getFirst().length();
	}

	public MultiblockElement getElementAt (int x, int y, int z) {
		var symbol = getSymbolAt(x, y, z);

		if (symbol == ' ') return null;
		return getPalette().get(symbol);
	}

	public char getSymbolAt (int x, int y, int z) {
		return getLayers().get(y).get(z).charAt(x);
	}

	/**
	 * Resolves every occurrence of a pattern symbol into world coordinates for a
	 * particular controller anchor and orientation.
	 */
	public List<BlockPos> getWorldPositions (char symbol, BlockPos worldAnchor, Direction facing) {
		Objects.requireNonNull(worldAnchor, "worldAnchor");
		Objects.requireNonNull(facing, "facing");
		if (!palette.containsKey(symbol)) throw new IllegalArgumentException("Undefined multiblock symbol: '" + symbol + "'");

		var positions = new ArrayList<BlockPos>();
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++) {
					if (getSymbolAt(x, y, z) == symbol) positions.add(getWorldPosition(worldAnchor, facing, x, y, z));
				}
			}
		}
		return List.copyOf(positions);
	}

	public BlockPos getWorldPosition (BlockPos worldAnchor, Direction facing, int x, int y, int z) {
		Objects.requireNonNull(worldAnchor, "worldAnchor");
		Objects.requireNonNull(facing, "facing");
		int relativeX = x - anchor.getX();
		int relativeY = y - anchor.getY();
		int relativeZ = z - anchor.getZ();
		return switch (facing) {
			case NORTH -> worldAnchor.offset(relativeX, relativeY, relativeZ);
			case SOUTH -> worldAnchor.offset(-relativeX, relativeY, -relativeZ);
			case EAST -> worldAnchor.offset(-relativeZ, relativeY, relativeX);
			case WEST -> worldAnchor.offset(relativeZ, relativeY, -relativeX);
			default -> throw new IllegalArgumentException("Multiblock facing must be horizontal");
		};
	}

	public static Builder builder () {
		return new Builder();
	}

	public static final class Builder {

		private final Map<Character, MultiblockElement> palette = new HashMap<>();

		private final List<List<String>> layers         = new ArrayList<>();
		private final Set<Character>     uniformSymbols = new HashSet<>();

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

		/**
		 * Defines a symbol whose positions must each match {@code element} and must
		 * all contain the same block type in a valid structure.
		 */
		public Builder whereUniform (char symbol, MultiblockElement element) {
			where(symbol, element);
			uniformSymbols.add(symbol);
			return this;
		}

		public Builder whereUniform (char symbol, Block block) {
			return whereUniform(symbol, MultiblockElement.block(block));
		}

		public Builder whereUniform (char symbol, BlockDefinition<? extends Block> block) {
			return whereUniform(symbol, MultiblockElement.block(block));
		}

		public Builder whereUniform (char symbol, TagKey<Block> tag) {
			return whereUniform(symbol, MultiblockElement.tag(tag));
		}

		public Builder whereUniform (char symbol, TagKey<Block> tag, Block constructionBlock) {
			return whereUniform(symbol, MultiblockElement.tag(tag, constructionBlock));
		}

		public Builder whereUniform (char symbol, TagKey<Block> tag, BlockDefinition<? extends Block> constructionBlock) {
			return whereUniform(symbol, MultiblockElement.tag(tag, constructionBlock));
		}

		public Builder whereUniform (char symbol, TagKey<Block> tag, Supplier<? extends Block> constructionBlock) {
			return whereUniform(symbol, MultiblockElement.tag(tag, constructionBlock));
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

			return new MultiblockPattern(palette, layers, anchor, uniformSymbols);
		}
	}

}
