package general.mechanics.common.block.machine;

import general.api.crafting.*;
import general.api.item.materials.IngotItem;
import general.api.machine.MachineBlock;
import general.api.model.IConfigurableMachineModel;
import general.api.resources.Resource;
import general.mechanics.common.block.entity.StampingPressBlockEntity;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import org.jspecify.annotations.Nullable;

public class StampingPressBlock extends MachineBlock<StampingPressBlockEntity> implements IConfigurableMachineModel, RecipeDataProvider {

	public StampingPressBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON), StampingPressBlockEntity.class, StampingPressBlockEntity.MACHINE);
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/stamping_press/stamping_press");
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/stamping_press/stamping_press_lit");
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {
		materialRecipes(context, Items.COPPER_INGOT, GenItems.COPPER_PLATE, GenItems.COPPER_NUGGET, GenItems.COPPER_GEAR, 160, "copper");
		materialRecipes(context, Items.IRON_INGOT, GenItems.IRON_PLATE, Items.IRON_NUGGET, GenItems.IRON_GEAR, 200, "iron");
		materialRecipes(context, Items.GOLD_INGOT, GenItems.GOLD_PLATE, Items.GOLD_NUGGET, GenItems.GOLD_GEAR, 140, "gold");
		materialRecipes(context, Items.NETHERITE_INGOT, GenItems.NETHERITE_PLATE, null, GenItems.NETHERITE_GEAR, 400, "netherite");
		materialRecipes(context, GenItems.STEEL.get(), 200, "steel");
		materialRecipes(context, GenItems.TITANIUM.get(), 240, "titanium");
		materialRecipes(context, GenItems.TUNGSTEN.get(), 300, "tungsten");
	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return GenItems.STEEL.get();
	}

	public static MachineRecipeDefinition<NoRecipeData> getRecipeDefinition () {
		return GenRecipes.STAMPING_PRESS;
	}

	private static void materialRecipes (RecipeGenerationContext context, IngotItem material, int baseDuration, String name) {
		materialRecipes(context, material, material.getPlateItem(), material.getNuggetItem(), material.getGearItem(), baseDuration, name);
	}

	private static void materialRecipes (RecipeGenerationContext context, ItemLike ingot, ItemLike plate, @Nullable ItemLike nugget, ItemLike gear, int baseDuration, String name) {
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, ingot, 1).itemOutput(RecipeSlots.OUTPUT, plate, 1).duration(baseDuration), "stamping_press/" + name + "_plate");
		if (nugget != null) {
			context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, ingot, 1).itemInput(RecipeSlots.DIE, GenItems.NUGGET_DIE.get(), 1).itemOutput(RecipeSlots.OUTPUT, nugget, 9).duration(Math.max(1, baseDuration / 2)), "stamping_press/" + name + "_nuggets");
		}
		context.save(getRecipeDefinition().recipeBuilder().itemInput(RecipeSlots.INPUT, plate, 4).itemInput(RecipeSlots.DIE, GenItems.GEAR_DIE.get(), 1).itemOutput(RecipeSlots.OUTPUT, gear, 1).duration(baseDuration * 2), "stamping_press/" + name + "_gear");
	}

	public static final class RecipeSlots {

		public static final MachineRecipeSlot.ItemInput  INPUT  = MachineRecipeSlots.ITEM_INPUT;
		public static final MachineRecipeSlot.ItemInput  DIE    = MachineRecipeSlots.itemInput("die");
		public static final MachineRecipeSlot.ItemOutput OUTPUT = MachineRecipeSlots.ITEM_OUTPUT;

		private RecipeSlots () {
		}
	}
}
