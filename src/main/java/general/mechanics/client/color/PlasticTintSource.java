package general.mechanics.client.color;

import com.mojang.serialization.MapCodec;
import general.api.block.plastic.ColoredPlasticBlock;
import general.api.block.plastic.PlasticTypeBlock;
import general.api.item.plastic.PlasticItem;
import general.api.item.plastic.PlasticTypeItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * Runtime tint source for plastic items. The color is read from the item itself:
 * {@link PlasticItem#getColor()} for color variants and the parent
 * {@link general.api.item.plastic.PlasticType#getDefaultColor()} for the base type item.
 */
public final class PlasticTintSource implements ItemTintSource {

	public static final PlasticTintSource INSTANCE = new PlasticTintSource();
	public static final MapCodec<PlasticTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

	private PlasticTintSource () {}

	@Override
	public int calculate (ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
		Item item = stack.getItem();
		if (item instanceof PlasticItem plastic) {
			return plastic.getColor().getTextureDiffuseColor();
		}
		if (item instanceof PlasticTypeItem type) {
			return type.getPlasticType().getDefaultColor();
		}
		if (item instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof ColoredPlasticBlock colored) {
				return colored.getColor().getTextureDiffuseColor();
			}
			if (blockItem.getBlock() instanceof PlasticTypeBlock type) {
				return type.getPlasticType().getDefaultColor();
			}
		}
		return -1;
	}

	@Override
	public @NonNull MapCodec<? extends ItemTintSource> type () {
		return MAP_CODEC;
	}

}
