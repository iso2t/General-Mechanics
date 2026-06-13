package general.mechanics.datagen.tags;

import general.mechanics.GM;
import general.mechanics.api.item.element.metallic.*;
import general.mechanics.api.item.plastic.PlasticType;
import general.mechanics.api.item.tools.*;
import general.mechanics.api.tags.CoreTags;
import general.mechanics.api.util.IDataProvider;
import general.mechanics.registries.CoreBlocks;
import general.mechanics.registries.CoreElements;
import general.mechanics.registries.CoreItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CoreItemTagGenerator extends ItemTagsProvider implements IDataProvider {

    public CoreItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, GM.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        for (var element : CoreElements.getElements()) {
            if (element.get() instanceof ElementItem) {
                this.tag(Tags.Items.INGOTS).add(element.asItem());
            } else if (element.get() instanceof ElementNuggetItem) {
                this.tag(Tags.Items.NUGGETS).add(element.asItem());
            } else if (element.get() instanceof ElementRawItem) {
                this.tag(Tags.Items.RAW_MATERIALS).add(element.asItem());
            } else if (element.get() instanceof ElementDustItem) {
                this.tag(Tags.Items.DUSTS).add(element.asItem());
            } else if (element.get() instanceof ElementPlateItem) {
                this.tag(CoreTags.Items.PLATES).add(element.asItem());
            } else if (element.get() instanceof ElementPileItem) {
                this.tag(CoreTags.Items.PILES).add(element.asItem());
            } else if (element.get() instanceof ElementRodItem) {
                this.tag(CoreTags.Items.RODS).add(element.asItem());
            }
        }

        // Add all colored plastics to the general plastic tag
        for (var plastic : CoreItems.getAllColoredPlastics()) {
            this.tag(CoreTags.Items.PLASTIC).add(plastic.asItem());
        }

        // Add all colored plastic blocks to the general plastic blocks tag
        for (var plasticBlock : CoreBlocks.getAllColoredPlasticBlocks()) {
            this.tag(CoreTags.Items.PLASTIC_BLOCKS).add(plasticBlock.asItem());
        }

        // Add colored variants to their specific plastic type tags
        for (PlasticType plasticType : PlasticType.values()) {
            TagKey<Item> plasticTypeTag = getPlasticTypeTag(plasticType);
            for (var plastic : CoreItems.getColoredPlasticsForType(plasticType)) {
                this.tag(plasticTypeTag).add(plastic.asItem());
            }
        }

        for (var tool : CoreItems.getItems()) {
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
                .add(CoreItems.BOLT.asItem());

        this.tag(CoreTags.Items.SCREWS)
                .add(CoreItems.SCREW.asItem());

        this.tag(CoreTags.Items.CARBON)
                .add(Items.COAL)
                .add(Items.CHARCOAL);

        overrideMCTags();

    }

    protected void overrideMCTags() {
        // Integrates our same type items with minecraft's default items.
        this.tag(Tags.Items.INGOTS_IRON)
                .add(CoreElements.IRON_INGOT.asItem());

        this.tag(Tags.Items.INGOTS_GOLD)
                .add(CoreElements.GOLD_INGOT.asItem());

        this.tag(Tags.Items.INGOTS_COPPER)
                .add(CoreElements.COPPER_INGOT.asItem());

        this.tag(Tags.Items.NUGGETS_IRON)
                .add(CoreElements.IRON_INGOT.get().getNuggetItem().asItem());

        this.tag(Tags.Items.NUGGETS_GOLD)
                .add(CoreElements.GOLD_INGOT.get().getNuggetItem().asItem());

        this.tag(Tags.Items.RAW_MATERIALS_IRON)
                .add(CoreElements.IRON_INGOT.get().getRawItem().asItem());

        this.tag(Tags.Items.RAW_MATERIALS_COPPER)
                .add(CoreElements.COPPER_INGOT.get().getRawItem().asItem());

        this.tag(Tags.Items.RAW_MATERIALS_GOLD)
                .add(CoreElements.GOLD_INGOT.get().getRawItem().asItem());
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

    @Override
    public @NotNull String getName() {
        return GM.NAME + " ItemTags";
    }
}
