package general.mechanics.datagen.tags;

import general.api.block.IBlockTagsProvider;
import general.api.block.util.IAxe;
import general.api.block.util.IPickaxe;
import general.api.block.util.IShovel;
import general.api.mod.GenAPI;
import general.mechanics.registries.GenBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GenBlockTagGenerator extends BlockTagsProvider {

	public GenBlockTagGenerator (PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, GenAPI.getModId());
	}

	@Override
	protected void addTags (HolderLookup.@NonNull Provider provider) {
		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			addTags(block.get(), tagsFor(block.get()));
		}

		for (var family : GenBlocks.LIMESTONE_FAMILIES) {
			addTags(new Block[]{family.slab().get(), family.stairs().get()}, tagsFor(family.base().get()));
		}
	}

	private Set<TagKey<Block>> tagsFor (Block block) {
		var tags = new LinkedHashSet<TagKey<Block>>();
		if (block instanceof IBlockTagsProvider provider) tags.addAll(provider.getBlockTags());
		if (block instanceof IPickaxe pickaxe) {
			tags.add(pickaxe.getRequiredTool());
			tags.add(pickaxe.getMiningTier());
		}
		if (block instanceof IAxe axe) {
			tags.add(axe.getRequiredTool());
			tags.add(axe.getMiningTier());
		}
		if (block instanceof IShovel shovel) {
			tags.add(shovel.getRequiredTool());
			tags.add(shovel.getMiningTier());
		}
		if (block instanceof LeavesBlock) tags.add(BlockTags.LEAVES);
		if (block instanceof SlabBlock) tags.add(BlockTags.SLABS);
		if (block instanceof StairBlock) tags.add(BlockTags.STAIRS);
		return tags;
	}

	private void addTags (Block block, Set<TagKey<Block>> tags) {
		addTags(new Block[]{block}, tags);
	}

	private void addTags (Block[] blocks, Set<TagKey<Block>> tags) {
		for (var tag : tags) this.tag(tag).add(blocks);
	}
}
