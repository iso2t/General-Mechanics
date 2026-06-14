package general.api.definitions;

import general.api.item.IStackBuilder;
import general.api.mod.GenAPI;
import general.api.registry.IRegistryNameProvider;
import general.api.registry.RegistryString;
import general.api.resources.Resource;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ItemDefinition<T extends Item>(RegistryString localizedName, DeferredItem<T> item) implements ItemLike, Supplier<T>, IStackBuilder, IRegistryNameProvider {

	public ItemDefinition (String localizedName, DeferredItem<T> item) {
		this(new RegistryString(localizedName), item);
	}

	public Identifier getId () {
		return item.getId();
	}

	@Override
	public ItemStack getStack (int count) {
		return item.toStack(count);
	}

	public Holder<Item> getHolder () {
		return item;
	}

	@Override
	public T get () {
		return item.get();
	}

	@Override
	public @NotNull T asItem () {
		return get();
	}

	@Override
	public RegistryString getRegistryString () {
		return localizedName;
	}

	public static <T extends Item> ItemDefinition<T> of (Item item) {
		Identifier id = Resource.getFromItem(item);
		return new ItemDefinition<>(id.getPath(), DeferredItem.createItem(id));
	}

	public static <T extends Item> ItemDefinition<T> of (ItemStack stack) {
		return of(stack.getItem());
	}

}
