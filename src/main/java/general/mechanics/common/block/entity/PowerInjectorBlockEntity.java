package general.mechanics.common.block.entity;

import general.api.block.entity.BaseBlockEntity;
import general.api.capabilities.GeneralCapabilities;
import general.api.network.INetworkInterface;
import general.api.network.NetworkNode;
import general.api.network.NetworkServices;
import general.api.network.service.EnergyNetworkService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.LimitingEnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PowerInjectorBlockEntity extends BaseBlockEntity implements INetworkInterface {

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<PowerInjectorBlockEntity> type) {
		event.registerBlockEntity(GeneralCapabilities.NETWORK_HANDLER_BLOCK, type, (injector, side) -> injector.isNetworkSide(side) ? injector : null);
		event.registerBlockEntity(Capabilities.Energy.BLOCK, type, PowerInjectorBlockEntity::getEnergyInput);
	}

	public static final int ENERGY_CAPACITY = 100_000;
	public static final int MAX_TRANSFER    = 10_000;

	private final NetworkNode         networkNode;
	private final SimpleEnergyHandler energy;
	private final EnergyHandler       energyInput;

	public PowerInjectorBlockEntity (BlockEntityType<PowerInjectorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.energy = new SimpleEnergyHandler(ENERGY_CAPACITY, MAX_TRANSFER, MAX_TRANSFER) {
			@Override
			protected void onEnergyChanged (int amount) {
				setChanged();
			}
		};
		this.energyInput = new LimitingEnergyHandler(energy, MAX_TRANSFER, 0);
		this.networkNode = new NetworkNode("PowerInjector");
		this.networkNode.getServices().register(NetworkServices.ENERGY, new InjectorEnergyService());
	}

	@Override
	public NetworkNode getNetworkNode () {
		return networkNode;
	}

	/**
	 * The front is reserved for the cable/network connection.
	 */
	public boolean isNetworkSide (@Nullable Direction side) {
		return side != null && side == getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	/**
	 * Energy is accepted on every physical side except the network front.
	 */
	public @Nullable EnergyHandler getEnergyInput (@Nullable Direction side) {
		return isNetworkSide(side) ? null : energyInput;
	}

	@Override
	protected void saveAdditional (@NonNull ValueOutput output) {
		super.saveAdditional(output);
		energy.serialize(output.child("energy"));
	}

	@Override
	protected void loadAdditional (@NonNull ValueInput input) {
		super.loadAdditional(input);
		energy.deserialize(input.childOrEmpty("energy"));
	}

	private final class InjectorEnergyService implements EnergyNetworkService {
		@Override
		public long insert (long amount, boolean simulate) {
			return transfer(amount, simulate, true);
		}

		@Override
		public long extract (long amount, boolean simulate) {
			return transfer(amount, simulate, false);
		}

		@Override
		public long getStored () {
			return energy.getAmountAsLong();
		}

		@Override
		public long getCapacity () {
			return energy.getCapacityAsLong();
		}

		private long transfer (long amount, boolean simulate, boolean insert) {
			if (amount <= 0) return 0;
			try (var transaction = Transaction.openRoot()) {
				int transferred = insert ? energy.insert((int) Math.min(amount, Integer.MAX_VALUE), transaction) : energy.extract((int) Math.min(amount, Integer.MAX_VALUE), transaction);
				if (!simulate) transaction.commit();
				return transferred;
			}
		}
	}
}
