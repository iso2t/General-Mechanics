package general.mechanics.compat.guideme.recipe;

import guideme.document.LytRect;
import guideme.document.block.LytBlock;
import guideme.document.interaction.GuideTooltip;
import guideme.document.interaction.InteractiveElement;
import guideme.document.interaction.ItemTooltip;
import guideme.document.interaction.TextTooltip;
import guideme.layout.LayoutContext;
import guideme.render.GuiAssets;
import guideme.render.RenderContext;
import guideme.siteexport.ExportableResourceProvider;
import guideme.siteexport.ResourceExporter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * GuideME slot capable of displaying either cycling item stacks or fluid stacks.
 */
final class MachineRecipeResourceSlot extends LytBlock implements InteractiveElement, ExportableResourceProvider {

	private static final int CONTENT_SIZE  = 16;
	private static final int PADDING       = 1;
	private static final int OUTER_SIZE    = CONTENT_SIZE + PADDING * 2;
	private static final int CYCLE_TIME_MS = 2_000;

	private final List<ItemStack>  items;
	private final List<FluidStack> fluids;

	private MachineRecipeResourceSlot (List<ItemStack> items, List<FluidStack> fluids) {
		this.items = items;
		this.fluids = fluids;
	}

	static MachineRecipeResourceSlot items (List<ItemStack> stacks) {
		List<ItemStack> copies = stacks.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy).toList();
		if (copies.isEmpty()) throw new IllegalArgumentException("A GuideME item recipe slot requires at least one stack");
		return new MachineRecipeResourceSlot(copies, List.of());
	}

	static MachineRecipeResourceSlot fluids (List<FluidStack> stacks) {
		List<FluidStack> copies = stacks.stream().filter(stack -> !stack.isEmpty()).map(FluidStack::copy).toList();
		if (copies.isEmpty()) throw new IllegalArgumentException("A GuideME fluid recipe slot requires at least one stack");
		return new MachineRecipeResourceSlot(List.of(), copies);
	}

	@Override
	protected LytRect computeLayout (LayoutContext context, int x, int y, int availableWidth) {
		return new LytRect(x, y, OUTER_SIZE, OUTER_SIZE);
	}

	@Override
	protected void onLayoutMoved (int deltaX, int deltaY) {
	}

	@Override
	public void render (RenderContext context) {
		context.fillIcon(bounds, GuiAssets.SLOT);
		if (!items.isEmpty()) {
			context.renderItem(displayed(items), bounds.x() + PADDING, bounds.y() + PADDING, 1, CONTENT_SIZE, CONTENT_SIZE);
		} else {
			context.renderFluid(displayed(fluids), bounds.x() + PADDING, bounds.y() + PADDING, CONTENT_SIZE, CONTENT_SIZE);
		}
	}

	@Override
	public Optional<GuideTooltip> getTooltip (float x, float y) {
		if (!items.isEmpty()) return Optional.of(new ItemTooltip(displayed(items), ItemStack.EMPTY));
		FluidStack stack = displayed(fluids);
		String amount = NumberFormat.getIntegerInstance().format(stack.getAmount()) + " mB";
		return Optional.of(new TextTooltip(stack.getHoverName(), Component.literal(amount).withStyle(ChatFormatting.GRAY)));
	}

	@Override
	public void exportResources (ResourceExporter exporter) {
		for (ItemStack stack : items) exporter.referenceItem(stack);
		for (FluidStack stack : fluids) exporter.referenceFluid(stack.getFluid());
	}

	private static <T> T displayed (List<T> values) {
		long cycle = System.nanoTime() / TimeUnit.MILLISECONDS.toNanos(CYCLE_TIME_MS);
		return values.get((int) (cycle % values.size()));
	}

}
