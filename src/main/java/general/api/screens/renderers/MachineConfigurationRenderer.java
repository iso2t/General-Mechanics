package general.api.screens.renderers;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import general.api.model.ConfigurableMachineModelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Renders a model-data-backed machine block inside a GUI viewport. */
public final class MachineConfigurationRenderer extends PictureInPictureRenderer<MachineConfigurationRenderState> {

	private static final Direction[] DIRECTIONS = Direction.values();

	private final List<BlockStateModelPart> parts        = new ArrayList<>();
	private final QuadInstance              quadInstance = new QuadInstance();
	private final RandomSource              random       = RandomSource.create(42L);

	public MachineConfigurationRenderer (MultiBufferSource.BufferSource bufferSource) {
		super(bufferSource);
	}

	@Override
	public Class<MachineConfigurationRenderState> getRenderStateClass () {
		return MachineConfigurationRenderState.class;
	}

	@Override
	protected void renderToTexture (MachineConfigurationRenderState renderState, PoseStack poseStack) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		poseStack.scale(1.0F, -1.0F, 1.0F);
		poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(renderState.pitch())).rotateY((float) Math.toRadians(renderState.yaw())));
		poseStack.translate(-0.5F, -0.5F, -0.5F);

		BlockState state = renderState.blockState();
		PreviewLevel previewLevel = new PreviewLevel(state, ConfigurableMachineModelData.create(renderState.modes()));
		var model = minecraft.getModelManager().getBlockStateModelSet().get(state);
		parts.clear();
		random.setSeed(42L);
		model.collectParts(previewLevel, BlockPos.ZERO, state, random, parts);

		for (BlockStateModelPart part : parts) {
			for (Direction direction : DIRECTIONS) renderQuads(poseStack, part.getQuads(direction));
			renderQuads(poseStack, part.getQuads(null));
		}
		parts.clear();
	}

	private void renderQuads (PoseStack poseStack, List<net.minecraft.client.resources.model.geometry.BakedQuad> quads) {
		for (var quad : quads) {
			bufferSource.getBuffer(quad.materialInfo().itemRenderType()).putBakedQuad(poseStack.last(), quad, quadInstance);
		}
	}

	@Override
	protected float getTranslateY (int height, int guiScale) {
		return height / 2.0F;
	}

	@Override
	protected String getTextureLabel () {
		return "machine configuration";
	}

	private record PreviewLevel(BlockState state, ModelData modelData) implements BlockAndTintGetter {

		@Override
		public CardinalLighting cardinalLighting () {
			return CardinalLighting.DEFAULT;
		}

		@Override
		public LevelLightEngine getLightEngine () {
			return LevelLightEngine.EMPTY;
		}

		@Override
		public int getBlockTint (BlockPos pos, ColorResolver color) {
			return -1;
		}

		@Override
		public @Nullable BlockEntity getBlockEntity (BlockPos pos) {
			return null;
		}

		@Override
		public BlockState getBlockState (BlockPos pos) {
			return pos.equals(BlockPos.ZERO) ? state : Blocks.AIR.defaultBlockState();
		}

		@Override
		public FluidState getFluidState (BlockPos pos) {
			return Fluids.EMPTY.defaultFluidState();
		}

		@Override
		public int getHeight () {
			return 1;
		}

		@Override
		public int getMinY () {
			return 0;
		}

		@Override
		public ModelData getModelData (BlockPos pos) {
			return pos.equals(BlockPos.ZERO) ? modelData : ModelData.EMPTY;
		}
	}
}
