package general.api.block;

import general.api.resources.Resource;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseBlock extends Block implements IBlockTagsProvider {

	protected BaseBlock (Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull String toString () {
		String regName = this.getRegistryName() != null ? this.getRegistryName().getPath() : "unregistered";
		return this.getClass().getSimpleName() + "[" + regName + "]";
	}

	@Nullable
	public Identifier getRegistryName () {
		return Resource.getFromBlock(this);
	}

	@Override
	public List<TagKey<Block>> getBlockTags () {
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
	}
}
