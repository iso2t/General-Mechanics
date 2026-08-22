package general.mechanics.common.block.entity;

import general.api.block.IOBlock;
import general.api.block.entity.BaseBlockEntity;
import general.api.capabilities.GeneralCapabilities;
import general.api.multiblock.*;
import general.api.network.INetworkInterface;
import general.api.network.NetworkEndpoint;
import general.api.network.NetworkNode;
import general.api.network.NetworkServices;
import general.api.transfer.ResourceAccessPolicy;
import general.api.transfer.ResourceIoMode;
import general.api.transfer.SupplierBackedRestrictedResourceHandler;
import general.api.transfer.energy.EnergyResourceProvider;
import general.api.transfer.energy.SupplierBackedEnergyHandler;
import general.api.transfer.fluid.FluidResourceProvider;
import general.api.transfer.item.ItemResourceProvider;
import general.mechanics.common.network.NetworkConnectorServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Shared, storage-free block entity for all controller-backed hatch blocks.
 */
public class MultiblockHatchBlockEntity extends BaseBlockEntity implements MultiblockHatch, INetworkInterface {

	public static void registerCapabilities (RegisterCapabilitiesEvent event, BlockEntityType<MultiblockHatchBlockEntity> type) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, type, MultiblockHatchBlockEntity::getItemCapability);
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, MultiblockHatchBlockEntity::getFluidCapability);
		event.registerBlockEntity(Capabilities.Energy.BLOCK, type, MultiblockHatchBlockEntity::getEnergyCapability);
		event.registerBlockEntity(GeneralCapabilities.NETWORK_HANDLER_BLOCK, type, MultiblockHatchBlockEntity::getNetworkCapability);
	}

	private final MultiblockAttachmentBinding    multiblockBinding = createAttachmentBinding();
	private final ResourceHandler<ItemResource>  itemCapability    = new SupplierBackedRestrictedResourceHandler<>(this::resolveItemHandler, this::resolveItemPolicy);
	private final ResourceHandler<FluidResource> fluidCapability   = new SupplierBackedRestrictedResourceHandler<>(this::resolveFluidHandler, this::resolveFluidPolicy);
	private final EnergyHandler                  energyCapability  = new SupplierBackedEnergyHandler(this::resolveEnergyHandler, this::resolveEnergyMode);
	private final NetworkNode                    networkNode       = new NetworkNode("MultiblockHatch");

	private @Nullable PolicyCache<ItemResource>  itemPolicy;
	private @Nullable PolicyCache<FluidResource> fluidPolicy;

	public MultiblockHatchBlockEntity (BlockEntityType<MultiblockHatchBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public MultiblockAttachmentBinding getMultiblockBinding () {
		return multiblockBinding;
	}

	@Override
	public IOBlock.IOType getHatchType () {
		return ioBlock().getType();
	}

	@Override
	public IOBlock.IOMode getHatchMode () {
		return ioBlock().getMode();
	}

	@Override
	public void onMultiblockBindingChanged (@Nullable BlockPos previous, @Nullable BlockPos current) {
		itemPolicy = null;
		fluidPolicy = null;
		networkNode.getServices().clear();
	}

	public @Nullable ResourceHandler<ItemResource> getItemCapability (@Nullable Direction side) {
		MultiblockHatchContext context = hatchContext();
		return getHatchType() == IOBlock.IOType.ITEM && context != null && context.definition().access().hasItemAccess() && resolveItemHandler(context) != null ? itemCapability : null;
	}

	public @Nullable ResourceHandler<FluidResource> getFluidCapability (@Nullable Direction side) {
		MultiblockHatchContext context = hatchContext();
		return getHatchType() == IOBlock.IOType.FLUID && context != null && context.definition().access().hasFluidAccess() && resolveFluidHandler(context) != null ? fluidCapability : null;
	}

	public @Nullable EnergyHandler getEnergyCapability (@Nullable Direction side) {
		MultiblockHatchContext context = hatchContext();
		return getHatchType() == IOBlock.IOType.POWER && context != null && context.definition().access().energyMode() != ResourceIoMode.NONE && resolveEnergyHandler(context) != null ? energyCapability : null;
	}

	public @Nullable INetworkInterface getNetworkCapability (@Nullable Direction side) {
		return getHatchType() == IOBlock.IOType.NETWORK && isNetworkEnabled() ? this : null;
	}

	@Override
	public NetworkNode getNetworkNode () {
		refreshNetworkServices();
		return networkNode;
	}

	@Override
	public boolean isNetworkEnabled () {
		refreshNetworkServices();
		return !networkNode.getServices().getAll().isEmpty();
	}

	@Override
	public List<NetworkEndpoint> getNetworkEndpoints () {
		MultiblockHatchContext context = hatchContext();
		return context == null ? List.of() : List.of(new NetworkEndpoint(context.instance().anchor()));
	}

	@Override
	public void onLoad () {
		super.onLoad();
		if (level instanceof ServerLevel serverLevel) {
			MultiblockHandler.onAttachmentLoaded(serverLevel, this);
		}
	}

	@Override
	public void setRemoved () {
		networkNode.getServices().clear();
		super.setRemoved();
	}

	@Override
	protected void saveAdditional (@NonNull ValueOutput output) {
		super.saveAdditional(output);
		saveMultiblockAttachment(output);
	}

	@Override
	protected void loadAdditional (@NonNull ValueInput input) {
		super.loadAdditional(input);
		loadMultiblockAttachment(input);
	}

	private IOBlock<?> ioBlock () {
		if (getBlockState().getBlock() instanceof IOBlock<?> block) return block;
		throw new IllegalStateException("Multiblock hatch block entity is attached to a non-I/O block");
	}

	private @Nullable MultiblockHatchContext hatchContext () {
		return level instanceof ServerLevel serverLevel ? MultiblockHandler.getHatchContext(serverLevel, this).orElse(null) : null;
	}

	private @Nullable BlockEntity resolveController (MultiblockHatchContext context) {
		return level == null ? null : level.getBlockEntity(context.instance().anchor());
	}

	private @Nullable ResourceHandler<ItemResource> resolveItemHandler () {
		MultiblockHatchContext context = hatchContext();
		return context == null ? null : resolveItemHandler(context);
	}

	private @Nullable ResourceHandler<ItemResource> resolveItemHandler (MultiblockHatchContext context) {
		return resolveController(context) instanceof ItemResourceProvider provider ? provider.getItemHandler() : null;
	}

	private @Nullable ResourceAccessPolicy<ItemResource> resolveItemPolicy () {
		MultiblockHatchContext context = hatchContext();
		if (context == null || !(resolveController(context) instanceof ItemResourceProvider provider)) return null;

		if (itemPolicy != null && itemPolicy.matches(context)) return itemPolicy.policy();
		var builder = provider.getItemDefinition().access();
		context.definition().access().itemInsertion().forEach(builder::insert);
		context.definition().access().itemExtraction().forEach(builder::extract);
		ResourceAccessPolicy<ItemResource> policy = builder.build();
		itemPolicy = new PolicyCache<>(context.instance().anchor(), context.definition().key(), policy);
		return policy;
	}

	private @Nullable ResourceHandler<FluidResource> resolveFluidHandler () {
		MultiblockHatchContext context = hatchContext();
		return context == null ? null : resolveFluidHandler(context);
	}

	private @Nullable ResourceHandler<FluidResource> resolveFluidHandler (MultiblockHatchContext context) {
		return resolveController(context) instanceof FluidResourceProvider provider ? provider.getFluidHandler() : null;
	}

	private @Nullable ResourceAccessPolicy<FluidResource> resolveFluidPolicy () {
		MultiblockHatchContext context = hatchContext();
		if (context == null || !(resolveController(context) instanceof FluidResourceProvider provider)) return null;

		if (fluidPolicy != null && fluidPolicy.matches(context)) return fluidPolicy.policy();
		var builder = provider.getFluidDefinition().access();
		context.definition().access().fluidInsertion().forEach(builder::insert);
		context.definition().access().fluidExtraction().forEach(builder::extract);
		ResourceAccessPolicy<FluidResource> policy = builder.build();
		fluidPolicy = new PolicyCache<>(context.instance().anchor(), context.definition().key(), policy);
		return policy;
	}

	private @Nullable EnergyHandler resolveEnergyHandler () {
		MultiblockHatchContext context = hatchContext();
		return context == null ? null : resolveEnergyHandler(context);
	}

	private @Nullable EnergyHandler resolveEnergyHandler (MultiblockHatchContext context) {
		return resolveController(context) instanceof EnergyResourceProvider provider ? provider.getEnergyHandler() : null;
	}

	private @Nullable ResourceIoMode resolveEnergyMode () {
		MultiblockHatchContext context = hatchContext();
		return context == null ? null : context.definition().access().energyMode();
	}

	private void refreshNetworkServices () {
		networkNode.getServices().clear();
		if (getHatchType() != IOBlock.IOType.NETWORK) return;

		MultiblockHatchContext context = hatchContext();
		if (context == null) return;
		var services = context.definition().access().networkServices();

		if (services.contains(NetworkServices.ITEM) && context.definition().access().hasItemAccess() && resolveItemHandler(context) != null) {
			networkNode.getServices().register(NetworkServices.ITEM, NetworkConnectorServices.itemService(() -> itemCapability));
		}
		if (services.contains(NetworkServices.FLUID) && context.definition().access().hasFluidAccess() && resolveFluidHandler(context) != null) {
			networkNode.getServices().register(NetworkServices.FLUID, NetworkConnectorServices.fluidService(() -> fluidCapability));
		}
		if (services.contains(NetworkServices.ENERGY) && context.definition().access().energyMode() != ResourceIoMode.NONE && resolveEnergyHandler(context) != null) {
			networkNode.getServices().register(NetworkServices.ENERGY, NetworkConnectorServices.energyService(() -> energyCapability));
		}
	}

	private record PolicyCache<R extends Resource>(BlockPos controller, HatchKey route, ResourceAccessPolicy<R> policy) {

		private boolean matches (MultiblockHatchContext context) {
			return controller.equals(context.instance().anchor()) && route.equals(context.definition().key());
		}
	}
}
