package general.api.network.service;

import general.api.network.NetworkServices;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

/**
 * Network-level item transfer service.
 *
 * <p>Implementations preserve complete {@link ItemStack} component identity.
 * Simulated operations report the same result without committing storage
 * changes. Availability contains only items that the exposed capability can
 * actually extract, not merely contents visible through queries.</p>
 */
public interface ItemNetworkService extends NetworkService {

	/**
	 * @return the uninserted remainder
	 */
	ItemStack insert (ItemStack stack, boolean simulate);

	/**
	 * Extracts at most one stack of one component-identical item resource.
	 */
	ItemStack extract (Predicate<ItemStack> filter, int amount, boolean simulate);

	/**
	 * Snapshot of currently extractable item stacks.
	 */
	List<ItemStack> getAvailableItems ();

	@Override
	default NetworkServiceType<ItemNetworkService> getType () {
		return NetworkServices.ITEM;
	}
}
