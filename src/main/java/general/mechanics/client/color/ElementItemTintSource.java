package general.mechanics.client.color;

import com.mojang.serialization.MapCodec;
import general.api.block.materials.MetalBlock;
import general.api.item.materials.*;
import general.mechanics.common.item.StampingDieItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class ElementItemTintSource implements ItemTintSource {

	public static final ElementItemTintSource           INSTANCE = new ElementItemTintSource();
	public static final MapCodec<ElementItemTintSource> CODEC    = MapCodec.unit(INSTANCE);

	private ElementItemTintSource () {
	}

	@Override
	public int calculate (ItemStack stack, ClientLevel level, LivingEntity entity) {
		var item = stack.getItem();
		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MetalBlock metal) {
			return metal.getColor();
		}
		if (item instanceof IngotItem ingot) return ingot.getColor();
		if (item instanceof MaterialPartItem part) return part.getColor();
		if (item instanceof StampingDieItem die) return die.getColor();
		return 0xFFFFFFFF;
	}

	@Override
	public @NonNull MapCodec<ElementItemTintSource> type () {
		return CODEC;
	}

}
