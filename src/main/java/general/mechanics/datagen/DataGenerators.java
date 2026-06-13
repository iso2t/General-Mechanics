package general.mechanics.datagen;

import general.mechanics.GM;
import general.mechanics.datagen.data.SoundProvider;
import general.mechanics.datagen.language.CoreEnLangProvider;
import general.mechanics.datagen.loot.CoreLootTableProvider;
import general.mechanics.datagen.models.BlockModelProvider;
import general.mechanics.datagen.models.ItemModelProvider;
import general.mechanics.datagen.recipes.CoreFluidTagGenerator;
import general.mechanics.datagen.recipes.CraftingRecipes;
import general.mechanics.datagen.recipes.MachineRecipes;
import general.mechanics.datagen.recipes.SmeltingRecipes;
import general.mechanics.datagen.tags.CoreBlockTagGenerator;
import general.mechanics.datagen.tags.CoreItemTagGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = GM.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gather (@NotNull GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var pack = generator.getVanillaPack(true);
        var localization = new CoreEnLangProvider(generator);

        // WORLD GENERATION
        //pack.addProvider(output -> new WorldGenProvider(output, registries));

        // SOUNDS
        pack.addProvider(SoundProvider::new);

        // LOOT TABLE
        pack.addProvider(bindRegistries(CoreLootTableProvider::new, registries));

        // POI
        //pack.addProvider(packOutput -> new FMPoiTagGenerator(packOutput, registries, existingFileHelper));

        // TAGS
        var blockTagsProvider = pack.addProvider(pOutput -> new CoreBlockTagGenerator(pOutput, registries));
        pack.addProvider(pOutput -> new CoreItemTagGenerator(pOutput, registries));
        pack.addProvider(packOutput -> new CoreFluidTagGenerator(packOutput, registries));

        // MODELS & STATES
        pack.addProvider(BlockModelProvider::new);
        pack.addProvider(ItemModelProvider::new);

        // RECIPES
        pack.addProvider(bindRegistries(CraftingRecipes::new, registries));
        pack.addProvider(bindRegistries(SmeltingRecipes::new, registries));
        pack.addProvider(bindRegistries(MachineRecipes::new, registries));

        // LOCALIZATION MUST RUN LAST
        pack.addProvider(output -> localization);
    }

    @Contract(pure = true)
    private static <T extends DataProvider> DataProvider.@NotNull Factory<T> bindRegistries (BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory, CompletableFuture<HolderLookup.Provider> factories) {
        return pOutput -> factory.apply(pOutput, factories);
    }

}
