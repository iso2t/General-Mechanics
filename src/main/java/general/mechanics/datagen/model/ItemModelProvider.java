package general.mechanics.datagen.model;

import general.api.definitions.ItemDefinition;
import general.api.item.ToolItem;
import general.api.item.materials.IMaterialItem;
import general.api.item.materials.IngotItem;
import general.api.item.plastic.PlasticItem;
import general.api.item.plastic.PlasticTypeItem;
import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.mechanics.client.color.MaterialTintSource;
import general.mechanics.client.color.PlasticTintSource;
import general.mechanics.item.tools.SawItem;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenParts;
import general.mechanics.registries.GenTools;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ItemModelProvider extends ModelProviders {

	private final Set<Identifier> createdPlasticModels = new HashSet<>();
	private final Set<Identifier> createdElementModels = new HashSet<>();

	public ItemModelProvider (PackOutput output) {
		super(output);
	}

	@Override
	protected void registerModels (@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
		for (var item : GenItems.INSTANCE.getItems()) {
			if (item.get() instanceof PlasticItem plastic) {
				plasticItem(item, plastic.getParent().getPlasticType().getDisplayName().toLowerCase(), itemModels);
			} else if (item.get() instanceof PlasticTypeItem plastic) {
				plasticItem(item, plastic.getPlasticType().getDisplayName().toLowerCase(), itemModels);
			} else {
				itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
			}
		}

		for (var tool : GenTools.INSTANCE.getItems()) {
			if (tool.get() instanceof ToolItem) {
				/*if (!(tool.get() instanceof SawItem))*/ itemModels.generateFlatItem(tool.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
				/*else itemModels.generateFlatItem(tool.get(), ModelTemplates.FLAT_HANDHELD_ITEM.extend()
						.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(0, 90, 0).translation(0, 3, 1).scale(0.55f, -0.55f, 0.55f))
						.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0, 90, 0).translation(1.13f, 3.2f, 1.13f).scale(0.68f, -0.68f, 0.68f))
						.build());*/
			}
		}

		for (var part : GenParts.INSTANCE.getItems()) {
			if (part.get() instanceof IngotItem ingot) {
				registerElementModels(ingot, itemModels);
			} else if (part.get() instanceof IMaterialItem) {
				// Element sub-item (raw/nugget/dust/...): emitted by its parent ingot's registerElementModels.
			} else {
				itemModels.generateFlatItem(part.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
			}
		}
	}

	private void registerElementModels (IngotItem ingot, ItemModelGenerators items) {
		elementModel(ingot, "item/ingot/ingot", items);
		elementModel(ingot.getNuggetItem(), "item/ingot/nugget", items);
		elementModel(ingot.getRawItem(), "item/ingot/raw_ore", items);
		elementModel(ingot.getDustItem(), "item/ingot/dust", items);
		elementModel(ingot.getPlateItem(), "item/ingot/plate", items);
		elementModel(ingot.getPileItem(), "item/ingot/pile", items);
		elementModel(ingot.getRodItem(), "item/ingot/rod", items);
		if (ingot.getBoltItem() != null) elementModel(ingot.getBoltItem(), "item/ingot/bolt", items);
		if (ingot.getBoltItem() != null) elementModel(ingot.getScrewItem(), "item/ingot/screw", items);
	}

	private void elementModel (Item item, String path, ItemModelGenerators items) {
		var model = Resource.get(path);
		if (createdElementModels.add(model)) {
			ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(new Material(model)), items.modelOutput);
		}
		items.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, MaterialTintSource.INSTANCE));
	}

	private void sawModel (Item item, ItemModelGenerators items) {
		var model = Resource.getFromItem(item);
		// Flat item, but mirrored vertically in-hand (negative Y scale) so the saw's teeth face away from
		// the player. Only the held contexts are overridden; GUI/ground/etc. inherit item/generated, so the
		// inventory icon keeps its current orientation. (Left-hand auto-mirrors the right-hand transform.)
		var template = ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ITEM)
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(0, 0, 0).translation(0, 3, 1).scale(0.55f, -0.55f, 0.55f))
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0, -90, 25).translation(1.13f, 3.2f, 1.13f).scale(0.68f, -0.68f, 0.68f))
				.build();
		template.create(model, TextureMapping.layer0(new Material(model)), items.modelOutput);
		items.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, MaterialTintSource.INSTANCE));
	}

	private void plasticItem (ItemDefinition<?> item, String parent, ItemModelGenerators generator) {
		var model = Resource.get("item/plastic/" + parent.toLowerCase().replace(' ', '_'));
		if (createdPlasticModels.add(model)) {
			ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(new Material(model)), generator.modelOutput);
		}
		generator.itemModelOutput.accept(item.get(), ItemModelUtils.tintedModel(model, PlasticTintSource.INSTANCE));
	}

	@Override
	protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks () {
		return Stream.empty();
	}

	/** Only non-block items are handled here; block items are owned by {@link BlockModelProvider}. */
	@Override
	protected @NotNull Stream<? extends Holder<Item>> getKnownItems () {
		return BuiltInRegistries.ITEM.listElements()
				.filter(holder -> holder.getKey().identifier().getNamespace().equals(GenAPI.getModId()))
				.filter(holder -> !(holder.value() instanceof BlockItem));
	}
}
