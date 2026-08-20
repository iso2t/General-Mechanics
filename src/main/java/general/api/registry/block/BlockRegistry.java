package general.api.registry.block;

import general.api.definitions.BlockDefinition;
import general.api.definitions.ItemDefinition;
import general.api.registry.item.ItemRegistry;
import general.api.tab.ICreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BlockRegistry implements ICreativeModeTab {

	public abstract DeferredRegister.Blocks getRegistry ();

	public abstract List<BlockDefinition<?>> getBlocks ();

	public static <T extends Block> BlockDefinition<T> registerBlock (BlockRegistry registry, ItemRegistry itemRegistry, final String localizedName, Identifier identifier, final Function<BlockBehaviour.Properties, T> factory) {
		return registerBlock(registry, itemRegistry, localizedName, identifier, factory, null, null);
	}

	public static <T extends Block> BlockDefinition<T> registerBlock (BlockRegistry registry, ItemRegistry itemRegistry, final String localizedName, Identifier identifier, final Function<BlockBehaviour.Properties, T> factory, @Nullable Supplier<BlockBehaviour.Properties> baseProperties) {
		return registerBlock(registry, itemRegistry, localizedName, identifier, factory, baseProperties, null);
	}

	public static <T extends Block> BlockDefinition<T> registerBlock (BlockRegistry registry, ItemRegistry itemRegistry, final String localizedName, Identifier identifier, final Function<BlockBehaviour.Properties, T> factory, @Nullable Supplier<BlockBehaviour.Properties> baseProperties, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
		var block = baseProperties != null ? registry.getRegistry().registerBlock(identifier.getPath(), factory, baseProperties) : registry.getRegistry().registerBlock(identifier.getPath(), factory);
		var item = itemRegistry.getRegistry().register(identifier.getPath(), () -> {
			var m_block = block.get();
			var itemProperties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, identifier)).useBlockDescriptionPrefix();
			if (itemFactory != null) {
				var m_item = itemFactory.apply(m_block, itemProperties);
				if (m_item == null) throw new IllegalArgumentException(String.format("BlockItem factory for %s returned null.", identifier));
				return m_item;
			} else {
				return new BlockItem(m_block, itemProperties);
			}
		});
		var itemDefinition = new ItemDefinition<>(localizedName, item);
		var blockDefinition = new BlockDefinition<>(localizedName, block, itemDefinition);
		registry.getBlocks().add(blockDefinition);
		return blockDefinition;
	}

	// blocks on this list will not be added to the creative tabs
	protected List<BlockDefinition<?>> dnaTab () {
		return List.of();
	}

}
