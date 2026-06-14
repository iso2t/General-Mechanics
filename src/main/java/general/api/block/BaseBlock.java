package general.api.block;

import general.api.item.IBlockTooltipProvider;
import general.api.resources.Resource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseBlock extends Block implements IBlockTooltipProvider, IBlockTagsProvider {

	protected BaseBlock (Properties properties) {
		super(properties);
	}

	public void addToCreativeTab (CreativeModeTab.Output output) {
		output.accept(this);
	}

	@Override
	public void appendHoverText (@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay toolTip, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag) {
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
