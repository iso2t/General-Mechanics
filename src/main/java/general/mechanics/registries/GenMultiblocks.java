package general.mechanics.registries;

import general.api.definitions.MultiblockDefinition;
import general.api.mod.GenAPI;
import general.api.multiblock.*;
import general.api.network.NetworkServices;
import general.api.registry.GenRegistries;
import general.mechanics.common.block.CokeOvenController;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GenMultiblocks {

	public static final DeferredRegister<Multiblock> REGISTRY    = DeferredRegister.create(GenRegistries.MULTIBLOCKS, GenAPI.getModId());
	public static final Registry<Multiblock>         MULTIBLOCKS = REGISTRY.makeRegistry(builder -> {
	});

	public static final MultiblockDefinition COKE_OVEN = register("coke_oven", Multiblock.builder(MultiblockPattern.builder().whereHatchable('F', MultiblockElement.block(GenBlocks.COKE_OVEN_BRICKS)).where('C', MultiblockElement.block(GenBlocks.COKE_OVEN_CONTROLLER)).where('.', MultiblockElement.air()).where('#', MultiblockElement.any()).where('L', MultiblockElement.block(Blocks.LAVA))

					.anchor('C')

					.layer("FFF", "FFF", "FFF")

					.layer("FFF", "FLF", "FCF")

					.layer("#F#", "F#F", "#F#")

					.build())

			.hatch(MultiblockHatchDefinition.builder("item_input", GenBlocks.ITEM_INPUT_HATCH).count(HatchCount.atMost(1)).itemInsert(CokeOvenController.RecipeSlots.INPUT).build()).hatch(MultiblockHatchDefinition.builder("item_output", GenBlocks.ITEM_OUTPUT_HATCH).count(HatchCount.atMost(1)).itemExtract(CokeOvenController.RecipeSlots.OUTPUT).build()).hatch(MultiblockHatchDefinition.builder("fluid_output", GenBlocks.FLUID_OUTPUT_HATCH).count(HatchCount.atMost(1)).fluidExtract(CokeOvenController.RecipeSlots.CREOSOTE).build()).hatch(MultiblockHatchDefinition.builder("network", GenBlocks.NETWORK_HATCH).count(HatchCount.atMost(1)).itemInsert(CokeOvenController.RecipeSlots.INPUT).itemExtract(CokeOvenController.RecipeSlots.OUTPUT).fluidExtract(CokeOvenController.RecipeSlots.CREOSOTE).network(NetworkServices.ITEM, NetworkServices.FLUID).build()).build());

	public static MultiblockDefinition register (String name, MultiblockPattern pattern) {
		return register(name, new Multiblock(pattern));
	}

	public static MultiblockDefinition register (String name, Multiblock multiblock) {
		return new MultiblockDefinition(name, REGISTRY.register(name, () -> multiblock));
	}

}
