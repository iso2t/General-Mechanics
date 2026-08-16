package general.api.network.service;

import general.api.network.NetworkServices;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public interface ItemNetworkService extends NetworkService {

	ItemStack insert (ItemStack stack, boolean simulate);

	ItemStack extract (Predicate<ItemStack> filter, int amount, boolean simulate);

	List<ItemStack> getAvailableItems ();

	@Override
	default NetworkServiceType<ItemNetworkService> getType () {
		return NetworkServices.ITEM;
	}
}
