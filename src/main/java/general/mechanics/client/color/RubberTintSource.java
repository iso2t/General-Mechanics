package general.mechanics.client.color;

import com.mojang.serialization.MapCodec;
import general.api.block.plastic.ColoredPlasticBlock;
import general.api.block.plastic.PlasticTypeBlock;
import general.api.formula.GenFormula;
import general.api.formula.core.Material;
import general.api.item.materials.IMaterialItem;
import general.api.item.plastic.PlasticItem;
import general.api.item.plastic.PlasticTypeItem;
import general.mechanics.item.RubberColoredItem;
import general.mechanics.item.RubberItem;
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
public final class RubberTintSource implements ItemTintSource {

	public static final RubberTintSource           INSTANCE  = new RubberTintSource();
	public static final MapCodec<RubberTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

	private RubberTintSource () {}

	@Override
	public int calculate (ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
		Item item = stack.getItem();
		if (item instanceof RubberColoredItem rubber) {
			return rubber.getColor().getTextureDiffuseColor();
		} else if (level != null && stack.getItem() instanceof IMaterialItem materialItem) {
			return GenFormula.material(level.registryAccess(), materialItem.getMaterial())
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
