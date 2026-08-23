package general.api.transfer.energy;

import general.api.machine.upgrade.MachineUpgradeProfile;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Transactional energy storage whose capacity and transfer limits are derived
 * from a live {@link MachineUpgradeProfile}.
 *
 * <p>Changing the supplied profile never changes the stored energy. If a new
 * capacity is smaller than the current amount, insertion stops until extraction
 * brings the amount below the configured capacity. NeoForge explicitly permits
 * an energy handler to report an amount above its current capacity.</p>
 */
public class ProfiledEnergyHandler extends SimpleEnergyHandler {

	private static final Runnable NO_CHANGE_CALLBACK = () -> {
	};

	private final int                             baseCapacity;
	private final int                             baseMaxInsert;
	private final int                             baseMaxExtract;
	private final Supplier<MachineUpgradeProfile> profileSupplier;
	private final Runnable                        changeCallback;

	public ProfiledEnergyHandler (int baseCapacity, int baseMaxInsert, int baseMaxExtract, Supplier<MachineUpgradeProfile> profileSupplier, Runnable changeCallback) {
		super(baseCapacity, baseMaxInsert, baseMaxExtract);
		this.baseCapacity = baseCapacity;
		this.baseMaxInsert = baseMaxInsert;
		this.baseMaxExtract = baseMaxExtract;
		this.profileSupplier = Objects.requireNonNull(profileSupplier, "profileSupplier");
		this.changeCallback = Objects.requireNonNull(changeCallback, "changeCallback");
		currentProfile();
	}

	public ProfiledEnergyHandler (int baseCapacity, int baseMaxInsert, int baseMaxExtract, Supplier<MachineUpgradeProfile> profileSupplier) {
		this(baseCapacity, baseMaxInsert, baseMaxExtract, profileSupplier, NO_CHANGE_CALLBACK);
	}

	public ProfiledEnergyHandler (int baseCapacity, int baseMaxInsert, int baseMaxExtract, MachineUpgradeProfile profile, Runnable changeCallback) {
		this(baseCapacity, baseMaxInsert, baseMaxExtract, fixed(profile), changeCallback);
	}

	public ProfiledEnergyHandler (int baseCapacity, int baseMaxInsert, int baseMaxExtract, MachineUpgradeProfile profile) {
		this(baseCapacity, baseMaxInsert, baseMaxExtract, fixed(profile), NO_CHANGE_CALLBACK);
	}

	public MachineUpgradeProfile getUpgradeProfile () {
		return currentProfile();
	}

	public int getBaseCapacity () {
		return baseCapacity;
	}

	public int getBaseMaxInsert () {
		return baseMaxInsert;
	}

	public int getBaseMaxExtract () {
		return baseMaxExtract;
	}

	public int getMaxInsert () {
		return currentProfile().scaleEnergyInput(baseMaxInsert);
	}

	public int getMaxExtract () {
		return currentProfile().scaleEnergyOutput(baseMaxExtract);
	}

	@Override
	public long getCapacityAsLong () {
		return currentProfile().scaleEnergyCapacity(baseCapacity);
	}

	@Override
	public int insert (int amount, TransactionContext transaction) {
		refreshLimits();
		return super.insert(amount, transaction);
	}

	@Override
	public int extract (int amount, TransactionContext transaction) {
		refreshLimits();
		return super.extract(amount, transaction);
	}

	@Override
	protected void onEnergyChanged (int previousAmount) {
		changeCallback.run();
	}

	private void refreshLimits () {
		MachineUpgradeProfile profile = currentProfile();
		capacity = profile.scaleEnergyCapacity(baseCapacity);
		maxInsert = profile.scaleEnergyInput(baseMaxInsert);
		maxExtract = profile.scaleEnergyOutput(baseMaxExtract);
	}

	private MachineUpgradeProfile currentProfile () {
		return Objects.requireNonNull(profileSupplier.get(), "Machine upgrade profile supplier returned null");
	}

	private static Supplier<MachineUpgradeProfile> fixed (MachineUpgradeProfile profile) {
		MachineUpgradeProfile checked = Objects.requireNonNull(profile, "profile");
		return () -> checked;
	}
}
