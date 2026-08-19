package general.api.capabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Type-safe, instance-free capability registration callback for a block entity type.
 */
@FunctionalInterface
public interface ICapabilityRegistrar<T extends BlockEntity> {

	void registerBlockEntity (RegisterCapabilitiesEvent event, BlockEntityType<T> type);

}
