package general.mechanics.client.model;

import com.mojang.serialization.MapCodec;
import general.api.model.IConfigurableMachineModel;
import general.api.resources.Resource;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;
import org.jspecify.annotations.NonNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Reusable blockstate definition for machines with model-data-backed sides. */
public final class ConfigurableMachineModelLoader implements CustomBlockModelDefinition {

	public static final Identifier                             ID       = Resource.get("configurable_machine");
	public static final ConfigurableMachineModelLoader         INSTANCE = new ConfigurableMachineModelLoader();
	public static final MapCodec<ConfigurableMachineModelLoader> CODEC  = MapCodec.unit(INSTANCE);

	private ConfigurableMachineModelLoader () {
	}

	public static void register (RegisterBlockStateModels event) {
		event.registerDefinition(ID, CODEC);
	}

	@Override
	public @NonNull Map<BlockState, BlockStateModel.UnbakedRoot> instantiate (StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier) {
		if (!(states.getOwner() instanceof IConfigurableMachineModel machine)) {
			throw new IllegalArgumentException("Configurable machine model used by a block that does not implement IConfigurableMachineModel: " + sourceSupplier.get());
		}

		BlockStateModel.UnbakedRoot root = new Root(machine);
		Map<BlockState, BlockStateModel.UnbakedRoot> result = new IdentityHashMap<>();
		for (BlockState state : states.getPossibleStates()) result.put(state, root);
		return result;
	}

	@Override
	public @NonNull MapCodec<? extends CustomBlockModelDefinition> codec () {
		return CODEC;
	}

	private record Root(IConfigurableMachineModel machine) implements BlockStateModel.UnbakedRoot {

		@Override
		public void resolveDependencies (net.minecraft.client.resources.model.ResolvableModel.Resolver resolver) {
			// All geometry is built from block-atlas materials at bake time.
		}

		@Override
		public @NonNull BlockStateModel bake (@NonNull BlockState state, @NonNull ModelBaker baker) {
			return new ConfigurableMachineBakedModel(baker, state, machine);
		}

		@Override
		public @NonNull Object visualEqualityGroup (@NonNull BlockState state) {
			return state;
		}
	}
}
