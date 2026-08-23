package general.api.transfer.fluid;

import general.api.transfer.DefinitionBackedResourceHandler;
import general.api.transfer.ResourceChangeListener;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Definition-backed transactional fluid storage.
 */
public class FluidResourceHandler extends DefinitionBackedResourceHandler<FluidStack, FluidResource> {

	public FluidResourceHandler (FluidInventoryDefinition definition, ResourceChangeListener<FluidResource> changeListener) {
		super(definition.genericDefinition(), FluidStack.EMPTY, FluidStack.OPTIONAL_CODEC, changeListener);
	}

	public FluidResourceHandler (FluidInventoryDefinition definition, Runnable changeCallback) {
		this(definition, ResourceChangeListener.from(changeCallback));
	}

	@Override
	protected FluidResource getResourceFrom (FluidStack stack) {
		return FluidResource.of(stack);
	}

	@Override
	protected int getAmountFrom (FluidStack stack) {
		return stack.getAmount();
	}

	@Override
	protected FluidStack getStackFrom (FluidResource resource, int amount) {
		return resource.toStack(amount);
	}

	@Override
	protected FluidStack copyOf (FluidStack stack) {
		return stack.copy();
	}

	@Override
	protected boolean matches (FluidStack stack, FluidResource resource) {
		return resource.matches(stack);
	}
}
