package general.api.item;

import net.minecraft.world.item.ItemStack;

public interface IStackBuilder {

	ItemStack getStack (int count);

	default ItemStack getStack () {
		return getStack(1);
	}

}