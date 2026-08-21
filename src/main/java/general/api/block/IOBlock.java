package general.api.block;

import general.api.block.util.IPickaxe;
import general.api.model.IMachineModel;
import general.api.tag.CoreTags;
import lombok.Getter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IOBlock<T extends BlockEntity> extends BaseBlock implements EntityBlock, BlockEntityTypeOwner<T>, IWrenchable, IMachineModel, IPickaxe {

	@Getter
	private final IOMode mode;

	@Getter
	private final IOType type;

	private BlockEntityType<T> blockEntityType;

	public IOBlock (Properties properties, IOMode mode, IOType type) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON));
		this.mode = mode;
		this.type = type;
	}

	@Override
	public void setBlockEntity (Class<T> blockEntityClass, BlockEntityType<T> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity (@NonNull BlockPos pos, @NonNull BlockState state) {
		if (blockEntityType == null) throw new IllegalStateException("I/O block entity type has not been bound yet");
		return blockEntityType.create(pos, state);
	}

	public enum IOType {
		ITEM,
		FLUID,
		POWER,
		NETWORK,
		ANY
	}

	public enum IOMode {
		INPUT,
		OUTPUT,
		ANY
	}

	@Override
	public TagKey<Block> getMiningTier () {
		return BlockTags.NEEDS_IRON_TOOL;
	}

	@Override
	public List<TagKey<Block>> getBlockTags () {
		var tags = new ArrayList<>(super.getBlockTags());
		tags.add(CoreTags.Blocks.MULTIBLOCK_HATCHES);
		if (type != IOType.ANY) {
			tags.add(switch (type) {
				case ITEM -> CoreTags.Blocks.ITEM_HATCHES;
				case FLUID -> CoreTags.Blocks.FLUID_HATCHES;
				case POWER -> CoreTags.Blocks.POWER_HATCHES;
				case NETWORK -> CoreTags.Blocks.NETWORK_HATCHES;
				case ANY -> throw new IllegalStateException("Handled above");
			});
		}
		if (mode == IOMode.INPUT) tags.add(CoreTags.Blocks.INPUT_HATCHES);
		if (mode == IOMode.OUTPUT) tags.add(CoreTags.Blocks.OUTPUT_HATCHES);
		return List.copyOf(tags);
	}
}
