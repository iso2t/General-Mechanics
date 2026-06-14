package general.api.definitions;

import general.api.item.IStackBuilder;
import general.api.registry.IRegistryNameProvider;
import general.api.registry.RegistryString;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

public record BlockDefinition<T extends Block>(RegistryString localizedName, DeferredBlock<T> block, ItemDefinition<BlockItem> item) implements ItemLike, IStackBuilder, IRegistryNameProvider {

	public BlockDefinition (String localizedName, DeferredBlock<T> block, ItemDefinition<BlockItem> item) {
		this(new RegistryString(localizedName), block, item);
	}

	public Identifier getId () {
		return block.getId();
	}

	@Override
	public ItemStack getStack (int count) {
		return item.getStack(count);
	}

	public Holder<Block> getHolder () {
		return block;
	}

	public T get () {
		return block.get();
	}

	public @NotNull Item asItem () {
		return item.get();
	}

	@Override
	public RegistryString getRegistryString () {
		return localizedName;
	}

}
