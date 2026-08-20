package general.mechanics.registries;

import general.api.definitions.MultiblockDefinition;
import general.api.mod.GenAPI;
import general.api.multiblock.Multiblock;
import general.api.multiblock.MultiblockElement;
import general.api.multiblock.MultiblockPattern;
import general.api.registry.GenRegistries;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GenMultiblocks {

	public static final DeferredRegister<Multiblock> REGISTRY    = DeferredRegister.create(GenRegistries.MULTIBLOCKS, GenAPI.getModId());
	public static final Registry<Multiblock>         MULTIBLOCKS = REGISTRY.makeRegistry(builder -> {
	});

	public static final MultiblockDefinition COKE_OVEN = register("coke_oven", MultiblockPattern.builder().where('F', MultiblockElement.block(GenBlocks.COKE_OVEN_BRICKS)).where('C', MultiblockElement.block(GenBlocks.COKE_OVEN_CONTROLLER)).where('.', MultiblockElement.air()).where('#', MultiblockElement.any())

			.anchor('C')

			.layer("FFF", "FFF", "FFF")

			.layer("FFF", "F.F", "FCF")

			.layer("#F#", "F.F", "#F#")

			.build());

	public static MultiblockDefinition register (String name, MultiblockPattern pattern) {
		return new MultiblockDefinition(name, REGISTRY.register(name, () -> new Multiblock(pattern)));
	}

}
