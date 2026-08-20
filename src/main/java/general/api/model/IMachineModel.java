package general.api.model;

import general.api.resources.Resource;
import net.minecraft.resources.Identifier;

/**
 * Represents a model interface used to define texture mappings for machines.
 * Provides default methods to retrieve textures for the top, side, front, and bottom faces of the model.
 */
public interface IMachineModel {

	/**
	 * Retrieves the texture used for the top face of the machine model.
	 *
	 * @return an {@link Identifier} pointing to the resource location of the top texture.
	 */
	default Identifier getTopTexture () {
		return Resource.getMainMod("block/machine/machine_top");
	}

	/**
	 * Retrieves the texture used for the side face of the machine model.
	 *
	 * @return an {@link Identifier} pointing to the resource location of the side texture.
	 */
	default Identifier getSideTexture () {
		return Resource.getMainMod("block/machine/machine_side");
	}

	/**
	 * Retrieves the texture used for the front face of the machine model.
	 * By default, this method returns the same texture used for the side face.
	 *
	 * @return an {@link Identifier} pointing to the resource location of the front texture.
	 */
	default Identifier getFrontTexture () {
		return getSideTexture();
	}

	/**
	 * Retrieves the texture used for the bottom face of the machine model.
	 *
	 * @return an {@link Identifier} pointing to the resource location of the bottom texture.
	 */
	default Identifier getBottomTexture () {
		return Resource.getMainMod("block/machine/machine_bottom");
	}

}
