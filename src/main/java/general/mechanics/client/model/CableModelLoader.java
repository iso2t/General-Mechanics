package general.mechanics.client.model;

import com.mojang.serialization.MapCodec;
import general.mechanics.common.block.cable.CableBlock;
import general.mechanics.common.block.cable.ConnectorType;
import general.api.resources.Resource;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * NeoForge 26.1 blockstate-definition loader for the state-dependent cable mesh.
 *
 * <p>In 1.20 this was an {@code IGeometryLoader} which returned a dynamic
 * {@code IBakedModel}. The 26.1 renderer bakes blockstate definitions into
 * {@link BlockStateModel}s instead, so each cable state is now baked through one
 * shared root.</p>
 */
public final class CableModelLoader implements CustomBlockModelDefinition {

	public static final Identifier ID = Resource.get("cable");
	public static final CableModelLoader INSTANCE = new CableModelLoader();
	public static final MapCodec<CableModelLoader> CODEC = MapCodec.unit(INSTANCE);

	private static final BlockStateModel.UnbakedRoot ROOT = new Root();

	private CableModelLoader() {}

	public static void register (RegisterBlockStateModels event) {
		event.registerDefinition(ID, CODEC);
	}

	@Override
	public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate (StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier) {
		Map<BlockState, BlockStateModel.UnbakedRoot> result = new IdentityHashMap<>();
		for (BlockState state : states.getPossibleStates()) {
			result.put(state, ROOT);
		}
		return result;
	}

	@Override
	public MapCodec<? extends CustomBlockModelDefinition> codec () {
		return CODEC;
	}

	private static final class Root implements BlockStateModel.UnbakedRoot {

		@Override
		public void resolveDependencies (net.minecraft.client.resources.model.ResolvableModel.Resolver resolver) {
			// Cable geometry only uses block-atlas materials; it has no model dependencies.
		}

		@Override
		public BlockStateModel bake (BlockState state, ModelBaker baker) {
			return new CableBakedModel(baker, CableState.from(state));
		}

		@Override
		public Object visualEqualityGroup (BlockState state) {
			return CableState.from(state);
		}
	}

	static record CableState(ConnectorType north, ConnectorType south, ConnectorType west, ConnectorType east, ConnectorType up, ConnectorType down) {
		static CableState from (BlockState state) {
			return new CableState(
					state.getValue(CableBlock.NORTH), state.getValue(CableBlock.SOUTH),
					state.getValue(CableBlock.WEST), state.getValue(CableBlock.EAST),
					state.getValue(CableBlock.UP), state.getValue(CableBlock.DOWN)
			);
		}
	}
}
