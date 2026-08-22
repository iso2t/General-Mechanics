package general.mechanics.client.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;
import general.api.block.util.ILitProvider;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideMode;
import general.api.model.ConfigurableMachineModelData;
import general.api.model.IConfigurableMachineModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Dynamic cube model whose five configurable faces come from immutable model data. */
final class ConfigurableMachineBakedModel implements DynamicBlockStateModel {

	private static final Vector3f FROM = new Vector3f(0, 0, 0);
	private static final Vector3f TO   = new Vector3f(16, 16, 16);

	private final Direction                                              front;
	private final OctahedralGroup                                        rotationGroup;
	private final BlockModelRotation                                     rotation;
	private final Material.Baked                                         particleMaterial;
	private final BakedQuad                                              frontQuad;
	private final Map<Direction, Map<MachineSideMode, BakedQuad>>        faceQuads = new EnumMap<>(Direction.class);
	private final ConfigurableMachineModelData.Modes                     fallback;
	private final Map<ConfigurableMachineModelData.Modes, BlockStateModelPart> parts = new ConcurrentHashMap<>();
	private final BlockStateModelPart                                    fallbackPart;
	private final Object                                                 geometryIdentity = new Object();

	ConfigurableMachineBakedModel (ModelBaker baker, BlockState state, IConfigurableMachineModel machine) {
		this.front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		this.rotationGroup = rotationFor(front);
		this.rotation = BlockModelRotation.get(rotationGroup);
		boolean lit = machine instanceof ILitProvider && state.hasProperty(ILitProvider.LIT) && state.getValue(ILitProvider.LIT);
		this.particleMaterial = material(baker, machine.getSideTexture());
		this.frontQuad = bakeFace(baker, Direction.NORTH, material(baker, lit ? ((ILitProvider) machine).getLitTexture() : machine.getFrontTexture()));
		this.fallback = ConfigurableMachineModelData.defaults(machine.getSideConfigurationDefinition());

		for (Direction face : Direction.values()) {
			if (face == Direction.NORTH) continue;
			var byMode = new EnumMap<MachineSideMode, BakedQuad>(MachineSideMode.class);
			Direction worldFace = rotationGroup.rotate(face);
			MachineFace machineFace = MachineFace.fromWorldDirection(front, worldFace);
			for (MachineSideMode mode : machine.getSideConfigurationDefinition().getAllowedModes(machineFace)) {
				byMode.put(mode, bakeFace(baker, face, material(baker, machine.getConfiguredTexture(face, mode))));
			}
			faceQuads.put(face, byMode);
		}
		this.fallbackPart = bakePart(fallback);
		parts.put(fallback, fallbackPart);
	}

	@Override
	public Object createGeometryKey (BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
		return new GeometryKey(geometryIdentity, modes(level, pos));
	}

	@Override
	public void collectParts (BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> output) {
		ConfigurableMachineModelData.Modes modes = modes(level, pos);
		output.add(parts.computeIfAbsent(modes, this::bakePart));
	}

	@Override
	public Material.Baked particleMaterial () {
		return particleMaterial;
	}

	@Override
	public int materialFlags () {
		return fallbackPart.materialFlags();
	}

	private ConfigurableMachineModelData.Modes modes (BlockAndTintGetter level, BlockPos pos) {
		ConfigurableMachineModelData.Modes modes = level.getModelData(pos).get(ConfigurableMachineModelData.MODES);
		return modes == null ? fallback : modes;
	}

	private BlockStateModelPart bakePart (ConfigurableMachineModelData.Modes modes) {
		QuadCollection.Builder quads = new QuadCollection.Builder();
		for (Direction baseFace : Direction.values()) {
			Direction worldFace = rotationGroup.rotate(baseFace);
			MachineFace machineFace = MachineFace.fromWorldDirection(front, worldFace);
			Map<MachineSideMode, BakedQuad> configuredQuads = faceQuads.get(baseFace);
			BakedQuad quad = baseFace == Direction.NORTH ? frontQuad : configuredQuads.getOrDefault(modes.get(machineFace), configuredQuads.get(fallback.get(machineFace)));
			quads.addCulledFace(worldFace, quad);
		}
		return new SimpleModelWrapper(quads.build(), true, particleMaterial);
	}

	private BakedQuad bakeFace (ModelBaker baker, Direction face, Material.Baked material) {
		CuboidFace cuboidFace = new CuboidFace(face, CuboidFace.NO_TINT, "", null, Quadrant.R0);
		return FaceBakery.bakeQuad(baker, FROM, TO, cuboidFace, material, face, rotation, null, true, 0);
	}

	private static Material.Baked material (ModelBaker baker, Identifier texture) {
		return baker.materials().get(new Material(texture), ConfigurableMachineModelLoader.ID::toString);
	}

	private static OctahedralGroup rotationFor (Direction front) {
		return switch (front) {
			case NORTH -> OctahedralGroup.IDENTITY;
			case EAST -> OctahedralGroup.BLOCK_ROT_Y_90;
			case SOUTH -> OctahedralGroup.BLOCK_ROT_Y_180;
			case WEST -> OctahedralGroup.BLOCK_ROT_Y_270;
			default -> throw new IllegalArgumentException("Configurable machine front must be horizontal: " + front);
		};
	}

	private record GeometryKey(Object model, ConfigurableMachineModelData.Modes modes) {
	}
}
