package general.mechanics.registries;

import general.api.crafting.MachineRecipeDefinition;
import general.api.crafting.MachineRecipeRegistry;
import general.api.crafting.MachineRecipeSchema;
import general.api.crafting.NoRecipeData;
import general.api.mod.GenAPI;
import general.mechanics.common.block.machine.CokeOvenController;
import general.mechanics.common.block.machine.ElectricFurnaceBlock;
import general.mechanics.common.block.machine.StampingPressBlock;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;

/**
 * Owns the paired machine recipe registries. Individual machines retain their
 * schema and type declaration; this bootstrap only ensures those classes are
 * initialized before the deferred registries are attached.
 */
public final class GenRecipes {

	public static final MachineRecipeRegistry REGISTRY = MachineRecipeRegistry.create(GenAPI.getModId());

	public static final MachineRecipeDefinition<NoRecipeData> COKE_OVEN        = REGISTRY.register("coke_oven", MachineRecipeSchema.builder().itemInput(CokeOvenController.RecipeSlots.INPUT).itemOutput(CokeOvenController.RecipeSlots.OUTPUT).fluidOutput(CokeOvenController.RecipeSlots.CREOSOTE).build()).craftingStation(() -> GenBlocks.COKE_OVEN_CONTROLLER);
	public static final MachineRecipeDefinition<NoRecipeData> STAMPING_PRESS   = REGISTRY.register("stamping_press", MachineRecipeSchema.builder().itemInput(StampingPressBlock.RecipeSlots.INPUT).itemOutput(StampingPressBlock.RecipeSlots.OUTPUT).build()).craftingStation(() -> GenBlocks.STAMPING_PRESS);
	public static final MachineRecipeDefinition<NoRecipeData> ELECTRIC_FURNACE = REGISTRY.register("electric_furnace", MachineRecipeSchema.builder().itemInput(ElectricFurnaceBlock.RecipeSlots.INPUT).optionalItemInput(ElectricFurnaceBlock.RecipeSlots.CATALYST).itemOutput(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1).optionalItemOutput(ElectricFurnaceBlock.RecipeSlots.OUTPUT_2).optionalItemOutput(ElectricFurnaceBlock.RecipeSlots.OUTPUT_3).optionalItemOutput(ElectricFurnaceBlock.RecipeSlots.OUTPUT_4).build()).craftingStation(() -> GenBlocks.ELECTRIC_FURNACE).interchangeableItemOutputs(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, ElectricFurnaceBlock.RecipeSlots.OUTPUT_2, ElectricFurnaceBlock.RecipeSlots.OUTPUT_3, ElectricFurnaceBlock.RecipeSlots.OUTPUT_4).cookingRecipes(RecipeType.SMELTING, ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, input -> input.item(ElectricFurnaceBlock.RecipeSlots.CATALYST).isEmpty());

	private GenRecipes () {
	}

	public static void register (IEventBus bus) {
		REGISTRY.register(bus);
	}

}
