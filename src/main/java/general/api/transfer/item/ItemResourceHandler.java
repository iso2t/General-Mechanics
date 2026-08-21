package general.api.transfer.item;

import general.api.transfer.DefinitionBackedResourceHandler;
import general.api.transfer.ResourceChangeListener;
import general.api.transfer.ResourceSlotDefinition;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

/**
 * Definition-backed transactional item storage.
 */
public class ItemResourceHandler extends DefinitionBackedResourceHandler<ItemStack, ItemResource> {

	public ItemResourceHandler (ItemInventoryDefinition definition, ResourceChangeListener<ItemResource> changeListener) {
		super(definition.genericDefinition(), ItemStack.EMPTY, ItemStack.OPTIONAL_CODEC, changeListener);
	}

	public ItemResourceHandler (ItemInventoryDefinition definition, Runnable changeCallback) {
		this(definition, ResourceChangeListener.from(changeCallback));
	}

	@Override
	protected ItemResource getResourceFrom (ItemStack stack) {
		return ItemResource.of(stack);
	}

	@Override
	protected int getAmountFrom (ItemStack stack) {
		return stack.getCount();
	}

	@Override
	protected ItemStack getStackFrom (ItemResource resource, int amount) {
		return resource.toStack(amount);
	}

	@Override
	protected ItemStack copyOf (ItemStack stack) {
		return stack.copy();
	}

	@Override
	protected boolean matches (ItemStack stack, ItemResource resource) {
		return resource.matches(stack);
	}

	@Override
	protected int getEffectiveCapacity (ResourceSlotDefinition<ItemResource> slot, ItemResource resource) {
		int resourceLimit = resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : resource.getMaxStackSize();
		return Math.min(slot.capacity(), resourceLimit);
	}
}
