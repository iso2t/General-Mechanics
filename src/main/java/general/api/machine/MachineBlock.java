package general.api.machine;

import general.api.block.BaseEntityBlock;
import general.api.block.IWrenchable;
import general.api.block.util.ILitProvider;
import general.api.block.util.IPickaxe;
import general.api.machine.config.MachineSideConfigurationDefinition;
import general.api.model.IConfigurableMachineModel;
import general.api.multiblock.MultiblockController;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Standard block host for a processing {@link MachineBlockEntity}.
 *
 * <p>Crafting machines are always lit-capable, horizontally rotatable, wrenchable,
 * and pickaxe-mineable. Their concrete blocks retain texture, recipe, particle,
 * shape, and custom-interaction policy.</p>
 */
public abstract class MachineBlock<T extends MachineBlockEntity> extends BaseEntityBlock<T> implements IWrenchable, IRotatableBlock, ILitProvider, IPickaxe {

	private final MachineDefinition machineDefinition;

	public MachineBlock (Properties properties, Class<T> blockEntityClass, MachineDefinition machineDefinition) {
		super(properties, blockEntityClass);
		this.machineDefinition = Objects.requireNonNull(machineDefinition, "machineDefinition");
		if (!machineDefinition.hasLitState()) throw new IllegalArgumentException("Processing machine definition must support lit state");
		registerDefaultState(getStateDefinition().any().setValue(LIT, false));
	}

	public final MachineDefinition getMachineDefinition () {
		return machineDefinition;
	}

	/**
	 * Supports {@link IConfigurableMachineModel} without another block override.
	 */
	public final MachineSideConfigurationDefinition getSideConfigurationDefinition () {
		MachineSideConfigurationDefinition configuration = machineDefinition.sideConfiguration();
		if (configuration == null) throw new IllegalStateException("Machine does not define configurable sides");
		return configuration;
	}

	@Override
	public BlockRotationStrategy getRotationStrategy () {
		return BlockRotationStrategies.HORIZONTAL_FACING;
	}

	@Override
	public final <E extends BlockEntity> @Nullable BlockEntityTicker<E> getTicker (Level level, @NonNull BlockState state, @NonNull BlockEntityType<E> type) {
		BlockEntityType<T> machineType = getBlockEntityType();
		if (level.isClientSide() || type != machineType) return null;
		return (tickLevel, pos, tickState, blockEntity) -> {
			if (tickLevel instanceof ServerLevel serverLevel && getBlockEntityClass().isInstance(blockEntity)) {
				getBlockEntityClass().cast(blockEntity).serverTick(serverLevel);
			}
		};
	}

	@Override
	protected final @NonNull InteractionResult useWithoutItem (@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
		T machine = getBlockEntity(level, pos);
		return machine == null ? InteractionResult.PASS : useMachine(state, level, pos, player, hitResult, machine);
	}

	/**
	 * Formed-only multiblock machines validate their structure by default; all
	 * other machines open their menu. Concrete blocks can override for custom use.
	 */
	protected InteractionResult useMachine (BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, T machine) {
		MachineDefinition.MultiblockSpec multiblock = machineDefinition.multiblock();
		if (multiblock != null && !multiblock.allowStandaloneOperation() && machine instanceof MultiblockController controller) {
			return controller.useMultiblock(player, hitResult);
		}
		if (!(machine instanceof MenuProvider menuProvider)) return InteractionResult.PASS;
		if (player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(menuProvider, pos);
		return InteractionResult.SUCCESS;
	}
}
