package general.mechanics.registries;

import com.google.common.base.Preconditions;
import general.api.block.BlockEntityTypeOwner;
import general.mechanics.common.block.entity.CableBlockEntity;
import general.api.definitions.BlockDefinition;
import general.api.definitions.BlockEntityDefinition;
import general.api.mod.GenAPI;
import general.mechanics.common.block.entity.NetworkConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GenBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GenAPI.getModId());

	private static final List<BlockEntityDefinition<?>> BLOCK_ENTITIES = new ArrayList<>();

	public static final BlockEntityDefinition<CableBlockEntity> CABLE = create("cable", CableBlockEntity.class, CableBlockEntity::new, GenBlocks.CABLE);
	public static final BlockEntityDefinition<NetworkConnectorBlockEntity> NETWORK_CONNECTOR = create("network_connector", NetworkConnectorBlockEntity.class, NetworkConnectorBlockEntity::new, GenBlocks.NETWORK_CONNECTOR);

	@SafeVarargs
	private static <T extends BlockEntity, B extends Block & BlockEntityTypeOwner<T>> BlockEntityDefinition<T> create (String id, Class<T> entityClass, BlockEntityFactory<T> factory, BlockDefinition<? extends B>... blockDefinitions) {
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

		var result = new BlockEntityDefinition<>(entityClass, deferred);
		BLOCK_ENTITIES.add(result);
		return result;
	}

	@FunctionalInterface
	interface BlockEntityFactory<T extends BlockEntity> {
		T create (BlockEntityType<T> type, BlockPos pos, BlockState state);
	}

}
