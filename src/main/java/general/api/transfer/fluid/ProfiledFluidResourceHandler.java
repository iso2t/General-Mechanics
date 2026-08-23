package general.api.transfer.fluid;

import general.api.machine.upgrade.MachineUpgradeProfile;
import general.api.transfer.ResourceChangeListener;
import general.api.transfer.ResourceSlotDefinition;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Definition-backed fluid storage whose tank capacities are derived from a live
 * {@link MachineUpgradeProfile}.
 *
 * <p>A capacity downgrade never truncates a tank. Over-capacity contents can be
 * extracted normally, while insertion remains blocked until the amount falls
 * below the current capacity. Persisted over-capacity contents are accepted so a
 * save/reload cannot turn a safe downgrade into fluid loss or a loading error.</p>
 */
public final class ProfiledFluidResourceHandler extends FluidResourceHandler {

	private final Supplier<MachineUpgradeProfile> profileSupplier;

	public ProfiledFluidResourceHandler (FluidInventoryDefinition definition, Supplier<MachineUpgradeProfile> profileSupplier, ResourceChangeListener<FluidResource> changeListener) {
		super(definition, changeListener);
		this.profileSupplier = Objects.requireNonNull(profileSupplier, "profileSupplier");
		currentProfile();
	}

	public ProfiledFluidResourceHandler (FluidInventoryDefinition definition, Supplier<MachineUpgradeProfile> profileSupplier, Runnable changeCallback) {
		this(definition, profileSupplier, ResourceChangeListener.from(changeCallback));
	}

	public ProfiledFluidResourceHandler (FluidInventoryDefinition definition, MachineUpgradeProfile profile, ResourceChangeListener<FluidResource> changeListener) {
		this(definition, fixed(profile), changeListener);
	}

	public ProfiledFluidResourceHandler (FluidInventoryDefinition definition, MachineUpgradeProfile profile, Runnable changeCallback) {
		this(definition, fixed(profile), changeCallback);
	}

	public MachineUpgradeProfile getUpgradeProfile () {
		return currentProfile();
	}

	@Override
	protected int getEffectiveCapacity (ResourceSlotDefinition<FluidResource> slot, FluidResource resource) {
		return currentProfile().scaleFluidCapacity(super.getEffectiveCapacity(slot, resource));
	}

	@Override
	protected boolean allowsSerializedAmountAboveCapacity (int index, FluidResource resource, int amount, int capacity) {
		return true;
	}

	private MachineUpgradeProfile currentProfile () {
		return Objects.requireNonNull(profileSupplier.get(), "Machine upgrade profile supplier returned null");
	}

	private static Supplier<MachineUpgradeProfile> fixed (MachineUpgradeProfile profile) {
		MachineUpgradeProfile checked = Objects.requireNonNull(profile, "profile");
		return () -> checked;
	}
}
