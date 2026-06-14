package general.mechanics.datagen.tags;

import general.api.block.IBlockTagsProvider;
import general.api.mod.GenAPI;
import general.mechanics.registries.GenBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
		}
	}
}
