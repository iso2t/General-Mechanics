package general.mechanics.datagen.data;

import general.api.resources.Resource;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class GenDamageTypes {

	public static final ResourceKey<DamageType> HEATING_ELEMENT = ResourceKey.create(Registries.DAMAGE_TYPE, Resource.getMainMod("heating_element"));

	public static void bootstrap (BootstrapContext<DamageType> context) {
		context.register(HEATING_ELEMENT, new DamageType("heating_element", 0.1f, DamageEffects.BURNING));
	}

	public static DamageSource create (Level level, ResourceKey<DamageType> type) {
		return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type));
	}

}
