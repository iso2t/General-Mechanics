package general.api.tab;

import general.api.definitions.ItemDefinition;
import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.mechanics.GenMech;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class TabBuilder {

	private final String                       translationKey;
	private final ResourceKey<CreativeModeTab> tabKey;
	private final Supplier<ItemStack>          displayItem;
	private final ICreativeModeTab               createModeTab;

	public TabBuilder (String translationKey, Identifier resourceKey, ItemDefinition<?> displayItem, ICreativeModeTab createModeTab) {
		this(translationKey, resourceKey, displayItem::getStack, createModeTab);
	}

	public TabBuilder (String translationKey, Identifier resourceKey, ItemStack displayItem, ICreativeModeTab createModeTab) {
		this(translationKey, resourceKey, () -> displayItem, createModeTab);
	}

	private TabBuilder (String translationKey, Identifier resourceKey, Supplier<ItemStack> displayItem, ICreativeModeTab createModeTab) {
		this.translationKey = translationKey;
		this.tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, resourceKey);
		this.displayItem = displayItem;
		this.createModeTab = createModeTab;
	}

	public void build (Registry<CreativeModeTab> registry) {
		Registry.register(registry, tabKey, createTab(createModeTab));
	}

	public void build (RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
		helper.register(tabKey, createTab(createModeTab));
	}

	private CreativeModeTab createTab (ICreativeModeTab tabContents) {
		return CreativeModeTab.builder().title(Component.translatable(translationKey)).icon(displayItem).displayItems(tabContents::buildDisplayItems).build();
	}

	public static class Builder {

		private String            translationKey = String.format("itemGroup.%s.unknown", GenAPI.getModId());
		private Identifier        resourceKey    = Resource.get("default");
		private ItemDefinition<?> displayItem    = ItemDefinition.of(Items.APPLE);
		private ICreativeModeTab    createModeTab  = (_, output) -> output.accept(ItemStack.EMPTY);

		public Builder setTranslationKey (String translationKey) {
			this.translationKey = translationKey;
			return this;
		}

		public Builder setResourceKey (Identifier resourceKey) {
			this.resourceKey = resourceKey;
			return this;
		}

		public Builder setDisplayItem (ItemDefinition<?> displayItem) {
			this.displayItem = displayItem;
			return this;
		}

		public Builder setCreateModeTab (ICreativeModeTab createModeTab) {
			this.createModeTab = createModeTab;
			return this;
		}

		public TabBuilder build () {
			return new TabBuilder(translationKey, resourceKey, displayItem, createModeTab);
		}

	}

	public static class MultiTabBuilder extends Builder {

		private final ArrayList<TabBuilder> tabs = new ArrayList<>();

		public MultiTabBuilder addTab (TabBuilder tab) {
			tabs.add(tab);
			return this;
		}

		public void build (Registry<CreativeModeTab> registry) {
			build(registry, true);
		}

		public void build (Registry<CreativeModeTab> registry, boolean combineTabs) {
			if (!combineTabs) {
				for (var tab : tabs) tab.build(registry);
				return;
			}

			if (tabs.isEmpty()) throw new IllegalStateException("Cannot build a creative mode tab without any tabs.");
			var mainTab = tabs.getFirst();
			Registry.register(registry, mainTab.tabKey, mainTab.createTab(this::buildDisplayItems));
		}

		public void build (RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
			build(helper, true);
		}

		public void build (RegisterEvent.RegisterHelper<CreativeModeTab> helper, boolean combineTabs) {
			if (!combineTabs) {
				for (var tab : tabs) tab.build(helper);
				return;
			}

			if (tabs.isEmpty()) throw new IllegalStateException("Cannot build a creative mode tab without any tabs.");
			var mainTab = tabs.getFirst();
			helper.register(mainTab.tabKey, mainTab.createTab(this::buildDisplayItems));
		}

		private void buildDisplayItems (CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
			for (var childTab : tabs) {
				childTab.createModeTab.buildDisplayItems(parameters, output);
			}
		}

	}

}
