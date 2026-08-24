package general.mechanics.registries;

import general.api.definitions.MultiblockDefinition;
import general.api.mod.GenAPI;
import general.api.multiblock.*;
import general.api.network.NetworkServices;
import general.api.registry.GenRegistries;
import general.api.tag.CoreTags;
import general.api.transfer.ResourceIoMode;
import general.mechanics.common.block.machine.CokeOvenController;
import general.mechanics.common.block.machine.ElectricFurnaceBlock;
import general.mechanics.common.block.machine.MaceratorBlock;
import general.mechanics.common.block.machine.StampingPressBlock;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenMultiblocks {

	public static final DeferredRegister<Multiblock> REGISTRY             = DeferredRegister.create(GenRegistries.MULTIBLOCKS, GenAPI.getModId());
	public static final Registry<Multiblock>         MULTIBLOCKS_REGISTRY = REGISTRY.makeRegistry(builder -> {
	});

	private static final List<MultiblockDefinition> MULTIBLOCKS = new ArrayList<>();

	public static final MultiblockDefinition COKE_OVEN = register("Primitive Coke Oven", Multiblock.builder(MultiblockPattern.builder().whereHatchable('F', MultiblockElement.block(GenBlocks.COKE_OVEN_BRICKS)).where('C', MultiblockElement.block(GenBlocks.COKE_OVEN_CONTROLLER)).where('.', MultiblockElement.air()).where('#', MultiblockElement.any()).where('L', MultiblockElement.block(Blocks.LAVA)).anchor('C').layer("FFF", "FFF", "FFF").layer("FFF", "FLF", "FCF").layer("#F#", "F#F", "#F#").build()).hatch(MultiblockHatchDefinition.builder("item_input", GenBlocks.ITEM_INPUT_HATCH).count(HatchCount.atMost(1)).itemInsert(CokeOvenController.RecipeSlots.INPUT).build()).hatch(MultiblockHatchDefinition.builder("item_output", GenBlocks.ITEM_OUTPUT_HATCH).count(HatchCount.atMost(1)).itemExtract(CokeOvenController.RecipeSlots.OUTPUT).build()).hatch(MultiblockHatchDefinition.builder("fluid_output", GenBlocks.FLUID_OUTPUT_HATCH).count(HatchCount.atMost(1)).fluidExtract(CokeOvenController.RecipeSlots.CREOSOTE).build()).hatch(MultiblockHatchDefinition.builder("network", GenBlocks.NETWORK_HATCH).count(HatchCount.atMost(1)).itemInsert(CokeOvenController.RecipeSlots.INPUT).itemExtract(CokeOvenController.RecipeSlots.OUTPUT).fluidExtract(CokeOvenController.RecipeSlots.CREOSOTE).network(NetworkServices.ITEM, NetworkServices.FLUID).build()).build());

	public static final MultiblockDefinition ELECTRIC_FURNACE = register("Furnace Factory", Multiblock.builder(MultiblockPattern.builder().whereHatchable('B', MultiblockElement.block(GenBlocks.MACHINE_FRAME)).where('C', MultiblockElement.block(GenBlocks.ELECTRIC_FURNACE)).whereUniform('U', CoreTags.Blocks.CORE_MATRICES, GenBlocks.MACHINE_FRAME).where('I', MultiblockElement.block(GenBlocks.HEATING_ELEMENT)).whereHatchable('T', GenBlocks.MACHINE_CASING).anchor('C').layer("BBB", "BBB", "BCB").layer("UUU", "UIU", "UUU").layer("UUU", "UIU", "UUU").layer("UUU", "UIU", "UUU").layer("TTT", "TTT", "TTT").build()).hatch(MultiblockHatchDefinition.builder("item_input", GenBlocks.ITEM_INPUT_HATCH).count(1).itemInsert(ElectricFurnaceBlock.RecipeSlots.INPUT, ElectricFurnaceBlock.RecipeSlots.CATALYST).build()).hatch(MultiblockHatchDefinition.builder("item_output", GenBlocks.ITEM_OUTPUT_HATCH).count(1).itemExtract(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, ElectricFurnaceBlock.RecipeSlots.OUTPUT_2, ElectricFurnaceBlock.RecipeSlots.OUTPUT_3, ElectricFurnaceBlock.RecipeSlots.OUTPUT_4).build()).hatch(MultiblockHatchDefinition.builder("energy", GenBlocks.POWER_HATCH).count(1).energy(ResourceIoMode.INSERT).build()).hatch(MultiblockHatchDefinition.builder("network", GenBlocks.NETWORK_HATCH).count(HatchCount.atMost(1)).itemInsert(ElectricFurnaceBlock.RecipeSlots.INPUT).itemInsert(ElectricFurnaceBlock.RecipeSlots.CATALYST).itemExtract(ElectricFurnaceBlock.RecipeSlots.OUTPUT_1, ElectricFurnaceBlock.RecipeSlots.OUTPUT_2, ElectricFurnaceBlock.RecipeSlots.OUTPUT_3, ElectricFurnaceBlock.RecipeSlots.OUTPUT_4).network(NetworkServices.ITEM, NetworkServices.ENERGY, NetworkServices.DATA).build()).build());

	public static final MultiblockDefinition STAMPING_PRESS = register("Stamping Factory", Multiblock.builder(MultiblockPattern.builder().whereHatchable('B', MultiblockElement.block(GenBlocks.MACHINE_FRAME)).where('C', MultiblockElement.block(GenBlocks.STAMPING_PRESS)).whereUniform('U', CoreTags.Blocks.CORE_MATRICES, GenBlocks.MACHINE_FRAME).where('.', MultiblockElement.air()).whereHatchable('T', GenBlocks.MACHINE_CASING).anchor('C').layer("BBB", "BBB", "BCB").layer("UUU", "U.U", "UUU").layer("TTT", "TTT", "TTT").build()).hatch(MultiblockHatchDefinition.builder("item_input", GenBlocks.ITEM_INPUT_HATCH).count(1).itemInsert(StampingPressBlock.RecipeSlots.INPUT, StampingPressBlock.RecipeSlots.DIE).build()).hatch(MultiblockHatchDefinition.builder("item_output", GenBlocks.ITEM_OUTPUT_HATCH).count(1).itemExtract(StampingPressBlock.RecipeSlots.OUTPUT).build()).hatch(MultiblockHatchDefinition.builder("energy", GenBlocks.POWER_HATCH).count(1).energy(ResourceIoMode.INSERT).build()).hatch(MultiblockHatchDefinition.builder("network", GenBlocks.NETWORK_HATCH).count(HatchCount.atMost(1)).itemInsert(StampingPressBlock.RecipeSlots.INPUT, StampingPressBlock.RecipeSlots.DIE).itemExtract(StampingPressBlock.RecipeSlots.OUTPUT).network(NetworkServices.ITEM, NetworkServices.ENERGY, NetworkServices.DATA).build()).build());

	public static final MultiblockDefinition MACERATOR = register("Macerator Factory", Multiblock.builder(MultiblockPattern.builder().whereHatchable('B', MultiblockElement.block(GenBlocks.MACHINE_FRAME)).where('C', MultiblockElement.block(GenBlocks.MACERATOR)).whereUniform('U', CoreTags.Blocks.CORE_MATRICES, GenBlocks.MACHINE_FRAME).where('.', MultiblockElement.air()).whereHatchable('T', GenBlocks.MACHINE_CASING).anchor('C').layer("BBB", "BBB", "BCB").layer("UUU", "U.U", "UUU").layer("TTT", "TTT", "TTT").build()).hatch(MultiblockHatchDefinition.builder("item_input", GenBlocks.ITEM_INPUT_HATCH).count(1).itemInsert(MaceratorBlock.RecipeSlots.INPUT).build()).hatch(MultiblockHatchDefinition.builder("item_output", GenBlocks.ITEM_OUTPUT_HATCH).count(1).itemExtract(MaceratorBlock.RecipeSlots.OUTPUT, MaceratorBlock.RecipeSlots.CHANCE_OUTPUT).build()).hatch(MultiblockHatchDefinition.builder("energy", GenBlocks.POWER_HATCH).count(1).energy(ResourceIoMode.INSERT).build()).hatch(MultiblockHatchDefinition.builder("network", GenBlocks.NETWORK_HATCH).count(HatchCount.atMost(1)).itemInsert(MaceratorBlock.RecipeSlots.INPUT).itemExtract(MaceratorBlock.RecipeSlots.OUTPUT, MaceratorBlock.RecipeSlots.CHANCE_OUTPUT).network(NetworkServices.ITEM, NetworkServices.ENERGY, NetworkServices.DATA).build()).build());

	public static List<MultiblockDefinition> getMultiblocks () {
		return Collections.unmodifiableList(MULTIBLOCKS);
	}

	public static MultiblockDefinition register (String name, MultiblockPattern pattern) {
		return register(name, new Multiblock(pattern));
	}

	public static MultiblockDefinition register (String name, Multiblock multiblock) {
		var definition = new MultiblockDefinition(name, REGISTRY.register(name.toLowerCase().replace(" ", ""), () -> multiblock));
		MULTIBLOCKS.add(definition);
		return definition;
	}

}
