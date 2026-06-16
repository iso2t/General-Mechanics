package general.mechanics.datagen.tags;

import general.api.item.ToolItem;
import general.api.item.materials.*;
import general.api.item.plastic.PlasticType;
import general.api.mod.GenAPI;
import general.api.tag.CoreTags;
import general.mechanics.item.tools.*;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenParts;
import general.mechanics.registries.GenTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
		for (var element : GenParts.INSTANCE.getItems()) {
			if (element.get() instanceof IngotItem) {
				this.tag(Tags.Items.INGOTS).add(element.asItem());
			} else if (element.get() instanceof NuggetItem) {
				this.tag(Tags.Items.NUGGETS).add(element.asItem());
			} else if (element.get() instanceof RawItem) {
				this.tag(Tags.Items.RAW_MATERIALS).add(element.asItem());
			} else if (element.get() instanceof DustItem) {
				this.tag(Tags.Items.DUSTS).add(element.asItem());
			} else if (element.get() instanceof PlateItem) {
				this.tag(CoreTags.Items.PLATES).add(element.asItem());
			} else if (element.get() instanceof PileItem) {
				this.tag(CoreTags.Items.PILES).add(element.asItem());
			} else if (element.get() instanceof RodItem) {
				this.tag(CoreTags.Items.RODS).add(element.asItem());
			}
		}

		// Add all colored plastics to the general plastic tag
		for (var plastic : GenItems.getAllColoredPlastics()) {
			this.tag(CoreTags.Items.PLASTIC).add(plastic.asItem());
		}

		// Add all colored plastic blocks to the general plastic blocks tag
		for (var plasticBlock : GenBlocks.getAllColoredPlasticBlocks()) {
			this.tag(CoreTags.Items.PLASTIC_BLOCKS).add(plasticBlock.asItem());
		}

		// Add colored variants to their specific plastic type tags
		for (PlasticType plasticType : PlasticType.values()) {
			TagKey<Item> plasticTypeTag = getPlasticTypeTag(plasticType);
			for (var plastic : GenItems.getColoredPlasticsForType(plasticType)) {
				this.tag(plasticTypeTag).add(plastic.asItem());
			}
		}

		for (var tool : GenTools.INSTANCE.getItems()) {
			if (tool.get() instanceof ToolItem toolItem) {
				if (toolItem instanceof FileItem) this.tag(CoreTags.Items.FILES).add(toolItem);
				if (toolItem instanceof FlatheadScrewdriverItem)
					this.tag(CoreTags.Items.FLATHEAD_SCREWDRIVERS).add(toolItem);
				if (toolItem instanceof HammerItem) this.tag(CoreTags.Items.HAMMERS).add(toolItem);
				if (toolItem instanceof PhillipsScrewdriverItem)
					this.tag(CoreTags.Items.PHILLIPS_SCREWDRIVERS).add(toolItem);
				if (toolItem instanceof SocketDriverItem) this.tag(CoreTags.Items.SOCKET_DRIVERS).add(toolItem);
				if (toolItem instanceof SawItem) this.tag(CoreTags.Items.SAWS).add(toolItem);
				if (toolItem instanceof WireCuttersItem) this.tag(CoreTags.Items.WIRE_CUTTERS).add(toolItem);
				if (toolItem instanceof WrenchItem) this.tag(CoreTags.Items.WRENCHES).add(toolItem);
				this.tag(Tags.Items.TOOLS).add(toolItem);
			}

		}

		this.tag(CoreTags.Items.BOLTS)
				.add(GenParts.BOLT.asItem());

		this.tag(CoreTags.Items.SCREWS)
				.add(GenParts.SCREW.asItem());

		this.tag(CoreTags.Items.CARBON)
				.add(Items.COAL)
				.add(Items.CHARCOAL);

	}

	/**
	 * Get the appropriate tag for a given plastic type
	 */
	private TagKey<Item> getPlasticTypeTag(PlasticType plasticType) {
		return switch (plasticType) {
			case POLYETHYLENE -> CoreTags.Items.POLYETHYLENE;
			case POLYPROPYLENE -> CoreTags.Items.POLYPROPYLENE;
			case POLYSTYRENE -> CoreTags.Items.POLYSTYRENE;
			case POLYVINYL_CHLORIDE -> CoreTags.Items.POLYVINYL_CHLORIDE;
			case POLYETHYLENE_TEREPHTHALATE -> CoreTags.Items.POLYETHYLENE_TEREPHTHALATE;
			case ACRYLONITRILE_BUTADIENE_STYRENE -> CoreTags.Items.ACRYLONITRILE_BUTADIENE_STYRENE;
			case POLYCARBONATE -> CoreTags.Items.POLYCARBONATE;
			case NYLON -> CoreTags.Items.NYLON;
			case POLYURETHANE -> CoreTags.Items.POLYURETHANE;
			case POLYTETRAFLUOROETHYLENE -> CoreTags.Items.POLYTETRAFLUOROETHYLENE;
			case POLYETHERETHERKETONE -> CoreTags.Items.POLYETHERETHERKETONE;
		};
	}
}
