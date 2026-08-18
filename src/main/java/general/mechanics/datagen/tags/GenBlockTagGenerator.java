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
import net.minecraft.world.level.block.LeavesBlock;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class GenBlockTagGenerator extends BlockTagsProvider {

	public GenBlockTagGenerator (PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, GenAPI.getModId());
	}

	@Override
	protected void addTags (HolderLookup.@NonNull Provider provider) {
		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			if (block.get() instanceof IBlockTagsProvider tagsProvider) {
				for (var tag : tagsProvider.getBlockTags()) {
					this.tag(tag).add(block.get());
				}
			}

			if (block.get() instanceof IPickaxe pickaxe) {
				this.tag(pickaxe.getRequiredTool()).add(block.get());
				this.tag(pickaxe.getMiningTier()).add(block.get());
			}

			if (block.get() instanceof IAxe axe) {
				this.tag(axe.getRequiredTool()).add(block.get());
				this.tag(axe.getMiningTier()).add(block.get());
			}

			if (block.get() instanceof IShovel shovel) {
				this.tag(shovel.getRequiredTool()).add(block.get());
				this.tag(shovel.getMiningTier()).add(block.get());
			}

			if (block.get() instanceof LeavesBlock) {
				this.tag(BlockTags.LEAVES).add(block.get());
			}
		}
	}
}
