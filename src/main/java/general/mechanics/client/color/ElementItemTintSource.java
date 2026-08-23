package general.mechanics.client.color;

import com.mojang.serialization.MapCodec;
import general.api.item.materials.DustItem;
import general.api.item.materials.GearItem;
import general.api.item.materials.IngotItem;
import general.api.item.materials.NuggetItem;
import general.api.item.materials.PlateItem;
import general.api.item.materials.RawItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class ElementItemTintSource implements ItemTintSource {

	public static final ElementItemTintSource           INSTANCE = new ElementItemTintSource();
	public static final MapCodec<ElementItemTintSource> CODEC    = MapCodec.unit(INSTANCE);

	private ElementItemTintSource () {
	}

	@Override
	public int calculate (ItemStack stack, ClientLevel level, LivingEntity entity) {
		return switch (stack.getItem()) {
			case IngotItem ingot -> ingot.getColor();
			case DustItem dust -> dust.getParent().getColor();
			case GearItem gear -> gear.getParent().getColor();
			case NuggetItem nugget -> nugget.getParent().getColor();
			case PlateItem plate -> plate.getParent().getColor();
			case RawItem raw -> raw.getParent().getColor();
			default -> 0xFFFFFFFF;
		};
	}

	@Override
	public @NonNull MapCodec<ElementItemTintSource> type () {
		return CODEC;
	}

}
