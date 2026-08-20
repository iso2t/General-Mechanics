package general.mechanics.registries;

import general.api.fluid.BaseFluid;
import general.api.mod.GenAPI;
import general.api.resources.Resource;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GenFluidTypes {

	private static final List<BaseFluid> FLUID_TYPES = new ArrayList<>();

	public static final Identifier WATER_STILL   = Resource.getMinecraftResource("block/water_still");
	public static final Identifier WATER_FLOWING = Resource.getMinecraftResource("block/water_flow");
	public static final Identifier WATER_OVERLAY = Resource.getMinecraftResource("block/water_overlay");
	public static final Identifier OPAQUE_STILL  = Resource.get("block/fluid/opaque_still");
	public static final Identifier OPAQUE_FLOW   = Resource.get("block/fluid/opaque_flow");

	public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, GenAPI.getModId());

	public static List<BaseFluid> getFluidTypes () {
		return Collections.unmodifiableList(FLUID_TYPES);
	}

	static Supplier<FluidType> register (String name, BaseFluid type) {
		FLUID_TYPES.add(type);
		return REGISTRY.register(name, () -> type);
	}

}
