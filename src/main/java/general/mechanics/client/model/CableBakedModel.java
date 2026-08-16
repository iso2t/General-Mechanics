package general.mechanics.client.model;

import general.mechanics.common.block.cable.ConnectorType;
import general.api.resources.Resource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static general.mechanics.common.block.cable.ConnectorType.BLOCK;
import static general.mechanics.common.block.cable.ConnectorType.CABLE;
import static general.mechanics.client.model.BakedModelHelper.quad;
import static general.mechanics.client.model.BakedModelHelper.v;

/** Bakes and caches the state-dependent cable geometry used by the 26.1 renderer. */
public final class CableBakedModel implements DynamicBlockStateModel {

	private static final double O = .4;
	private static final double P = .1;
	private static final double Q = .2;

	static {
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, false, false, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_NONE, 0));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, false, false, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_END, 3));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, true, false, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_END, 0));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, false, true, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_END, 1));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, false, false, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_END, 2));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, true, false, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_CORNER, 0));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, true, true, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_CORNER, 1));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, false, true, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_CORNER, 2));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, false, false, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_CORNER, 3));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, true, false, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_STRAIGHT, 0));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, false, true, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_STRAIGHT, 1));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, true, true, false), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_THREE, 0));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(false, true, true, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_THREE, 1));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, false, true, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_THREE, 2));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, true, false, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_THREE, 3));
		CablePatterns.PATTERNS.put(CablePatterns.Pattern.of(true, true, true, true), CablePatterns.QuadSetting.of(CablePatterns.SpriteIdx.SPRITE_CROSS, 0));
	}

	private final ModelBaker baker;
	private final CableModelLoader.CableState bakedState;
	private final Material.Baked connector;
	private final Material.Baked normal;
	private final Material.Baked side;
	private final Map<CablePatterns.SpriteIdx, Material.Baked> caps;
	private final Map<CableModelLoader.CableState, BlockStateModelPart> parts = new ConcurrentHashMap<>();

	public CableBakedModel (ModelBaker baker, CableModelLoader.CableState bakedState) {
		this.baker = baker;
		this.bakedState = bakedState;
		this.connector = material("block/cable/connector");
		this.normal = material("block/cable/normal");
		this.side = material("block/cable/side");
		this.caps = new EnumMap<>(CablePatterns.SpriteIdx.class);
		caps.put(CablePatterns.SpriteIdx.SPRITE_NONE, material("block/cable/none"));
		caps.put(CablePatterns.SpriteIdx.SPRITE_END, material("block/cable/end"));
		caps.put(CablePatterns.SpriteIdx.SPRITE_STRAIGHT, normal);
		caps.put(CablePatterns.SpriteIdx.SPRITE_CORNER, material("block/cable/corner"));
		caps.put(CablePatterns.SpriteIdx.SPRITE_THREE, material("block/cable/three"));
		caps.put(CablePatterns.SpriteIdx.SPRITE_CROSS, material("block/cable/cross"));
	}

	private Material.Baked material (String path) {
		return baker.materials().get(new Material(Resource.get(path)), () -> "generalmechanics:cable");
	}

	@Override
	public Object createGeometryKey (BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
		return CableModelLoader.CableState.from(state);
	}

	@Override
	public void collectParts (BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> output) {
		output.add(parts.computeIfAbsent(CableModelLoader.CableState.from(state), this::bakePart));
	}

	@Override
	public Material.Baked particleMaterial () {
		return normal;
	}

	@Override
	public int materialFlags () {
		return parts.computeIfAbsent(bakedState, this::bakePart).materialFlags();
	}

	private BlockStateModelPart bakePart (CableModelLoader.CableState state) {
		List<net.minecraft.client.resources.model.geometry.BakedQuad> quads = new ArrayList<>();

		up(quads, state.up());
		down(quads, state.down());
		east(quads, state.east());
		west(quads, state.west());
		north(quads, state.north());
		south(quads, state.south());

		if (state.up() == ConnectorType.NONE) cap(quads, Direction.UP, state.west(), state.south(), state.east(), state.north());
		if (state.down() == ConnectorType.NONE) cap(quads, Direction.DOWN, state.west(), state.north(), state.east(), state.south());
		if (state.east() == ConnectorType.NONE) cap(quads, Direction.EAST, state.down(), state.north(), state.up(), state.south());
		if (state.west() == ConnectorType.NONE) cap(quads, Direction.WEST, state.down(), state.south(), state.up(), state.north());
		if (state.north() == ConnectorType.NONE) cap(quads, Direction.NORTH, state.west(), state.up(), state.east(), state.down());
		if (state.south() == ConnectorType.NONE) cap(quads, Direction.SOUTH, state.west(), state.down(), state.east(), state.up());

		QuadCollection.Builder builder = new QuadCollection.Builder();
		quads.forEach(builder::addUnculledFace);
		return new SimpleModelWrapper(builder.build(), true, normal);
	}

	private void up (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, ConnectorType type) {
		if (type == CABLE) {
			add(quads, normal, v(1 - O, 1, O), v(1 - O, 1, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1 - O, O));
			add(quads, normal, v(O, 1, 1 - O), v(O, 1, O), v(O, 1 - O, O), v(O, 1 - O, 1 - O));
			add(quads, normal, v(O, 1, O), v(1 - O, 1, O), v(1 - O, 1 - O, O), v(O, 1 - O, O));
			add(quads, normal, v(O, 1 - O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1, 1 - O), v(O, 1, 1 - O));
		} else if (type == BLOCK) {
			add(quads, normal, v(1 - O, 1 - P, O), v(1 - O, 1 - P, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1 - O, O));
			add(quads, normal, v(O, 1 - P, 1 - O), v(O, 1 - P, O), v(O, 1 - O, O), v(O, 1 - O, 1 - O));
			add(quads, normal, v(O, 1 - P, O), v(1 - O, 1 - P, O), v(1 - O, 1 - O, O), v(O, 1 - O, O));
			add(quads, normal, v(O, 1 - O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1 - P, 1 - O), v(O, 1 - P, 1 - O));
			add(quads, side, v(1 - Q, 1 - P, Q), v(1 - Q, 1, Q), v(1 - Q, 1, 1 - Q), v(1 - Q, 1 - P, 1 - Q));
			add(quads, side, v(Q, 1 - P, 1 - Q), v(Q, 1, 1 - Q), v(Q, 1, Q), v(Q, 1 - P, Q));
			add(quads, side, v(Q, 1, Q), v(1 - Q, 1, Q), v(1 - Q, 1 - P, Q), v(Q, 1 - P, Q));
			add(quads, side, v(Q, 1 - P, 1 - Q), v(1 - Q, 1 - P, 1 - Q), v(1 - Q, 1, 1 - Q), v(Q, 1, 1 - Q));
			add(quads, connector, v(Q, 1 - P, Q), v(1 - Q, 1 - P, Q), v(1 - Q, 1 - P, 1 - Q), v(Q, 1 - P, 1 - Q));
			add(quads, side, v(Q, 1, Q), v(Q, 1, 1 - Q), v(1 - Q, 1, 1 - Q), v(1 - Q, 1, Q));
		}
	}

	private void down (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, ConnectorType type) {
		if (type == CABLE) {
			add(quads, normal, v(1 - O, O, O), v(1 - O, O, 1 - O), v(1 - O, 0, 1 - O), v(1 - O, 0, O));
			add(quads, normal, v(O, O, 1 - O), v(O, O, O), v(O, 0, O), v(O, 0, 1 - O));
			add(quads, normal, v(O, O, O), v(1 - O, O, O), v(1 - O, 0, O), v(O, 0, O));
			add(quads, normal, v(O, 0, 1 - O), v(1 - O, 0, 1 - O), v(1 - O, O, 1 - O), v(O, O, 1 - O));
		} else if (type == BLOCK) {
			add(quads, normal, v(1 - O, O, O), v(1 - O, O, 1 - O), v(1 - O, P, 1 - O), v(1 - O, P, O));
			add(quads, normal, v(O, O, 1 - O), v(O, O, O), v(O, P, O), v(O, P, 1 - O));
			add(quads, normal, v(O, O, O), v(1 - O, O, O), v(1 - O, P, O), v(O, P, O));
			add(quads, normal, v(O, P, 1 - O), v(1 - O, P, 1 - O), v(1 - O, O, 1 - O), v(O, O, 1 - O));
			add(quads, side, v(1 - Q, 0, Q), v(1 - Q, P, Q), v(1 - Q, P, 1 - Q), v(1 - Q, 0, 1 - Q));
			add(quads, side, v(Q, 0, 1 - Q), v(Q, P, 1 - Q), v(Q, P, Q), v(Q, 0, Q));
			add(quads, side, v(Q, P, Q), v(1 - Q, P, Q), v(1 - Q, 0, Q), v(Q, 0, Q));
			add(quads, side, v(Q, 0, 1 - Q), v(1 - Q, 0, 1 - Q), v(1 - Q, P, 1 - Q), v(Q, P, 1 - Q));
			add(quads, connector, v(Q, P, 1 - Q), v(1 - Q, P, 1 - Q), v(1 - Q, P, Q), v(Q, P, Q));
			add(quads, side, v(Q, 0, 1 - Q), v(Q, 0, Q), v(1 - Q, 0, Q), v(1 - Q, 0, 1 - Q));
		}
	}

	private void east (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, ConnectorType type) {
		if (type == CABLE) {
			add(quads, normal, v(1, 1 - O, 1 - O), v(1, 1 - O, O), v(1 - O, 1 - O, O), v(1 - O, 1 - O, 1 - O));
			add(quads, normal, v(1, O, O), v(1, O, 1 - O), v(1 - O, O, 1 - O), v(1 - O, O, O));
			add(quads, normal, v(1, 1 - O, O), v(1, O, O), v(1 - O, O, O), v(1 - O, 1 - O, O));
			add(quads, normal, v(1, O, 1 - O), v(1, 1 - O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, O, 1 - O));
		} else if (type == BLOCK) {
			add(quads, normal, v(1 - P, 1 - O, 1 - O), v(1 - P, 1 - O, O), v(1 - O, 1 - O, O), v(1 - O, 1 - O, 1 - O));
			add(quads, normal, v(1 - P, O, O), v(1 - P, O, 1 - O), v(1 - O, O, 1 - O), v(1 - O, O, O));
			add(quads, normal, v(1 - P, 1 - O, O), v(1 - P, O, O), v(1 - O, O, O), v(1 - O, 1 - O, O));
			add(quads, normal, v(1 - P, O, 1 - O), v(1 - P, 1 - O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, O, 1 - O));
			add(quads, side, v(1 - P, 1 - Q, 1 - Q), v(1, 1 - Q, 1 - Q), v(1, 1 - Q, Q), v(1 - P, 1 - Q, Q));
			add(quads, side, v(1 - P, Q, Q), v(1, Q, Q), v(1, Q, 1 - Q), v(1 - P, Q, 1 - Q));
			add(quads, side, v(1 - P, 1 - Q, Q), v(1, 1 - Q, Q), v(1, Q, Q), v(1 - P, Q, Q));
			add(quads, side, v(1 - P, Q, 1 - Q), v(1, Q, 1 - Q), v(1, 1 - Q, 1 - Q), v(1 - P, 1 - Q, 1 - Q));
			add(quads, connector, v(1 - P, Q, 1 - Q), v(1 - P, 1 - Q, 1 - Q), v(1 - P, 1 - Q, Q), v(1 - P, Q, Q));
			add(quads, side, v(1, Q, 1 - Q), v(1, Q, Q), v(1, 1 - Q, Q), v(1, 1 - Q, 1 - Q));
		}
	}

	private void west (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, ConnectorType type) {
		if (type == CABLE) {
			add(quads, normal, v(O, 1 - O, 1 - O), v(O, 1 - O, O), v(0, 1 - O, O), v(0, 1 - O, 1 - O));
			add(quads, normal, v(O, O, O), v(O, O, 1 - O), v(0, O, 1 - O), v(0, O, O));
			add(quads, normal, v(O, 1 - O, O), v(O, O, O), v(0, O, O), v(0, 1 - O, O));
			add(quads, normal, v(O, O, 1 - O), v(O, 1 - O, 1 - O), v(0, 1 - O, 1 - O), v(0, O, 1 - O));
		} else if (type == BLOCK) {
			add(quads, normal, v(O, 1 - O, 1 - O), v(O, 1 - O, O), v(P, 1 - O, O), v(P, 1 - O, 1 - O));
			add(quads, normal, v(O, O, O), v(O, O, 1 - O), v(P, O, 1 - O), v(P, O, O));
			add(quads, normal, v(O, 1 - O, O), v(O, O, O), v(P, O, O), v(P, 1 - O, O));
			add(quads, normal, v(O, O, 1 - O), v(O, 1 - O, 1 - O), v(P, 1 - O, 1 - O), v(P, O, 1 - O));
			add(quads, side, v(0, 1 - Q, 1 - Q), v(P, 1 - Q, 1 - Q), v(P, 1 - Q, Q), v(0, 1 - Q, Q));
			add(quads, side, v(0, Q, Q), v(P, Q, Q), v(P, Q, 1 - Q), v(0, Q, 1 - Q));
			add(quads, side, v(0, 1 - Q, Q), v(P, 1 - Q, Q), v(P, Q, Q), v(0, Q, Q));
			add(quads, side, v(0, Q, 1 - Q), v(P, Q, 1 - Q), v(P, 1 - Q, 1 - Q), v(0, 1 - Q, 1 - Q));
			add(quads, connector, v(P, Q, Q), v(P, 1 - Q, Q), v(P, 1 - Q, 1 - Q), v(P, Q, 1 - Q));
			add(quads, side, v(0, Q, Q), v(0, Q, 1 - Q), v(0, 1 - Q, 1 - Q), v(0, 1 - Q, Q));
		}
	}

	private void north (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, ConnectorType type) {
		if (type == CABLE) {
			add(quads, normal, v(O, 1 - O, O), v(1 - O, 1 - O, O), v(1 - O, 1 - O, 0), v(O, 1 - O, 0));
			add(quads, normal, v(O, O, 0), v(1 - O, O, 0), v(1 - O, O, O), v(O, O, O));
			add(quads, normal, v(1 - O, O, 0), v(1 - O, 1 - O, 0), v(1 - O, 1 - O, O), v(1 - O, O, O));
			add(quads, normal, v(O, O, O), v(O, 1 - O, O), v(O, 1 - O, 0), v(O, O, 0));
		} else if (type == BLOCK) {
			add(quads, normal, v(O, 1 - O, O), v(1 - O, 1 - O, O), v(1 - O, 1 - O, P), v(O, 1 - O, P));
			add(quads, normal, v(O, O, P), v(1 - O, O, P), v(1 - O, O, O), v(O, O, O));
			add(quads, normal, v(1 - O, O, P), v(1 - O, 1 - O, P), v(1 - O, 1 - O, O), v(1 - O, O, O));
			add(quads, normal, v(O, O, O), v(O, 1 - O, O), v(O, 1 - O, P), v(O, O, P));
			add(quads, side, v(Q, 1 - Q, P), v(1 - Q, 1 - Q, P), v(1 - Q, 1 - Q, 0), v(Q, 1 - Q, 0));
			add(quads, side, v(Q, Q, 0), v(1 - Q, Q, 0), v(1 - Q, Q, P), v(Q, Q, P));
			add(quads, side, v(1 - Q, Q, 0), v(1 - Q, 1 - Q, 0), v(1 - Q, 1 - Q, P), v(1 - Q, Q, P));
			add(quads, side, v(Q, Q, P), v(Q, 1 - Q, P), v(Q, 1 - Q, 0), v(Q, Q, 0));
			add(quads, connector, v(Q, Q, P), v(1 - Q, Q, P), v(1 - Q, 1 - Q, P), v(Q, 1 - Q, P));
			add(quads, side, v(Q, Q, 0), v(Q, 1 - Q, 0), v(1 - Q, 1 - Q, 0), v(1 - Q, Q, 0));
		}
	}

	private void south (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, ConnectorType type) {
		if (type == CABLE) {
			add(quads, normal, v(O, 1 - O, 1), v(1 - O, 1 - O, 1), v(1 - O, 1 - O, 1 - O), v(O, 1 - O, 1 - O));
			add(quads, normal, v(O, O, 1 - O), v(1 - O, O, 1 - O), v(1 - O, O, 1), v(O, O, 1));
			add(quads, normal, v(1 - O, O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1 - O, 1), v(1 - O, O, 1));
			add(quads, normal, v(O, O, 1), v(O, 1 - O, 1), v(O, 1 - O, 1 - O), v(O, O, 1 - O));
		} else if (type == BLOCK) {
			add(quads, normal, v(O, 1 - O, 1 - P), v(1 - O, 1 - O, 1 - P), v(1 - O, 1 - O, 1 - O), v(O, 1 - O, 1 - O));
			add(quads, normal, v(O, O, 1 - O), v(1 - O, O, 1 - O), v(1 - O, O, 1 - P), v(O, O, 1 - P));
			add(quads, normal, v(1 - O, O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1 - O, 1 - P), v(1 - O, O, 1 - P));
			add(quads, normal, v(O, O, 1 - P), v(O, 1 - O, 1 - P), v(O, 1 - O, 1 - O), v(O, O, 1 - O));
			add(quads, side, v(Q, 1 - Q, 1), v(1 - Q, 1 - Q, 1), v(1 - Q, 1 - Q, 1 - P), v(Q, 1 - Q, 1 - P));
			add(quads, side, v(Q, Q, 1 - P), v(1 - Q, Q, 1 - P), v(1 - Q, Q, 1), v(Q, Q, 1));
			add(quads, side, v(1 - Q, Q, 1 - P), v(1 - Q, 1 - Q, 1 - P), v(1 - Q, 1 - Q, 1), v(1 - Q, Q, 1));
			add(quads, side, v(Q, Q, 1), v(Q, 1 - Q, 1), v(Q, 1 - Q, 1 - P), v(Q, Q, 1 - P));
			add(quads, connector, v(Q, 1 - Q, 1 - P), v(1 - Q, 1 - Q, 1 - P), v(1 - Q, Q, 1 - P), v(Q, Q, 1 - P));
			add(quads, side, v(Q, 1 - Q, 1), v(Q, Q, 1), v(1 - Q, Q, 1), v(1 - Q, 1 - Q, 1));
		}
	}


	private void cap (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, Direction direction, ConnectorType a, ConnectorType b, ConnectorType c, ConnectorType d) {
		CablePatterns.QuadSetting setting = CablePatterns.findPattern(a, b, c, d);
		Material.Baked material = caps.get(setting.sprite());
		switch (direction) {
			case NORTH -> add(quads, material, setting.rotation(), v(O, 1 - O, O), v(1 - O, 1 - O, O), v(1 - O, O, O), v(O, O, O));
			case SOUTH -> add(quads, material, setting.rotation(), v(O, O, 1 - O), v(1 - O, O, 1 - O), v(1 - O, 1 - O, 1 - O), v(O, 1 - O, 1 - O));
			case WEST -> add(quads, material, setting.rotation(), v(O, O, 1 - O), v(O, 1 - O, 1 - O), v(O, 1 - O, O), v(O, O, O));
			case EAST -> add(quads, material, setting.rotation(), v(1 - O, O, O), v(1 - O, 1 - O, O), v(1 - O, 1 - O, 1 - O), v(1 - O, O, 1 - O));
			case DOWN -> add(quads, material, setting.rotation(), v(O, O, O), v(1 - O, O, O), v(1 - O, O, 1 - O), v(O, O, 1 - O));
			case UP -> add(quads, material, setting.rotation(), v(O, 1 - O, 1 - O), v(1 - O, 1 - O, 1 - O), v(1 - O, 1 - O, O), v(O, 1 - O, O));
		}
	}

	private static void add (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, Material.Baked material, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		quads.add(quad(v1, v2, v3, v4, material, 0));
	}

	private static void add (List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, Material.Baked material, int rotation, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		quads.add(quad(v1, v2, v3, v4, material, rotation));
	}
}
