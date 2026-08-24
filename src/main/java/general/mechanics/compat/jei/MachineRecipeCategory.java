package general.mechanics.compat.jei;

import general.api.crafting.MachineRecipe;
import general.api.crafting.MachineRecipeDefinition;
import general.api.crafting.MachineRecipeSchema;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.text.NumberFormat;

/**
 * Default JEI category generated entirely from a machine recipe schema.
 */
final class MachineRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MachineRecipe>> {

	private static final int    SLOT_SIZE                = 18;
	private static final int    MAX_ROWS                 = 3;
	private static final int    ARROW_WIDTH              = 24;
	private static final int    ARROW_AREA_WIDTH         = 42;
	private static final int    MIN_WIDTH                = 82;
	private static final int    MIN_HEIGHT               = 36;
	private static final String TIME_SECONDS_KEY         = "gui.generalmechanics.machine_recipe.time.seconds";
	private static final String TIME_MINUTES_SECONDS_KEY = "gui.generalmechanics.machine_recipe.time.minutes_seconds";
	private static final String OUTPUT_CHANCE_KEY        = "gui.generalmechanics.machine_recipe.output_chance";

	private final MachineRecipeDefinition<?> definition;
	private final List<ItemLike>             craftingStations;
	private final int                        inputCount;
	private final int                        outputCount;
	private final int                        inputRows;
	private final int                        outputRows;
	private final int                        inputStartX;
	private final int                        outputStartX;
	private final int                        arrowX;
	private final int                        arrowY;

	MachineRecipeCategory (IGuiHelper guiHelper, MachineRecipeDefinition<?> definition) {
		this(guiHelper, definition, definition.craftingStations());
	}

	private MachineRecipeCategory (IGuiHelper guiHelper, MachineRecipeDefinition<?> definition, List<ItemLike> craftingStations) {
		super(createRecipeType(definition), createTitle(definition), craftingStations.isEmpty() ? guiHelper.createBlankDrawable(16, 16) : guiHelper.createDrawableItemLike(craftingStations.getFirst()), calculateWidth(definition.schema()), calculateHeight(definition.schema()));
		this.definition = Objects.requireNonNull(definition, "definition");
		this.craftingStations = List.copyOf(craftingStations);

		MachineRecipeSchema schema = definition.schema();
		this.inputCount = inputCount(schema);
		this.outputCount = outputCount(schema);
		this.inputRows = rows(inputCount);
		this.outputRows = rows(outputCount);

		int inputWidth = columns(inputCount) * SLOT_SIZE;
		int outputWidth = columns(outputCount) * SLOT_SIZE;
		int contentWidth = inputWidth + ARROW_AREA_WIDTH + outputWidth;
		int contentStartX = (getWidth() - contentWidth) / 2;
		this.inputStartX = contentStartX;
		this.outputStartX = contentStartX + inputWidth + ARROW_AREA_WIDTH;
		this.arrowX = contentStartX + inputWidth + (ARROW_AREA_WIDTH - ARROW_WIDTH) / 2;
		this.arrowY = (getHeight() - 27) / 2;
	}

	MachineRecipeDefinition<?> definition () {
		return definition;
	}

	List<ItemLike> craftingStations () {
		return craftingStations;
	}

	@Override
	public void setRecipe (@NonNull IRecipeLayoutBuilder builder, RecipeHolder<MachineRecipe> holder, @NonNull IFocusGroup focuses) {
		MachineRecipe recipe = holder.value();
		if (recipe.definition() != definition) throw new IllegalArgumentException("Recipe " + holder.id().identifier() + " does not belong to JEI category " + definition.id());

		Map<String, ItemStack> itemOutputs = recipe.itemOutputs();
		Map<String, FluidStack> fluidOutputs = recipe.fluidOutputs();
		int inputIndex = 0;
		int outputIndex = 0;

		for (MachineRecipeSchema.Slot slot : definition.schema().itemInputs()) {
			addItemInput(builder, slot.name(), recipe.itemInputs().get(slot.name()), inputIndex++);
		}
		for (MachineRecipeSchema.Slot slot : definition.schema().fluidInputs()) {
			addFluidInput(builder, slot.name(), recipe.fluidInputs().get(slot.name()), inputIndex++);
		}
		for (MachineRecipeSchema.Slot slot : definition.schema().itemOutputs()) {
			ItemStack stack = itemOutputs.get(slot.name());
			addItemOutput(builder, slot.name(), stack, stack == null ? 1.0D : recipe.itemOutputChance(slot.name()), outputIndex++);
		}
		for (MachineRecipeSchema.Slot slot : definition.schema().fluidOutputs()) {
			FluidStack stack = fluidOutputs.get(slot.name());
			addFluidOutput(builder, slot.name(), stack, stack == null ? 1.0D : recipe.fluidOutputChance(slot.name()), outputIndex++);
		}
	}

