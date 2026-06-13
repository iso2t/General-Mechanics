package general.mechanics.datagen.models;

import general.mechanics.GM;
import general.mechanics.api.electrical.capacitors.CapacitorItem;
import general.mechanics.api.electrical.ics.IntegratedCircuitItem;
import general.mechanics.api.electrical.resistors.ResistorItem;
import general.mechanics.api.electrical.transformers.TransformerItem;
import general.mechanics.api.electrical.transistor.TransistorItem;
import general.mechanics.api.item.ItemDefinition;
import general.mechanics.api.item.base.ElectricalComponent;
import general.mechanics.api.item.element.metallic.ElementItem;
import general.mechanics.api.item.plastic.ColoredPlasticItem;
import general.mechanics.api.item.plastic.PlasticTypeItem;
import general.mechanics.registries.CoreElements;
import general.mechanics.registries.CoreFluids;
import general.mechanics.registries.CoreItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BucketItem;
import org.jetbrains.annotations.NotNull;

public final class ItemModelProvider extends ModelProviders {

	public ItemModelProvider (PackOutput output) {
		super(output);
	}

	@Override
	protected void registerItemModels (@NotNull ItemModelGenerators itemGenerator) {
		for (var item : CoreItems.getItems()) {
			if (item.asItem() instanceof ElectricalComponent) {

				if (item.asItem() instanceof PlasticTypeItem p) {
					plasticItem(item, p.getPlasticType().getDisplayName().toLowerCase(), itemGenerator);
					continue;
				}

				if (item.asItem() instanceof ColoredPlasticItem p) {
					plasticItem(item, p.getParentPlastic().getPlasticType().getDisplayName().toLowerCase(), itemGenerator);
					continue;
				}

				if (item.get() instanceof ResistorItem) {
					electricalComponentItem(item, "resistor", itemGenerator);
					continue;
				}

				if (item.get() instanceof CapacitorItem) {
					electricalComponentItem(item, "capacitor", itemGenerator);
					continue;
				}

				if (item.get() instanceof TransistorItem) {
					electricalComponentItem(item, "transistor", itemGenerator);
					continue;
				}

				if (item.get() instanceof TransformerItem) {
					electricalComponentItem(item, "transformer", itemGenerator);
					continue;
				}

				if (item.get() instanceof IntegratedCircuitItem) {
					electricalComponentItem(item, "integrated_circuit", itemGenerator);
					continue;
				}

				electricalComponentItem(item, item.id().getPath(), itemGenerator);
				continue;
			}

			if (item.get() instanceof BucketItem) {
				boolean generated = false;
				for (var fluid : CoreFluids.getFluids()) {
					if (fluid.bucket().id().equals(item.id())) {
						registerBucketModels(item, itemGenerator);
						generated = true;
						break;
					}
				}
				if (generated) continue; // prevent fallback from overriding the layered bucket model
			}

			itemGenerator.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
		}
		for (var item : CoreElements.getElements()) {
			assert item.get() instanceof ElementItem;
			registerElementModels((ElementItem) item.get(), itemGenerator);
		}
	}

	private void registerBucketModels (ItemDefinition<?> definition, ItemModelGenerators items) {
		var background = GM.getResource("item/bucket/bucket_background");
		var overlay = GM.getResource("item/bucket/bucket_overlay");
		items.generateLayeredItem(definition.get(), new Material(background), new Material(overlay));
	}

	public void electricalComponentItem(ItemDefinition<?> item, String type, ItemModelGenerators generator) {
		generator.generateFlatItem(item.get(), ModelTemplates.createItem("item/components/" + type, TextureSlot.LAYER0));
	}

	private void plasticItem (ItemDefinition<?> item, String parent, ItemModelGenerators generator) {
		generator.generateFlatItem(item.get(), ModelTemplates.createItem("item/plastic/" + parent.toLowerCase().replace(' ', '_'), TextureSlot.LAYER0));
	}

	private void registerElementModels (ElementItem element, ItemModelGenerators items) {
		var nugget = element.getNuggetItem();
		var raw = element.getRawItem();
		var dust = element.getDustItem();
		var plate = element.getPlateItem();
		var pile = element.getPileItem();
		var rod = element.getRodItem();

		items.generateFlatItem(element, ModelTemplates.createItem("item/ingot/ingot", TextureSlot.LAYER0));
		items.generateFlatItem(nugget, ModelTemplates.createItem("item/ingot/nugget", TextureSlot.LAYER0));
		items.generateFlatItem(raw, ModelTemplates.createItem("item/ingot/raw_ore", TextureSlot.LAYER0));
		items.generateFlatItem(dust, ModelTemplates.createItem("item/ingot/dust", TextureSlot.LAYER0));
		items.generateFlatItem(plate, ModelTemplates.createItem("item/ingot/plate", TextureSlot.LAYER0));
		items.generateFlatItem(pile, ModelTemplates.createItem("item/ingot/pile", TextureSlot.LAYER0));
		items.generateFlatItem(rod, ModelTemplates.createItem("item/ingot/rod", TextureSlot.LAYER0));
	}

}
