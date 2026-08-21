package general.api.multiblock;

import general.api.definitions.BlockDefinition;
import general.api.network.service.NetworkServiceType;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.ResourceSlotKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public record MultiblockHatchDefinition(HatchKey key, MultiblockHatchMatcher matcher, HatchCount count, HatchAccess access) {

	public MultiblockHatchDefinition {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(matcher, "matcher");
		Objects.requireNonNull(count, "count");
		Objects.requireNonNull(access, "access");
	}

	public static Builder builder (String key, MultiblockHatchMatcher matcher) {
		return new Builder(HatchKey.of(key), matcher);
	}

	public static Builder builder (String key, Block block) {
		return builder(key, MultiblockHatchMatcher.block(block));
	}

	public static Builder builder (String key, BlockDefinition<? extends Block> block) {
		return builder(key, MultiblockHatchMatcher.block(block));
	}

	public static Builder builder (String key, TagKey<Block> tag) {
		return builder(key, MultiblockHatchMatcher.tag(tag));
	}

	public static final class Builder {

		private final HatchKey               key;
		private final MultiblockHatchMatcher matcher;
		private final HatchAccess.Builder    access = HatchAccess.builder();
		private       HatchCount             count  = HatchCount.any();

		private Builder (HatchKey key, MultiblockHatchMatcher matcher) {
			this.key = Objects.requireNonNull(key, "key");
			this.matcher = Objects.requireNonNull(matcher, "matcher");
		}

		public Builder count (HatchCount count) {
			this.count = Objects.requireNonNull(count, "count");
			return this;
		}

		public Builder count (int minimum, int maximum) {
			return count(HatchCount.between(minimum, maximum));
		}

		public Builder itemInsert (ResourceSlotKey... slots) {
			access.itemInsert(slots);
			return this;
		}

		public Builder itemExtract (ResourceSlotKey... slots) {
			access.itemExtract(slots);
			return this;
		}

		public Builder fluidInsert (ResourceSlotKey... slots) {
			access.fluidInsert(slots);
			return this;
		}

		public Builder fluidExtract (ResourceSlotKey... slots) {
			access.fluidExtract(slots);
			return this;
		}

		public Builder energy (ResourceIoMode mode) {
			access.energy(mode);
			return this;
		}

		public Builder network (NetworkServiceType<?>... services) {
			access.network(services);
			return this;
		}

		public MultiblockHatchDefinition build () {
			return new MultiblockHatchDefinition(key, matcher, count, access.build());
		}
	}
}
