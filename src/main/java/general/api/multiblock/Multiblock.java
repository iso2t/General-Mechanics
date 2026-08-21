package general.api.multiblock;

import java.util.*;

public record Multiblock(MultiblockPattern pattern, List<MultiblockHatchDefinition> hatches) {

	public Multiblock {
		Objects.requireNonNull(pattern, "pattern");
		Objects.requireNonNull(hatches, "hatches");
		hatches = List.copyOf(hatches);

		Set<HatchKey> keys = new HashSet<>();
		for (MultiblockHatchDefinition hatch : hatches) {
			Objects.requireNonNull(hatch, "hatch definition");
			if (!keys.add(hatch.key())) throw new IllegalArgumentException("Duplicate multiblock hatch key '" + hatch.key() + "'");
		}
	}

	public Multiblock (MultiblockPattern pattern) {
		this(pattern, List.of());
	}

	public Optional<MultiblockHatchDefinition> hatch (HatchKey key) {
		Objects.requireNonNull(key, "key");
		return hatches.stream().filter(hatch -> hatch.key().equals(key)).findFirst();
	}

	public static Builder builder (MultiblockPattern pattern) {
		return new Builder(pattern);
	}

	public static final class Builder {

		private final MultiblockPattern               pattern;
		private final List<MultiblockHatchDefinition> hatches = new ArrayList<>();

		private Builder (MultiblockPattern pattern) {
			this.pattern = Objects.requireNonNull(pattern, "pattern");
		}

		public Builder hatch (MultiblockHatchDefinition hatch) {
			hatches.add(Objects.requireNonNull(hatch, "hatch"));
			return this;
		}

		public Multiblock build () {
			return new Multiblock(pattern, hatches);
		}
	}
}
