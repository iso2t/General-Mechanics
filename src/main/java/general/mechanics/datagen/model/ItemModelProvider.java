package general.mechanics.datagen.model;

import general.api.item.ToolItem;
import general.api.item.materials.DustItem;
import general.api.item.materials.GearItem;
import general.api.item.materials.IngotItem;
import general.api.item.materials.NuggetItem;
import general.api.item.materials.PlateItem;
import general.api.item.materials.RawItem;
import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.mechanics.client.color.ElementItemTintSource;
import general.mechanics.registries.GenItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class ItemModelProvider extends ModelProviders {

	private final Set<Identifier> createdPlasticModels = new HashSet<>();
	private final Set<Identifier> createdElementModels = new HashSet<>();
	private final Set<Identifier> createdRubberModels  = new HashSet<>();

	public ItemModelProvider (PackOutput output) {
		super(output);
	}

	@Override
	protected void registerModels (@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
		for (var item : GenItems.INSTANCE.getItems()) {
			var registeredItem = item.get();
			if (registeredItem instanceof BucketItem bucket) {
				registerFluidBucket(bucket, itemModels);
			} else if (registeredItem instanceof ToolItem) {
				itemModels.generateFlatItem(registeredItem, ModelTemplates.FLAT_HANDHELD_ITEM);
			} else if (registeredItem instanceof IngotItem ingot) {
				registerPartModels(ingot, itemModels);
			} else if (registeredItem instanceof RawItem || registeredItem instanceof NuggetItem || registeredItem instanceof DustItem || registeredItem instanceof PlateItem || registeredItem instanceof GearItem) {
				// These are emitted with their parent ingot so each form can share a common model and texture.
			} else itemModels.generateFlatItem(registeredItem, ModelTemplates.FLAT_HANDHELD_ITEM);
		}
	}

	private void registerFluidBucket (BucketItem bucket, ItemModelGenerators items) {
		var textures = new DynamicFluidContainerModel.Textures(Optional.empty(), Optional.of(new Material(Resource.getMinecraftResource("item/bucket"))), Optional.of(new Material(Resource.getCustomResource("neoforge", "item/mask/bucket_fluid"))), Optional.empty());
		items.itemModelOutput.accept(bucket, new DynamicFluidContainerModel.Unbaked(textures, bucket.getContent(), true, true, true));
	}

	private void registerPartModels (IngotItem ingot, ItemModelGenerators items) {
		// The ingot is always present; every other form is emitted only when GenParts created it.
		partModel(ingot, "item/material/ingot", items);
		if (ingot.getNuggetItem() != null) partModel(ingot.getNuggetItem(), "item/material/nugget", items);
		if (ingot.getRawItem() != null) partModel(ingot.getRawItem(), "item/material/raw_ore", items);
		if (ingot.getDustItem() != null) partModel(ingot.getDustItem(), "item/material/dust", items);
		if (ingot.getPlateItem() != null) partModel(ingot.getPlateItem(), "item/material/plate", items);
		if (ingot.getGearItem() != null) partModel(ingot.getGearItem(), "item/material/gear", items);
	}

	private void partModel (Item item, String path, ItemModelGenerators items) {
		var model = Resource.get(path);
		if (createdElementModels.add(model)) {
			ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(new Material(model)), items.modelOutput);
		}
		items.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, ElementItemTintSource.INSTANCE));
	}

	@Override
	protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks () {
		return Stream.empty();
	}

	/**
	 * Only non-block items are handled here; block items are owned by {@link BlockModelProvider}.
	 */
	@Override
	protected @NotNull Stream<? extends Holder<Item>> getKnownItems () {
		return BuiltInRegistries.ITEM.listElements().filter(holder -> holder.getKey().identifier().getNamespace().equals(GenAPI.getModId())).filter(holder -> !(holder.value() instanceof BlockItem));
	}
}
