package general.mechanics.datagen.tags;

import general.api.item.IItemTagsProvider;
import general.api.item.ToolItem;
import general.api.mod.GenAPI;
import general.api.tag.CoreTags;
import general.mechanics.item.tools.SawItem;
import general.mechanics.item.tools.WireCuttersItem;
import general.mechanics.item.tools.WrenchItem;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class GenItemTagGenerator extends ItemTagsProvider {

	public GenItemTagGenerator (PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, GenAPI.getModId());
	}

	@Override
	protected void addTags (HolderLookup.@NonNull Provider provider) {
		for (var item : GenItems.INSTANCE.getItems()) {
			if (item.get() instanceof ToolItem toolItem) {
				if (toolItem instanceof SawItem) this.tag(CoreTags.Items.SAWS).add(toolItem);
				if (toolItem instanceof WireCuttersItem) this.tag(CoreTags.Items.WIRE_CUTTERS).add(toolItem);
				if (toolItem instanceof WrenchItem) this.tag(Tags.Items.TOOLS_WRENCH).add(toolItem);
				this.tag(Tags.Items.TOOLS).add(toolItem);
			}

			if (item.get() instanceof IItemTagsProvider tagsProvider) {
				for (var tag : tagsProvider.getItemTags()) {
					this.tag(tag).add(item.get());
				}
			}
		}

		this.tag(ItemTags.PLANKS).add(GenBlocks.RUBBER_PLANKS.asItem());

	}

}
