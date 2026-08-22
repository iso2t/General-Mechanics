package general.mechanics.datagen.tags;

import general.mechanics.Mechanics;
import general.mechanics.registries.GenFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class GenFluidTagGenerator extends FluidTagsProvider {

	public GenFluidTagGenerator (PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Mechanics.MOD_ID);
	}

	@Override
	protected void addTags (HolderLookup.@NonNull Provider registries) {
		for (var fluid : GenFluids.getFluids()) {
			this.tag(FluidTags.WATER).add(fluid.get());
		}
	}

}
