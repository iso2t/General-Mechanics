package general.api.block;

import general.api.block.util.ILitProvider;
import general.api.resources.Resource;
import general.api.rotation.IRotatableBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseBlock extends Block implements IBlockTagsProvider {

	protected BaseBlock (Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		if (this instanceof IRotatableBlock rotatable) {
			rotatable.getRotationStrategy().addProperties(builder);
		}
		if (this instanceof ILitProvider) {
			builder.add(ILitProvider.LIT);
		}
	}

	@Override
	public BlockState getStateForPlacement (BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state != null && this instanceof IRotatableBlock rotatable) {
			return rotatable.getRotationStrategy().getStateForPlacement(state, context);
		}
		return state;
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
