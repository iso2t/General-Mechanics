package general.mechanics.datagen.models;

import general.mechanics.GM;
import general.mechanics.api.util.IDataProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

public abstract sealed class ModelProviders extends ModelProvider implements IDataProvider permits BlockModelProvider, ItemModelProvider {

	public ModelProviders (PackOutput output) {
		super(output, GM.MODID);
	}

	@Override
	protected void registerModels (@NotNull BlockModelGenerators blockGenerator, @NotNull ItemModelGenerators itemGenerator) {
		registerBlockModels(blockGenerator, itemGenerator);
		registerItemModels(itemGenerator);
	}

	protected void registerBlockModels (@NotNull BlockModelGenerators blockGenerator, @NotNull ItemModelGenerators itemGenerator) {
	}

	protected void registerItemModels (@NotNull ItemModelGenerators itemGenerator) {
	}

}
