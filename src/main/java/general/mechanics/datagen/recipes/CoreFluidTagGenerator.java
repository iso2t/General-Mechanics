package general.mechanics.datagen.recipes;

import general.mechanics.GM;
import general.mechanics.api.util.IDataProvider;
import general.mechanics.registries.CoreFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class CoreFluidTagGenerator extends FluidTagsProvider implements IDataProvider {

    public CoreFluidTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, GM.MODID);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider provider) {
        this.tag(FluidTags.WATER)
                .add(CoreFluids.CRUDE_OIL.source().get())
                .add(CoreFluids.CRUDE_OIL.flowing().get())
                .add(CoreFluids.DIESEL.source().get())
                .add(CoreFluids.DIESEL.flowing().get());
    }

    @Override
    public @Nonnull String getName() {
        return GM.NAME + " Fluid Tags";
    }
}
