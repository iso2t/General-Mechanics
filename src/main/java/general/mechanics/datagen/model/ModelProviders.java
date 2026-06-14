package general.mechanics.datagen.model;

import general.api.mod.GenAPI;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public abstract sealed class ModelProviders extends ModelProvider permits BlockModelProvider, ItemModelProvider {

	public ModelProviders (PackOutput output) {
		super(output, GenAPI.getModId());
	}

}
