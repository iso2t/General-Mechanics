package general.api.fluid;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector3f;

public class BaseFluid extends FluidType {

	@Getter
	private final Identifier stillTexture;

	@Getter
	private final Identifier flowingTexture;

	@Getter
	private final Identifier overlayTexture;

	@Getter
	private final int tintColor;

	@Getter
	private final Vector3f fogColor;

	@Getter
	private final String englishName;

	@Getter
	private final boolean opaque;

	@Getter
	private final float fogStart;

	@Getter
	private final float fogEnd;

	public BaseFluid (String englishName, Properties properties, Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture, int tintColor, Vector3f fogColor, boolean opaque, float fogStart, float fogEnd) {
		super(properties);
		this.englishName = englishName;
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.overlayTexture = overlayTexture;
		this.tintColor = tintColor;
		this.fogColor = fogColor;
		this.opaque = opaque;
		this.fogStart = fogStart;
		this.fogEnd = fogEnd;
	}

}
