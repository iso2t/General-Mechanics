package general.api.resources;

import general.api.definitions.BlockDefinition;
import general.api.definitions.ItemDefinition;
import general.api.mod.GenAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class Resource {

	private Resource() {}

	public static Identifier get (String path) {
		return Identifier.fromNamespaceAndPath(GenAPI.getModId(), path);
	}

	public static Identifier get (Class<?> owner, String path) {
		return Identifier.fromNamespaceAndPath(GenAPI.getModId(owner), path);
	}

	public static Identifier getMinecraftResource (String path) {
		return Identifier.withDefaultNamespace(path);
	}

	public static Identifier getCustomResource (String namespace, String path) {
		return Identifier.fromNamespaceAndPath(namespace, path);
	}

	public static Identifier getFromItem (Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}

	public static Identifier getFromItem (ItemStack stack) {
		return Resource.getFromItem(stack.getItem());
	}

	public static Identifier getFromItem (ItemDefinition<?> item) {
		return Resource.getFromItem(item.get());
	}

	public static Identifier getFromBlock (Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	public static Identifier getFromBlock (BlockDefinition<?> block) {
		return Resource.getFromBlock(block.get());
	}

}
