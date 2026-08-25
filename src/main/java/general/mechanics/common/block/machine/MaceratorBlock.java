package general.mechanics.common.block.machine;

import general.api.crafting.*;
import general.api.machine.MachineBlock;
import general.api.model.IConfigurableMachineModel;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.MaceratorBlockEntity;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;

public class MaceratorBlock extends MachineBlock<MaceratorBlockEntity> implements IConfigurableMachineModel, RecipeDataProvider {

	public MaceratorBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON), MaceratorBlockEntity.class, MaceratorBlockEntity.MACHINE);
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/macerator/macerator");
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/macerator/macerator_lit");
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		macerate(context, Items.COPPER_INGOT, GenItems.COPPER_DUST, 160, "copper");
		macerate(context, Items.IRON_INGOT, GenItems.IRON_DUST, 200, "iron");
		macerate(context, Items.GOLD_INGOT, GenItems.GOLD_DUST, 140, "gold");
		macerate(context, Items.NETHERITE_INGOT, GenItems.NETHERITE_DUST, 400, "netherite");
		macerate(context, GenItems.STEEL.get(), GenItems.STEEL.get().getDustItem(), 200, "steel");
		macerate(context, GenItems.TITANIUM.get(), GenItems.TITANIUM.get().getDustItem(), 240, "titanium");
		macerate(context, GenItems.TUNGSTEN.get(), GenItems.TUNGSTEN.get().getDustItem(), 300, "tungsten");
		macerate(context, Items.BLAZE_ROD, 1, Items.BLAZE_POWDER, 3, Items.BLAZE_POWDER, 1, 0.05D, 350, "blaze_rod");
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return Items.IRON_INGOT;
	}

	public static MachineRecipeDefinition<NoRecipeData> getRecipeDefinition () {
		return GenRecipes.MACERATOR;
	}

	private static void macerate (RecipeGenerationContext context, ItemLike ingot, ItemLike dust, int duration, String material) {
		macerate(context, ingot, 1, dust, 1, dust, 1, 0.10D, duration, material);
	}

	private static void macerate (RecipeGenerationContext context, ItemLike input, int inputCount, ItemLike output, int outputCount, @Nullable ItemLike chanceOutput, int chanceOutputCount, double chanceOutputYield, int duration, String material) {
		if (chanceOutput != null)
			context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, input, inputCount).itemOutput(RecipeSlots.OUTPUT, output, outputCount).chanceItemOutput(RecipeSlots.CHANCE_OUTPUT, chanceOutput, chanceOutputCount, chanceOutputYield).duration(duration), "macerator/" + material + "_dust");
		else context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, input, inputCount).itemOutput(RecipeSlots.OUTPUT, output, outputCount).duration(duration), "macerator/" + material + "_dust");
	}

	public static final class RecipeSlots {

		public static final MachineRecipeSlot.ItemInput  INPUT         = MachineRecipeSlots.ITEM_INPUT;
		public static final MachineRecipeSlot.ItemOutput OUTPUT        = MachineRecipeSlots.ITEM_OUTPUT;
		public static final MachineRecipeSlot.ItemOutput CHANCE_OUTPUT = MachineRecipeSlots.itemOutput("chance_output");

		private RecipeSlots () {
		}
	}

}
