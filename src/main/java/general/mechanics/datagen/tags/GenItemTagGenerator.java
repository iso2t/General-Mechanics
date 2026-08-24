package general.mechanics.datagen.tags;

import general.api.item.IItemTagsProvider;
import general.api.item.ToolItem;
import general.api.item.materials.DustItem;
import general.api.item.materials.NuggetItem;
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
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
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
			if (item.get() instanceof DustItem) this.tag(Tags.Items.DUSTS).add(item.get());
			if (item.get() instanceof NuggetItem) this.tag(Tags.Items.NUGGETS).add(item.get());

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
		this.tag(Tags.Items.NUGGETS_COPPER).add(GenItems.COPPER_NUGGET.get());

		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			if (block.get() instanceof SlabBlock) this.tag(ItemTags.SLABS).add(block.asItem());
			if (block.get() instanceof StairBlock) this.tag(ItemTags.STAIRS).add(block.asItem());
		}

	}

}
