package general.mechanics.registries;

import com.google.common.base.Preconditions;
import general.api.block.BlockEntityTypeOwner;
import general.api.capabilities.ICapabilityRegistrar;
import general.api.definitions.BlockDefinition;
import general.api.definitions.BlockEntityDefinition;
import general.api.mod.GenAPI;
import general.mechanics.common.block.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GenBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GenAPI.getModId());

	private static final List<BlockEntityDefinition<?>> BLOCK_ENTITIES = new ArrayList<>();

	public static final BlockEntityDefinition<CableBlockEntity>              CABLE                = create("cable", CableBlockEntity.class, CableBlockEntity::new, CableBlockEntity::registerCapabilities, GenBlocks.CABLE);
	public static final BlockEntityDefinition<NetworkConnectorBlockEntity>   NETWORK_CONNECTOR    = create("network_connector", NetworkConnectorBlockEntity.class, NetworkConnectorBlockEntity::new, NetworkConnectorBlockEntity::registerCapabilities, GenBlocks.NETWORK_CONNECTOR);
	public static final BlockEntityDefinition<PowerInjectorBlockEntity>      POWER_INJECTOR       = create("power_injector", PowerInjectorBlockEntity.class, PowerInjectorBlockEntity::new, PowerInjectorBlockEntity::registerCapabilities, GenBlocks.POWER_INJECTOR);
	public static final BlockEntityDefinition<CokeOvenControllerBlockEntity> COKE_OVEN_CONTROLLER = create("coke_oven_controller", CokeOvenControllerBlockEntity.class, CokeOvenControllerBlockEntity::new, CokeOvenControllerBlockEntity::registerCapabilities, GenBlocks.COKE_OVEN_CONTROLLER);
	public static final BlockEntityDefinition<ElectricFurnaceBlockEntity>    ELECTRIC_FURNACE     = create("electric_furnace", ElectricFurnaceBlockEntity.class, ElectricFurnaceBlockEntity::new, ElectricFurnaceBlockEntity::registerCapabilities, GenBlocks.ELECTRIC_FURNACE);
	public static final BlockEntityDefinition<StampingPressBlockEntity>      STAMPING_PRESS       = create("stamping_press", StampingPressBlockEntity.class, StampingPressBlockEntity::new, StampingPressBlockEntity::registerCapabilities, GenBlocks.STAMPING_PRESS);
	public static final BlockEntityDefinition<MaceratorBlockEntity>          MACERATOR            = create("macerator", MaceratorBlockEntity.class, MaceratorBlockEntity::new, MaceratorBlockEntity::registerCapabilities, GenBlocks.MACERATOR);
	public static final BlockEntityDefinition<FluidInfuserBlockEntity>       FLUID_INFUSER        = create("fluid_infuser", FluidInfuserBlockEntity.class, FluidInfuserBlockEntity::new, FluidInfuserBlockEntity::registerCapabilities, GenBlocks.FLUID_INFUSER);
	public static final BlockEntityDefinition<MultiblockHatchBlockEntity>    MULTIBLOCK_HATCH     = create("multiblock_hatch", MultiblockHatchBlockEntity.class, MultiblockHatchBlockEntity::new, MultiblockHatchBlockEntity::registerCapabilities, GenBlocks.ITEM_INPUT_HATCH, GenBlocks.ITEM_OUTPUT_HATCH, GenBlocks.FLUID_INPUT_HATCH, GenBlocks.FLUID_OUTPUT_HATCH, GenBlocks.POWER_HATCH, GenBlocks.NETWORK_HATCH);
	public static final BlockEntityDefinition<HeatingElementBlockEntity>     HEATING_ELEMENT      = create("heating_element", HeatingElementBlockEntity.class, HeatingElementBlockEntity::new, HeatingElementBlockEntity::registerCapabilities, GenBlocks.HEATING_ELEMENT);

	public static List<BlockEntityDefinition<?>> getBlockEntities () {
		return Collections.unmodifiableList(BLOCK_ENTITIES);
	}

	@SafeVarargs
	private static <T extends BlockEntity, B extends Block & BlockEntityTypeOwner<T>> BlockEntityDefinition<T> create (String id, Class<T> entityClass, BlockEntityFactory<T> factory, ICapabilityRegistrar<T> capabilityRegistrar, BlockDefinition<? extends B>... blockDefinitions) {
		Preconditions.checkArgument(blockDefinitions.length > 0);
		var deferred = REGISTRY.register(id, () -> {
			AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
			BlockEntityType.BlockEntitySupplier<T> supplier = (blockPos, blockState) -> factory.create(typeHolder.get(), blockPos, blockState);

			var blocks = new Block[blockDefinitions.length];
			for (int index = 0; index < blockDefinitions.length; index++) {
				blocks[index] = blockDefinitions[index].get();
			}
			var type = new BlockEntityType<>(supplier, blocks);
			typeHolder.setPlain(type);

			for (var definition : blockDefinitions) {
				definition.get().setBlockEntity(entityClass, type);
			}

			return type;
		});

		var result = new BlockEntityDefinition<>(entityClass, deferred, capabilityRegistrar);
		BLOCK_ENTITIES.add(result);
		return result;
	}

	@FunctionalInterface
	interface BlockEntityFactory<T extends BlockEntity> {
		T create (BlockEntityType<T> type, BlockPos pos, BlockState state);
	}

}
