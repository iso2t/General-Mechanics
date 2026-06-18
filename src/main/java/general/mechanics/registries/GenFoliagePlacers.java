package general.mechanics.registries;

import general.api.mod.GenAPI;
import general.mechanics.worldgen.RubberFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GenFoliagePlacers {

	private GenFoliagePlacers () {
	}

	public static final DeferredRegister<FoliagePlacerType<?>> REGISTRY = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, GenAPI.getModId());

	public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<RubberFoliagePlacer>> RUBBER = REGISTRY.register("rubber_foliage_placer", () -> new FoliagePlacerType<>(RubberFoliagePlacer.CODEC));
}
