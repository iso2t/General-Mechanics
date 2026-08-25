package general.api.machine;

import general.api.block.entity.BaseBlockEntity;
import general.api.crafting.MachineRecipeProcessor;
import general.api.machine.config.MachineFace;
import general.api.machine.config.MachineSideConfiguration;
import general.api.machine.config.MachineSideMode;
import general.api.machine.power.MachinePowerProfile;
import general.api.machine.upgrade.MachineUpgradeProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Shared Minecraft lifecycle host for definition-backed machines.
 *
 * <p>Concrete machines normally contain only their immutable definition, menu
 * factory, and truly machine-specific display or interaction helpers.</p>
 */
public abstract class MachineBlockEntity extends BaseBlockEntity implements MachineHost {

	private final MachineRuntime machine;

	protected MachineBlockEntity (BlockEntityType<?> type, BlockPos pos, BlockState state, MachineDefinition definition) {
		super(type, pos, state);
		this.machine = new MachineRuntime(this, Objects.requireNonNull(definition, "definition"));
	}

	@Override
	public final MachineRuntime machine () {
		return machine;
	}

	public final MachineDefinition getMachineDefinition () {
		return machine.getDefinition();
	}

	public final @Nullable MachineSideConfiguration getSideConfiguration () {
		return machine.getSideConfiguration();
	}

	public final MachineSideMode getSideMode (MachineFace face) {
		return machine.getSideMode(face);
	}

	public final MachineSideMode getSideMode (@Nullable Direction side) {
		return machine.getSideMode(side);
	}

	public final boolean setSideMode (MachineFace face, MachineSideMode mode) {
		return machine.setSideMode(face, mode);
	}

	public final Direction getMachineFront () {
		return machine.getMachineFront();
	}

	public final MachineRecipeProcessor getRecipeProcessor () {
		return machine.requireRecipeProcessor();
	}

	public final int getProgress () {
		return machine.getProgress();
	}

	public final int getMaxProgress () {
		return machine.getMaxProgress();
	}

	public final MachineRecipeProcessor.Status getProcessingStatus () {
		return machine.getProcessingStatus();
	}

	public final MachinePowerProfile getPowerProfile () {
		return machine.getPowerProfile();
	}

	public final MachineUpgradeProfile getMultiblockUpgradeProfile () {
		return machine.getMultiblockUpgradeProfile();
	}

	public final MachineUpgradeProfile getOperatingUpgradeProfile () {
		return machine.getOperatingUpgradeProfile();
	}

	public final double getProcessingSpeedMultiplier () {
		return machine.getProcessingSpeedMultiplier();
	}

	public final int getEnergyStored () {
		return machine.getEnergyStored();
	}

	public final int getEnergyCapacity () {
		return machine.getEnergyCapacity();
	}

	public final int getMaxEnergyInput () {
		return machine.getMaxEnergyInput();
	}

	public MachineRecipeProcessor.TickResult serverTick (ServerLevel level) {
		return machine.serverTick(level);
	}

	/**
	 * Applies shared multiblock validation and operating-profile changes before a
	 * custom processor advances. A {@code false} result means processing must halt
	 * for this tick; the runtime has already updated the lit state.
	 */
	protected final boolean prepareMachineProcessing (ServerLevel level) {
		return machine.prepareProcessing(level);
	}

	/**
	 * Updates the standard machine lit state for non-recipe processing logic.
	 */
	protected final void setMachineActive (ServerLevel level, boolean active) {
		machine.setProcessingActive(level, active);
	}

	public final void resetProcessing () {
		machine.resetProcessing();
	}

	@Override
	public void onLoad () {
		super.onLoad();
		machine.onLoad();
		onMachineLoaded();
	}

	@Override
	public void setRemoved () {
		machine.onRemoved();
		onMachineRemoved();
		super.setRemoved();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket () {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public @NonNull CompoundTag getUpdateTag (HolderLookup.@NonNull Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public @NonNull ModelData getModelData () {
		return machine.getModelData();
	}

	@Override
	public void onDataPacket (@NonNull Connection connection, @NonNull ValueInput input) {
		loadWithComponents(input);
		machine.onClientDataLoaded();
	}

	@Override
	public void handleUpdateTag (@NonNull ValueInput input) {
		loadWithComponents(input);
		machine.onClientDataLoaded();
	}

	@Override
	protected final void saveAdditional (@NonNull ValueOutput output) {
		super.saveAdditional(output);
		machine.save(output);
		saveMachineAdditional(output);
	}

	@Override
	protected final void loadAdditional (@NonNull ValueInput input) {
		super.loadAdditional(input);
		machine.load(input);
		loadMachineAdditional(input);
	}

	protected void saveMachineAdditional (ValueOutput output) {
	}

	protected void loadMachineAdditional (ValueInput input) {
	}

	/**
	 * Resets state owned by a custom, non-recipe processor. The runtime invokes
	 * this whenever structure or upgrade changes invalidate active work.
	 */
	protected void resetCustomProcessing () {
	}

	final void resetCustomProcessingFromRuntime () {
		resetCustomProcessing();
	}

	protected void onMachineLoaded () {
	}

	protected void onMachineRemoved () {
	}
}
