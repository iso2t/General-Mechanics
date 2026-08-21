package general.api.transfer.energy;

import general.api.transfer.ResourceIoMode;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stable, non-retaining energy capability proxy for a controller attachment.
 */
public final class SupplierBackedEnergyHandler implements EnergyHandler {

	private final Supplier<? extends EnergyHandler>  handlerSupplier;
	private final Supplier<ResourceIoMode>           modeSupplier;

	public SupplierBackedEnergyHandler (Supplier<? extends EnergyHandler> handlerSupplier, Supplier<ResourceIoMode> modeSupplier) {
		this.handlerSupplier = Objects.requireNonNull(handlerSupplier, "handlerSupplier");
		this.modeSupplier = Objects.requireNonNull(modeSupplier, "modeSupplier");
	}

	@Override
	public int insert (int amount, TransactionContext transaction) {
		Objects.requireNonNull(transaction, "transaction");
		if (amount < 0) throw new IllegalArgumentException("Energy amount cannot be negative: " + amount);
		EnergyHandler handler = handlerSupplier.get();
		ResourceIoMode mode = modeSupplier.get();
		return handler == null || mode == null || !mode.allowsInsertion() ? 0 : handler.insert(amount, transaction);
	}

	@Override
	public int extract (int amount, TransactionContext transaction) {
		Objects.requireNonNull(transaction, "transaction");
		if (amount < 0) throw new IllegalArgumentException("Energy amount cannot be negative: " + amount);
		EnergyHandler handler = handlerSupplier.get();
		ResourceIoMode mode = modeSupplier.get();
		return handler == null || mode == null || !mode.allowsExtraction() ? 0 : handler.extract(amount, transaction);
	}

	@Override
	public long getAmountAsLong () {
		EnergyHandler handler = handlerSupplier.get();
		return handler == null ? 0 : handler.getAmountAsLong();
	}

	@Override
	public long getCapacityAsLong () {
		EnergyHandler handler = handlerSupplier.get();
		return handler == null ? 0 : handler.getCapacityAsLong();
	}
}
