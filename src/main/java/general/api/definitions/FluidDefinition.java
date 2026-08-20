package general.api.definitions;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Aggregates all parts of a fluid registration in one place.
 */
public record FluidDefinition(String englishName, Supplier<FluidType> type, Supplier<FlowingFluid> source, Supplier<FlowingFluid> flowing, DeferredBlock<LiquidBlock> block, ItemDefinition<BucketItem> bucket) implements Supplier<FlowingFluid> {

	public FluidDefinition (String englishName, Supplier<FluidType> type, Supplier<FlowingFluid> source, Supplier<FlowingFluid> flowing, DeferredBlock<LiquidBlock> block, ItemDefinition<BucketItem> bucket) {
		this.englishName = Objects.requireNonNull(englishName, "englishName");
		this.type = Objects.requireNonNull(type, "type");
		this.source = Objects.requireNonNull(source, "source");
		this.flowing = Objects.requireNonNull(flowing, "flowing");
		this.block = Objects.requireNonNull(block, "block");
		this.bucket = Objects.requireNonNull(bucket, "bucket");
	}

	public FluidStack getStack () {
		return getStack(FluidType.BUCKET_VOLUME);
	}

	public FluidStack getStack (int amount) {
		if (amount < 0) throw new IllegalArgumentException("Fluid stack amount cannot be negative: " + amount);
		return new FluidStack(get(), amount);
	}

	@Override
	public FlowingFluid get () {
		return source.get();
	}

}
