package general.mechanics.datagen.tags;

import general.mechanics.GM;
import general.mechanics.api.tags.CoreTags;
import general.mechanics.api.util.IDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static general.mechanics.registries.CoreBlocks.*;
import general.mechanics.registries.CoreBlocks;

public class CoreBlockTagGenerator extends BlockTagsProvider implements IDataProvider {

    public CoreBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, GM.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ICE2.block())
                .add(ICE3.block())
                .add(ICE4.block())
                .add(ICE5.block())
                .add(ICE6.block())
                .add(ICE7.block())
                .add(POLYETHYLENE_MACHINE_FRAME.block());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ICE2.block())
                .add(ICE3.block())
                .add(ICE4.block())
                .add(ICE5.block())
                .add(ICE6.block())
                .add(ICE7.block())
                .add(POLYETHYLENE_MACHINE_FRAME.block());

        for (var block : CoreBlocks.getAllColoredPlasticBlocks()) {
            this.tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            this.tag(CoreTags.Blocks.PLASTIC).add(block);
        }

        for (var block : CoreBlocks.getOreBlocks()) {
            this.tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            this.tag(Tags.Blocks.ORES).add(block);
        }

        this.tag(BlockTags.ICE)
                .add(ICE2.block())
                .add(ICE3.block())
                .add(ICE4.block())
                .add(ICE5.block())
                .add(ICE6.block())
                .add(ICE7.block());
    }
}
