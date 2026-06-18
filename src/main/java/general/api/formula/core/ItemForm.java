package general.api.formula.core;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The default item forms a {@link Material} can be expressed as (dust, ingot, plate, ...).
 *
 * <p><b>Design-only in v1.</b> This enum names the forms and is carried on {@link Material}, but the
 * Formula API does not yet auto-generate the corresponding items/blocks/datagen. A future pass can
 * read {@link Material#defaultForms()} and register the matching content.
 */
public enum ItemForm implements StringRepresentable {
	DUST,
	PILE,
	INGOT,
	NUGGET,
	PLATE,
	RAW,
	BOLT,
	SCREW,
	ROD,
	WIRE,
	GEAR,
	BLOCK,
	FLUID,
	GAS,
	CRYSTAL,
	POLYMER,
	CIRCUIT;

	public static final Codec<ItemForm> CODEC = StringRepresentable.fromEnum(ItemForm::values);

	private final String serializedName = name().toLowerCase(Locale.ROOT);

	public static List<ItemForm> getDefaultForms () {
		return List.of(DUST, PILE, INGOT, NUGGET, PLATE, RAW, BOLT, SCREW, ROD);
	}

	public static List<ItemForm> getDefaultFormsAndGear() {
		var forms = new ArrayList<>(getDefaultForms());
		forms.add(GEAR);
		return forms;
	}

	@Override
	public @NonNull String getSerializedName () {
		return serializedName;
	}
}
