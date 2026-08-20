package general.mechanics.registries;

import general.api.definitions.FluidDefinition;
import general.api.definitions.ItemDefinition;
import general.api.fluid.BaseFluid;
import general.api.mod.GenAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class GenFluids {

	public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(BuiltInRegistries.FLUID, GenAPI.getModId());

	private static final List<FluidDefinition> FLUIDS = new ArrayList<>();

	public static final FluidDefinition CREOSOTE = registerFluid("Creosote", 0xFF281E15, new Vector3f(0.2F, 0.2F, 0.2F), 1000, 100, true);

	public static List<FluidDefinition> getFluids () {
		return Collections.unmodifiableList(FLUIDS);
	}

	/**
	 * Registers every registry owned by the fluid definition system. Calling this method also
	 * initializes the definitions before the block and item deferred registers are attached.
	 */
	public static void register (IEventBus bus) {
		GenFluidTypes.REGISTRY.register(bus);
		REGISTRY.register(bus);
	}

	public static FluidDefinition registerFluid (String englishName, int tintColor, Vector3f fogColor, int density, int viscosity) {
		return registerFluid(englishName, tintColor, fogColor, density, viscosity, false);
	}

	public static FluidDefinition registerFluid (String englishName, int tintColor, Vector3f fogColor, int density, int viscosity, boolean opaque) {
		return registerFluid(englishName, tintColor, fogColor, density, viscosity, opaque, 0.0F, 1.5F);
	}

	public static FluidDefinition registerFluid (String englishName, int tintColor, Vector3f fogColor, int density, int viscosity, boolean opaque, float fogStart, float fogEnd) {
		String baseName = englishName.toLowerCase(Locale.ROOT).replace(' ', '_');

		// FluidType
		FluidType.Properties fluidProperties = FluidType.Properties.create().density(density).viscosity(viscosity).motionScale(0.014D).canPushEntity(true).canSwim(true).canDrown(true).canConvertToSource(false).fallDistanceModifier(0.0F).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
		Supplier<FluidType> type = GenFluidTypes.register(baseName, new BaseFluid(englishName, fluidProperties, opaque ? GenFluidTypes.OPAQUE_STILL : GenFluidTypes.WATER_STILL, opaque ? GenFluidTypes.OPAQUE_FLOW : GenFluidTypes.WATER_FLOWING, GenFluidTypes.WATER_OVERLAY, tintColor, fogColor, opaque, fogStart, fogEnd));

		// Source/Flowing with forward references
		final AtomicReference<Supplier<FlowingFluid>> sourceRef = new AtomicReference<>();
		final AtomicReference<Supplier<FlowingFluid>> flowingRef = new AtomicReference<>();

		BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(type, () -> sourceRef.get().get(), () -> flowingRef.get().get());

		Supplier<FlowingFluid> source = REGISTRY.register(baseName, () -> new BaseFlowingFluid.Source(properties));
		Supplier<FlowingFluid> flowing = REGISTRY.register("flowing_" + baseName, () -> new BaseFlowingFluid.Flowing(properties));
		sourceRef.set(source);
		flowingRef.set(flowing);

		// Fluid blocks intentionally have no BlockItem and therefore are not BlockDefinitions.
		var block = GenBlocks.REGISTRY.registerBlock(baseName, blockProperties -> new LiquidBlock(source.get(), blockProperties), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable());

		ItemDefinition<BucketItem> bucket = GenItems.registerItem(englishName + " Bucket", baseName + "_bucket", (Item.Properties p) -> new BucketItem(source.get(), p.stacksTo(1).craftRemainder(Items.BUCKET)));

		properties.block(block).bucket(bucket);

		FluidDefinition definition = new FluidDefinition(englishName, type, source, flowing, block, bucket);
		FLUIDS.add(definition);
		return definition;
	}

}