	@Override
	public void createRecipeExtras (IRecipeExtrasBuilder builder, RecipeHolder<MachineRecipe> holder, @NonNull IFocusGroup focuses) {
		int duration = holder.value().duration();
		builder.addAnimatedRecipeArrow(duration).setPosition(arrowX, arrowY);

		int totalSeconds = Math.max(1, Mth.ceil(duration / 20.0));
		Component time = totalSeconds < 60 ? Component.translatableWithFallback(TIME_SECONDS_KEY, "%ss", totalSeconds) : Component.translatableWithFallback(TIME_MINUTES_SECONDS_KEY, "%sm %ss", totalSeconds / 60, totalSeconds % 60);
		builder.addText(time, ARROW_AREA_WIDTH, 10).setPosition(arrowX - (ARROW_AREA_WIDTH - ARROW_WIDTH) / 2, arrowY + 18).setTextAlignment(HorizontalAlignment.CENTER).setColor(0xFF404040);
	}

	@Override
	public boolean isHandled (RecipeHolder<MachineRecipe> holder) {
		return holder.value().definition() == definition;
	}

	@SuppressWarnings("deprecation")
	private void addItemInput (IRecipeLayoutBuilder builder, String name, SizedIngredient ingredient, int index) {
		if (ingredient == null) return;
		List<ItemStack> stacks = ingredient.ingredient().items().map(item -> new ItemStack(item, ingredient.count())).toList();
		if (stacks.isEmpty()) return;
		Position position = inputPosition(index);
		builder.addInputSlot(position.x(), position.y()).setStandardSlotBackground().setSlotName("item_input/" + name).addItemStacks(stacks);
	}

	private void addFluidInput (IRecipeLayoutBuilder builder, String name, SizedFluidIngredient ingredient, int index) {
		if (ingredient == null) return;
		List<FluidStack> stacks = ingredient.ingredient().fluids().stream().map(fluid -> new FluidStack(fluid, ingredient.amount())).toList();
		if (stacks.isEmpty()) return;
		Position position = inputPosition(index);
		builder.addInputSlot(position.x(), position.y()).setStandardSlotBackground().setSlotName("fluid_input/" + name).setFluidRenderer(ingredient.amount(), true, 16, 16).addIngredients(NeoForgeTypes.FLUID_STACK, stacks);
	}

	private void addItemOutput (IRecipeLayoutBuilder builder, String name, ItemStack stack, double chance, int index) {
		if (stack == null || stack.isEmpty()) return;
		Position position = outputPosition(index);
		var slot = builder.addOutputSlot(position.x(), position.y()).setStandardSlotBackground().setSlotName("item_output/" + name).add(stack);
		addChanceTooltip(slot, chance);
	}

	private void addFluidOutput (IRecipeLayoutBuilder builder, String name, FluidStack stack, double chance, int index) {
		if (stack == null || stack.isEmpty()) return;
		Position position = outputPosition(index);
		var slot = builder.addOutputSlot(position.x(), position.y()).setStandardSlotBackground().setSlotName("fluid_output/" + name).setFluidRenderer(stack.getAmount(), true, 16, 16).add(NeoForgeTypes.FLUID_STACK, stack);
		addChanceTooltip(slot, chance);
	}

	private static void addChanceTooltip (mezz.jei.api.gui.builder.IRecipeSlotBuilder slot, double chance) {
		if (chance >= 1.0D) return;
		Component tooltip = Component.translatableWithFallback(OUTPUT_CHANCE_KEY, "Chance: %s", NumberFormat.getPercentInstance().format(chance)).withStyle(ChatFormatting.GRAY);
		slot.addRichTooltipCallback((view, builder) -> builder.add(tooltip));
	}

	private Position inputPosition (int index) {
		return position(inputStartX, inputRows, inputCount, index);
	}

	private Position outputPosition (int index) {
		return position(outputStartX, outputRows, outputCount, index);
	}

	private Position position (int startX, int rowCount, int count, int index) {
		if (index < 0 || index >= count) throw new IndexOutOfBoundsException(index);
		int column = index / rowCount;
		int row = index % rowCount;
		int groupHeight = rowCount * SLOT_SIZE;
		return new Position(startX + column * SLOT_SIZE, (getHeight() - groupHeight) / 2 + row * SLOT_SIZE);
	}

	private static IRecipeHolderType<MachineRecipe> createRecipeType (MachineRecipeDefinition<?> definition) {
		return IRecipeHolderType.create(definition.type());
	}

	private static Component createTitle (MachineRecipeDefinition<?> definition) {
		return Component.translatableWithFallback(definition.descriptionId(), definition.defaultEnglishName());
	}

	private static int calculateWidth (MachineRecipeSchema schema) {
		return Math.max(MIN_WIDTH, columns(inputCount(schema)) * SLOT_SIZE + ARROW_AREA_WIDTH + columns(outputCount(schema)) * SLOT_SIZE);
	}

	private static int calculateHeight (MachineRecipeSchema schema) {
		return Math.max(MIN_HEIGHT, Math.max(rows(inputCount(schema)), rows(outputCount(schema))) * SLOT_SIZE);
	}

	private static int inputCount (MachineRecipeSchema schema) {
		return schema.itemInputs().size() + schema.fluidInputs().size();
	}

	private static int outputCount (MachineRecipeSchema schema) {
		return schema.itemOutputs().size() + schema.fluidOutputs().size();
	}

	private static int rows (int count) {
		return Math.min(count, MAX_ROWS);
	}

	private static int columns (int count) {
		int rows = rows(count);
		return rows == 0 ? 0 : (count + rows - 1) / rows;
	}

	private record Position(int x, int y) {
	}
}
