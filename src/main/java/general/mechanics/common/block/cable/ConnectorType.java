package general.mechanics.common.block.cable;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum ConnectorType implements StringRepresentable {

	NONE,
	CABLE,
	BLOCK;

	public static final ConnectorType[] VALUES = values();

	@Override
	public @NonNull String getSerializedName () {
		return name().toLowerCase();
	}

}
