package general.mechanics.common.block.machine;

import general.api.crafting.*;
import general.api.machine.MachineBlock;
import general.api.model.IConfigurableMachineModel;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.ElectricFurnaceBlockEntity;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public class ElectricFurnaceBlock extends MachineBlock<ElectricFurnaceBlockEntity> implements IConfigurableMachineModel, RecipeDataProvider {

	public ElectricFurnaceBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON), ElectricFurnaceBlockEntity.class, ElectricFurnaceBlockEntity.MACHINE);
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, GenItems.TUNGSTEN.get().getRawItem(), 1).itemOutput(RecipeSlots.OUTPUT_1, GenItems.TUNGSTEN.get(), 1).duration(250), "electric_furnace/tungsten_ingot_from_raw_tungsten");
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, GenItems.TUNGSTEN.get().getDustItem(), 1).itemOutput(RecipeSlots.OUTPUT_1, GenItems.TUNGSTEN.get(), 2).duration(300), "electric_furnace/tungsten_ingot_from_2_dust_tungsten");

		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, GenItems.TITANIUM.get().getRawItem(), 1).itemInput(RecipeSlots.CATALYST, Items.GUNPOWDER, 2).itemOutput(RecipeSlots.OUTPUT_1, GenItems.TITANIUM.get(), 1).duration(250), "electric_furnace/titanium_ingot_from_raw_titanium");
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, GenItems.TITANIUM.get().getDustItem(), 1).itemInput(RecipeSlots.CATALYST, Items.GUNPOWDER, 2).itemOutput(RecipeSlots.OUTPUT_1, GenItems.TITANIUM.get(), 2).duration(300), "electric_furnace/titanium_ingot_from_2_dust_titanium");
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return Blocks.FURNACE;
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/electric_furnace/electric_furnace");
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/electric_furnace/electric_furnace_lit");
	}

	public static MachineRecipeDefinition<NoRecipeData> getRecipeDefinition () {
		return GenRecipes.ELECTRIC_FURNACE;
	}

	public static final class RecipeSlots {

		public static final MachineRecipeSlot.ItemInput  INPUT    = MachineRecipeSlots.ITEM_INPUT;
		public static final MachineRecipeSlot.ItemInput  CATALYST = MachineRecipeSlots.itemInput("catalyst");
		public static final MachineRecipeSlot.ItemOutput OUTPUT_1 = MachineRecipeSlots.ITEM_OUTPUT;
		public static final MachineRecipeSlot.ItemOutput OUTPUT_2 = MachineRecipeSlots.itemOutput(2);
		public static final MachineRecipeSlot.ItemOutput OUTPUT_3 = MachineRecipeSlots.itemOutput(3);
		public static final MachineRecipeSlot.ItemOutput OUTPUT_4 = MachineRecipeSlots.itemOutput(4);

		private RecipeSlots () {
		}
	}
}
