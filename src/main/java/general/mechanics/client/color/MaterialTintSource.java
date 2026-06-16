package general.mechanics.client.color;

import com.mojang.serialization.MapCodec;
import general.api.formula.GenFormula;
import general.api.formula.core.Material;
import general.api.item.materials.IMaterialItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class MaterialTintSource implements ItemTintSource {

	public static final MaterialTintSource INSTANCE = new MaterialTintSource();

	public static final MapCodec<MaterialTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

	private MaterialTintSource () {}

	@Override
	public int calculate (@NonNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
		if (clientLevel != null && itemStack.getItem() instanceof IMaterialItem materialItem) {
			return GenFormula.material(clientLevel.registryAccess(), materialItem.getMaterial())
					.map(Material::color)
					.orElse(-1);
		}
		return -1;
	}

	@Override
	public @NonNull MapCodec<? extends ItemTintSource> type () {
		return MAP_CODEC;
	}
}
