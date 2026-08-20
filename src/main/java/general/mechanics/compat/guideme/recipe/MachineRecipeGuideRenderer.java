package general.mechanics.compat.guideme.recipe;

import general.api.crafting.MachineRecipe;
import general.api.crafting.MachineRecipeDefinition;
import general.api.crafting.MachineRecipeSchema;
import guideme.document.LytSize;
import guideme.document.block.AlignItems;
import guideme.document.block.LytBlock;
import guideme.document.block.LytGuiSprite;
import guideme.document.block.LytHBox;
import guideme.document.block.LytParagraph;
import guideme.document.block.LytVBox;
import guideme.document.block.recipes.LytStandardRecipeBox;
import guideme.render.GuiAssets;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Builds GuideME recipe panels directly from a machine recipe schema. */
final class MachineRecipeGuideRenderer {

	private static final int MAX_ROWS = 3;
	private static final String DURATION_DESCRIPTION_ID = "guide.generalmechanics.machine_recipe.duration";

	private MachineRecipeGuideRenderer () {
	}

	static LytBlock create (MachineRecipeDefinition<?> definition, RecipeHolder<MachineRecipe> holder) {
		MachineRecipe recipe = holder.value();
		if (recipe.definition() != definition) {
			throw new IllegalArgumentException("Recipe " + holder.id().identifier() + " does not belong to GuideME category " + definition.id());
		}

		List<LytBlock> inputs = createInputs(definition.schema(), recipe);
		List<LytBlock> outputs = createOutputs(definition.schema(), recipe);

		var body = new LytHBox();
		body.setAlignItems(AlignItems.CENTER);
		body.setGap(2);
		body.setWrap(false);
		if (!inputs.isEmpty()) body.append(createGrid(inputs));
		body.append(new LytGuiSprite(GuiAssets.ARROW, new LytSize(24, 17)));
		if (!outputs.isEmpty()) body.append(createGrid(outputs));

		var builder = LytStandardRecipeBox.builder().title(Component.translatableWithFallback(definition.descriptionId(), definition.defaultEnglishName()).getString()).customBody(body);
		var stations = definition.craftingStations();
		if (!stations.isEmpty()) builder.icon(stations.getFirst());

		int seconds = Math.max(1, Mth.ceil(recipe.duration() / 20.0));
		builder.addBottom(LytParagraph.of(Component.translatableWithFallback(DURATION_DESCRIPTION_ID, "Time: %s s", seconds).getString()));
		return builder.build(holder);
	}

	private static List<LytBlock> createInputs (MachineRecipeSchema schema, MachineRecipe recipe) {
		var result = new ArrayList<LytBlock>();
		for (MachineRecipeSchema.Slot slot : schema.itemInputs()) {
			var ingredient = recipe.itemInputs().get(slot.name());
			if (ingredient == null) continue;
			List<ItemStack> stacks = ingredient.ingredient().items().map(item -> new ItemStack(item, ingredient.count())).toList();
			if (!stacks.isEmpty()) result.add(MachineRecipeResourceSlot.items(stacks));
		}
		for (MachineRecipeSchema.Slot slot : schema.fluidInputs()) {
			var ingredient = recipe.fluidInputs().get(slot.name());
			if (ingredient == null) continue;
			List<FluidStack> stacks = ingredient.ingredient().fluids().stream().map(fluid -> new FluidStack(fluid, ingredient.amount())).toList();
			if (!stacks.isEmpty()) result.add(MachineRecipeResourceSlot.fluids(stacks));
		}
		return result;
	}

	private static List<LytBlock> createOutputs (MachineRecipeSchema schema, MachineRecipe recipe) {
		var result = new ArrayList<LytBlock>();
		Map<String, ItemStack> itemOutputs = recipe.itemOutputs();
		Map<String, FluidStack> fluidOutputs = recipe.fluidOutputs();
		for (MachineRecipeSchema.Slot slot : schema.itemOutputs()) {
			ItemStack stack = itemOutputs.get(slot.name());
			if (stack != null && !stack.isEmpty()) result.add(MachineRecipeResourceSlot.items(List.of(stack)));
		}
		for (MachineRecipeSchema.Slot slot : schema.fluidOutputs()) {
			FluidStack stack = fluidOutputs.get(slot.name());
			if (stack != null && !stack.isEmpty()) result.add(MachineRecipeResourceSlot.fluids(List.of(stack)));
		}
		return result;
	}

	private static LytBlock createGrid (List<LytBlock> slots) {
		var grid = new LytHBox();
		grid.setAlignItems(AlignItems.CENTER);
		grid.setWrap(false);

		int rows = Math.min(slots.size(), MAX_ROWS);
		int columns = (slots.size() + rows - 1) / rows;
		for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
			var column = new LytVBox();
			column.setAlignItems(AlignItems.CENTER);
			for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
				int index = columnIndex * rows + rowIndex;
				if (index < slots.size()) column.append(slots.get(index));
			}
			grid.append(column);
		}
		return grid;
	}

}
