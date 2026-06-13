package general.mechanics.gui.renderers;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import general.mechanics.GM;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FluidTankRenderer {

	private static final NumberFormat nf = NumberFormat.getIntegerInstance();
	private static final int TEXTURE_SIZE = 16;
	private static final int MIN_FLUID_HEIGHT = 1;

	private final long capacity;
	private final TooltipMode tooltipMode;
	@Getter
    private final int width;
	@Getter
    private final int height;

	public FluidTankRenderer(long capacity, boolean showCapacity, int width, int height) {
		this(capacity, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height);
	}

	public FluidTankRenderer(long capacity, TooltipMode tooltipMode, int width, int height) {
		Preconditions.checkArgument(capacity > 0, "Capacity must be greater than 0.");
		Preconditions.checkArgument(width > 0, "Width must be greater than 0.");
		Preconditions.checkArgument(height > 0, "Height must be greater than 0.");

		this.capacity = capacity;
		this.tooltipMode = tooltipMode;
		this.width = width;
		this.height = height;
	}

	public void render (GuiGraphicsExtractor graphics, int x, int y, FluidStack stack) {
		RenderSystem.enableBlend();
        RenderSystem.
		graphics.pose().pushPose();
		{
			graphics.pose().translate(x, y, 0);
			drawFluid(graphics, width, height, stack);
		}
		graphics.pose().popPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.disableBlend();
	}

	private void drawFluid (GuiGraphicsExtractor graphics, final int width, final int height, FluidStack stack) {
		var fluid = stack.getFluid();
		if (fluid.isSame(Fluids.EMPTY)) {
			return;
		}

		var fluidStillSprite = getStillFluidSprite(stack);
		var fluidColor = getColorTint(stack);

		long amount = stack.getAmount();
		var scaledAmount = (amount * height) / capacity;

		if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
			scaledAmount = MIN_FLUID_HEIGHT;
		}

		if (scaledAmount > height) {
			scaledAmount = height;
		}

		drawTiledSprite(graphics, width, height, fluidColor, scaledAmount, fluidStillSprite);
	}

	private TextureAtlasSprite getStillFluidSprite (FluidStack stack) {
		var fluid = stack.getFluid();
		var renderProperties = IClientFluidTypeExtensions.of(fluid);
		var fluidStill = renderProperties.getStillTexture(stack);
		var minecraft = Minecraft.getInstance();

		return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
	}

	private int getColorTint (FluidStack stack) {
		var fluid = stack.getFluid();
		var renderProperties = IClientFluidTypeExtensions.of(fluid);
		return renderProperties.getTintColor(stack);
	}

	private static void drawTiledSprite (GuiGraphicsExtractor graphics, final int tiledWidth, final int tiledHeight, int color, long scaledAmount, TextureAtlasSprite sprite) {
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		var matrix = graphics.pose().last().pose();
		setGLColorFromInt(color);

		final var xTileCount = tiledWidth / TEXTURE_SIZE;
		final var xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
		final var yTileCount = scaledAmount / TEXTURE_SIZE;
		final var yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

		final var yStart = tiledHeight;

		for (var xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				var width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
				var height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
				var x = (xTile * TEXTURE_SIZE);
				var y = yStart - ((yTile + 1) * TEXTURE_SIZE);
				if (width > 0 && height > 0) {
					var maskTop = TEXTURE_SIZE - height;
					var maskRight = TEXTURE_SIZE - width;

					drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
				}
			}
		}
	}

	private static void setGLColorFromInt (int color) {
		var red = (color >> 16 & 0xFF) / 255.0F;
		var green = (color >> 8 & 0xFF) / 255.0F;
		var blue = (color & 0xFF) / 255.0F;
		var alpha = ((color >> 24) & 0xFF) / 255F;

		RenderSystem.setShaderColor(red, green, blue, alpha);
	}

	private static void drawTextureWithMasking (Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, float zLevel) {
		var uMin = textureSprite.getU0();
		var uMax = textureSprite.getU1();
		var vMin = textureSprite.getV0();
		var vMax = textureSprite.getV1();
		uMax = uMax - (maskRight / 16F * (uMax - uMin));
		vMax = vMax - (maskTop / 16F * (vMax - vMin));

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		var tessellator = Tesselator.getInstance();
		var bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.addVertex(matrix, xCoord, yCoord + 16, zLevel).setUv(uMin, vMax);
		bufferBuilder.addVertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).setUv(uMax, vMax);
		bufferBuilder.addVertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).setUv(uMax, vMin);
		bufferBuilder.addVertex(matrix, xCoord, yCoord + maskTop, zLevel).setUv(uMin, vMin);
		BufferUploader.drawWithShader(bufferBuilder.build());
	}

	public List<Component> getTooltip (FluidStack fluidStack, TooltipFlag tooltipFlag) {
		List<Component> tooltip = new ArrayList<>();

		Fluid fluidType = fluidStack.getFluid();
		try {
			if (fluidType.isSame(Fluids.EMPTY)) {
				tooltip.add(Component.literal("Empty"));
				tooltip.add(Component.translatable("gm.tooltip.liquid.amount.with.capacity", 0, nf.format(capacity)).withStyle(ChatFormatting.GRAY));
				return tooltip;
			}

			Component displayName = fluidStack.getHoverName();
			tooltip.add(displayName);

			long amount = fluidStack.getAmount();
			long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

			if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
				MutableComponent amountString = Component.translatable("gm.tooltip.liquid.amount.with.capacity", nf.format(milliBuckets), nf.format(capacity));
				tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
			} else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
				MutableComponent amountString = Component.translatable("gm.tooltip.liquid.amount", nf.format(milliBuckets));
				tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
			}
		} catch (RuntimeException e) {
			GM.LOGGER.error("Failed to get tooltip for fluid: {}", String.valueOf(e));
		}

		return tooltip;
	}

    public enum TooltipMode {
		SHOW_AMOUNT,
		SHOW_AMOUNT_AND_CAPACITY,
		ITEM_LIST
	}

}
