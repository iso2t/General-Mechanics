package general.mechanics.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GenLootTableProvider extends LootTableProvider {

	private static final List<SubProviderEntry> SUB_PROVIDERS = List.of(new SubProviderEntry(DropProvider::new, LootContextParamSets.BLOCK));

	public GenLootTableProvider (PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), SUB_PROVIDERS, registries);
	}

	@Override
	protected void validate (@NonNull WritableRegistry<LootTable> tables, @NonNull ValidationContextSource validationContext, ProblemReporter.@NonNull Collector problems) {

	}
}
