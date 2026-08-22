package general.mechanics.common.block.entity;

import general.api.block.entity.BaseBlockEntity;
import general.api.definitions.MultiblockDefinition;
import general.api.multiblock.MultiblockController;
import general.api.transfer.SidedResourceHandlers;
import general.api.transfer.fluid.FluidInventoryDefinition;
import general.api.transfer.fluid.SidedFluidResourceProvider;
import general.api.transfer.item.ItemInventoryDefinition;
import general.api.transfer.item.SidedItemResourceProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class ElectricFurnaceBlockEntity extends BaseBlockEntity implements MultiblockController, SidedItemResourceProvider, SidedFluidResourceProvider, MenuProvider {
	public ElectricFurnaceBlockEntity (BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public MultiblockDefinition getMultiblockDefinition () {
		return null;
	}

	@Override
	public BlockPos getMultiblockPosition () {
		return null;
	}

	@Override
	public Direction getMultiblockFacing () {
		return null;
	}

	@Override
	public boolean isMultiblockFormed () {
		return false;
	}

	@Override
	public void setMultiblockFormed (boolean formed) {

	}

	@Override
	public SidedResourceHandlers<FluidResource> getSidedFluidHandlers () {
		return null;
	}

	@Override
	public ResourceHandler<FluidResource> getFluidHandler () {
		return null;
	}

	@Override
	public FluidInventoryDefinition getFluidDefinition () {
		return null;
	}

	@Override
	public SidedResourceHandlers<ItemResource> getSidedItemHandlers () {
		return null;
	}

	@Override
	public ResourceHandler<ItemResource> getItemHandler () {
		return null;
	}

	@Override
	public ItemInventoryDefinition getItemDefinition () {
		return null;
	}

	@Override
	public Component getDisplayName () {
		return null;
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu (int i, Inventory inventory, Player player) {
		return null;
	}
}
