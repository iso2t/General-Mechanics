package general.api.screens.renderers;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.Supplier;

public class GuiFluidRenderer extends AbstractBarRenderer {
	private static final Component EMPTY_FLUID = Component.translatableWithFallback("genapi.fluid.empty", "Empty");

	@Getter
	private static final NumberFormat format = NumberFormat.getIntegerInstance();

	@Getter
	private static final int textureSize = 16;

	@Getter
	private static final int minFluidHeight = 1;

	@Getter
	private final long capacity;

	private final Supplier<FluidStack> fluidSupplier;

	@Getter
	@Setter
	private TooltipMode tooltipMode;

	public GuiFluidRenderer (int xPos, int yPos, int width, int height, long capacity, Supplier<FluidStack> fluidSupplier, TooltipMode tooltipMode) {
		super(xPos, yPos, width, height);

		Preconditions.checkArgument(capacity > 0, "Capacity must be greater than 0.");

		this.capacity = capacity;
		this.fluidSupplier = Preconditions.checkNotNull(fluidSupplier, "Fluid supplier cannot be null.");
		this.tooltipMode = Preconditions.checkNotNull(tooltipMode, "Tooltip mode cannot be null.");
	}

	public GuiFluidRenderer (int xPos, int yPos, int width, int height, long capacity, Supplier<FluidStack> fluidSupplier, boolean showCapacity) {
		this(xPos, yPos, width, height, capacity, fluidSupplier, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT);
	}

	@Override
	public void render (GuiGraphicsExtractor graphics) {
		renderFluid(graphics, getFluid(), getXPos(), getYPos());
	}

	/**
	 * Renders the fluid and handles its tooltip.
	 * <p>
	 * GuiGraphicsExtractor stores the mouse position internally, but does not
	 * expose public mouse position getters in 26.1.2, so the screen should use
	 * this overload when tooltip support is desired.
	 */
	public void render (GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		FluidStack stack = getFluid();

		renderFluid(graphics, stack, getXPos(), getYPos());

		if (isMouseOverAbsolute(mouseX, mouseY, getXPos(), getYPos())) {
			renderTooltip(graphics, mouseX, mouseY, stack);
		}
	}

	/** Renders at the configured position relative to a screen origin. */
	public void render (GuiGraphicsExtractor graphics, int screenX, int screenY, int mouseX, int mouseY) {
		FluidStack stack = getFluid();
		int x = screenX + getXPos();
		int y = screenY + getYPos();
		renderFluid(graphics, stack, x, y);
		if (isMouseOverAbsolute(mouseX, mouseY, x, y)) {
			renderTooltip(graphics, mouseX, mouseY, stack);
		}
	}

	private void renderFluid (GuiGraphicsExtractor graphics, FluidStack stack, int x, int y) {
		if (stack.isEmpty()) {
			return;
		}

		FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(stack.getFluid().defaultFluidState());

		TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

		int tint = getTint(fluidModel, stack);
		int fluidHeight = getFluidHeight(stack);

		if (fluidHeight <= 0) {
			return;
		}

		int bottom = y + getHeight();
		int top = bottom - fluidHeight;

		graphics.enableScissor(x, top, x + getWidth(), bottom);

		try {
			/*
			 * Tile the normal 16x16 fluid texture rather than stretching it.
			 * Starting from the bottom makes partial fills behave naturally.
			 */
			for (int tileY = bottom - textureSize; tileY < bottom; tileY -= textureSize) {
				for (int tileX = x; tileX < x + getWidth(); tileX += textureSize) {
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, tileX, tileY, textureSize, textureSize, tint);
				}

				if (tileY <= top) {
					break;
				}
			}
		} finally {
			graphics.disableScissor();
		}
	}

	private FluidStack getFluid () {
		FluidStack stack = fluidSupplier.get();
		return stack == null ? FluidStack.EMPTY : stack;
	}

	private int getFluidHeight (FluidStack stack) {
		if (stack.isEmpty() || stack.getAmount() <= 0) {
			return 0;
		}

		long amount = Math.min(stack.getAmount(), capacity);

		int fluidHeight = (int) Math.ceil((amount / (double) capacity) * getHeight());

		return Math.clamp(fluidHeight, minFluidHeight, getHeight());
	}

	private static int getTint (FluidModel model, FluidStack stack) {
		FluidTintSource tintSource = model.fluidTintSource();

		if (tintSource == null) {
			return 0xFFFFFFFF;
		}

		return tintSource.colorAsStack(stack);
	}

	/**
	 * Tests this renderer's bounds relative to a screen origin.
	 *
	 * @param mouseX mouse x-coordinate in screen space
	 * @param mouseY mouse y-coordinate in screen space
	 * @param screenX x-coordinate of the screen's top-left corner
	 * @param screenY y-coordinate of the screen's top-left corner
	 */
	public boolean isMouseOver (double mouseX, double mouseY, int screenX, int screenY) {
		return isMouseOverAbsolute(mouseX, mouseY, screenX + getXPos(), screenY + getYPos());
	}

	private boolean isMouseOverAbsolute (double mouseX, double mouseY, int x, int y) {
		return mouseX >= x && mouseX < x + getWidth() && mouseY >= y && mouseY < y + getHeight();
	}

	private void renderTooltip (GuiGraphicsExtractor graphics, int mouseX, int mouseY, FluidStack stack) {
		Minecraft minecraft = Minecraft.getInstance();

		List<Component> tooltip = stack.isEmpty() ? getEmptyTooltip() : switch (tooltipMode) {
			case SHOW_AMOUNT -> List.of(stack.getHoverName(), Component.literal(format.format(stack.getAmount()) + " mB").withStyle(ChatFormatting.GRAY));

			case SHOW_AMOUNT_AND_CAPACITY -> List.of(stack.getHoverName(), Component.literal(format.format(stack.getAmount()) + " / " + format.format(capacity) + " mB").withStyle(ChatFormatting.GRAY));

			case ITEM_LIST -> getFluidTooltip(stack);
		};

		graphics.setComponentTooltipForNextFrame(minecraft.font, tooltip, mouseX, mouseY);
	}

	private List<Component> getEmptyTooltip () {
		return switch (tooltipMode) {
			case SHOW_AMOUNT -> List.of(EMPTY_FLUID, Component.literal("0 mB").withStyle(ChatFormatting.GRAY));
			case SHOW_AMOUNT_AND_CAPACITY -> List.of(EMPTY_FLUID, Component.literal("0 / " + format.format(capacity) + " mB").withStyle(ChatFormatting.GRAY));
			case ITEM_LIST -> List.of(EMPTY_FLUID);
		};
	}

	private static List<Component> getFluidTooltip (FluidStack stack) {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.level == null) {
			return List.of(stack.getHoverName());
		}

		return stack.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
	}

	public enum TooltipMode {
		SHOW_AMOUNT,
		SHOW_AMOUNT_AND_CAPACITY,
		ITEM_LIST
	}
}
