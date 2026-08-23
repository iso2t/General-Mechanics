package general.api.transfer.fluid;

import general.api.machine.upgrade.MachineUpgradeProfile;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stable fluid capability view that applies profile-scaled per-operation
 * insertion and extraction limits without limiting a machine's internal recipe
 * access to its tanks.
 */
public final class ProfiledFluidTransferHandler implements ResourceHandler<FluidResource> {

	private final ResourceHandler<FluidResource>  delegate;
	private final int                             baseMaxInsert;
	private final int                             baseMaxExtract;
	private final Supplier<MachineUpgradeProfile> profileSupplier;

	public ProfiledFluidTransferHandler (ResourceHandler<FluidResource> delegate, int baseMaxInsert, int baseMaxExtract, Supplier<MachineUpgradeProfile> profileSupplier) {
		this.delegate = Objects.requireNonNull(delegate, "delegate");
		if (baseMaxInsert < 0) throw new IllegalArgumentException("Base fluid insertion limit cannot be negative: " + baseMaxInsert);
		if (baseMaxExtract < 0) throw new IllegalArgumentException("Base fluid extraction limit cannot be negative: " + baseMaxExtract);
		this.baseMaxInsert = baseMaxInsert;
		this.baseMaxExtract = baseMaxExtract;
		this.profileSupplier = Objects.requireNonNull(profileSupplier, "profileSupplier");
		currentProfile();
	}

	public ProfiledFluidTransferHandler (ResourceHandler<FluidResource> delegate, int baseMaxTransfer, Supplier<MachineUpgradeProfile> profileSupplier) {
		this(delegate, baseMaxTransfer, baseMaxTransfer, profileSupplier);
	}

	public ProfiledFluidTransferHandler (ResourceHandler<FluidResource> delegate, int baseMaxInsert, int baseMaxExtract, MachineUpgradeProfile profile) {
		this(delegate, baseMaxInsert, baseMaxExtract, fixed(profile));
	}

	public ProfiledFluidTransferHandler (ResourceHandler<FluidResource> delegate, int baseMaxTransfer, MachineUpgradeProfile profile) {
		this(delegate, baseMaxTransfer, baseMaxTransfer, fixed(profile));
	}

	public ResourceHandler<FluidResource> getDelegate () {
		return delegate;
	}

	public MachineUpgradeProfile getUpgradeProfile () {
		return currentProfile();
	}

	public int getBaseMaxInsert () {
		return baseMaxInsert;
	}

	public int getBaseMaxExtract () {
		return baseMaxExtract;
	}

	public int getMaxInsert () {
		return currentProfile().scaleFluidTransfer(baseMaxInsert);
	}

	public int getMaxExtract () {
		return currentProfile().scaleFluidTransfer(baseMaxExtract);
	}

	@Override
	public int size () {
		return delegate.size();
	}

	@Override
	public FluidResource getResource (int index) {
		return delegate.getResource(index);
	}

	@Override
	public long getAmountAsLong (int index) {
		return delegate.getAmountAsLong(index);
	}

	@Override
	public long getCapacityAsLong (int index, FluidResource resource) {
		return delegate.getCapacityAsLong(index, resource);
	}

	@Override
	public boolean isValid (int index, FluidResource resource) {
		return delegate.isValid(index, resource);
	}

	@Override
	public int insert (int index, FluidResource resource, int amount, TransactionContext transaction) {
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		Objects.requireNonNull(transaction, "transaction");
		int limited = Math.min(amount, getMaxInsert());
		return limited == 0 ? 0 : delegate.insert(index, resource, limited, transaction);
	}

	@Override
	public int insert (FluidResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		Objects.requireNonNull(transaction, "transaction");
		int limited = Math.min(amount, getMaxInsert());
		return limited == 0 ? 0 : delegate.insert(resource, limited, transaction);
	}

	@Override
	public int extract (int index, FluidResource resource, int amount, TransactionContext transaction) {
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		Objects.requireNonNull(transaction, "transaction");
		int limited = Math.min(amount, getMaxExtract());
		return limited == 0 ? 0 : delegate.extract(index, resource, limited, transaction);
	}

	@Override
	public int extract (FluidResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		Objects.requireNonNull(transaction, "transaction");
		int limited = Math.min(amount, getMaxExtract());
		return limited == 0 ? 0 : delegate.extract(resource, limited, transaction);
	}

	private MachineUpgradeProfile currentProfile () {
		return Objects.requireNonNull(profileSupplier.get(), "Machine upgrade profile supplier returned null");
	}

	private static Supplier<MachineUpgradeProfile> fixed (MachineUpgradeProfile profile) {
		MachineUpgradeProfile checked = Objects.requireNonNull(profile, "profile");
		return () -> checked;
	}
}
