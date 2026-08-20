package general.mechanics;

import general.api.item.ITooltipProvider;
import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.api.tab.TabBuilder;
import general.mechanics.registries.*;
import guideme.Guide;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class MechanicsBase implements Mechanics {

	static Mechanics INSTANCE;

	@Getter
	private final IEventBus bus;

	@Getter
	private final ModContainer container;

	public MechanicsBase (ModContainer container, IEventBus bus) {
		if (INSTANCE != null) throw new IllegalStateException(String.format("%s is already initialized", NAME));
		INSTANCE = this;
		this.container = container;
		this.bus = bus;

		registerModListeners();
		registerModRegistries();
		registerTooltipProviders();

		Guide.builder(Resource.get("guide")).build();
	}

	private void registerModRegistries () {
		GenFluids.register(getBus());
		GenItems.INSTANCE.getRegistry().register(getBus());
		GenBlocks.INSTANCE.getRegistry().register(getBus());

		GenBlockEntities.REGISTRY.register(getBus());
		GenMenus.REGISTRY.register(getBus());
		GenRecipes.register(getBus());
		GenSounds.REGISTRY.register(getBus());
		GenFoliagePlacers.REGISTRY.register(getBus());
		GenMultiblocks.REGISTRY.register(getBus());

		getBus().addListener(GenCapabilities::register);
	}

	private void registerModListeners () {
		getBus().addListener((RegisterEvent event) -> event.register(Registries.CREATIVE_MODE_TAB, helper -> {
			var multitab = new TabBuilder.MultiTabBuilder();
			multitab.addTab(new TabBuilder.Builder().setTranslationKey(String.format("itemGroup.%s.items", GenAPI.getModId())).setDisplayItem(GenItems.REDSTONE_WIRE_SPOOL).setResourceKey(Resource.get("items")).setCreateModeTab(GenItems.INSTANCE).build())
					.addTab(new TabBuilder.Builder().setTranslationKey(String.format("itemGroup.%s.blocks", GenAPI.getModId())).setResourceKey(Resource.get("blocks")).setCreateModeTab(GenBlocks.INSTANCE).build());
			multitab.build(helper);
		}));
	}

	// Attaches each ITooltipProvider's data components as item defaults.
	private void registerTooltipProviders () {
		getBus().addListener((ModifyDefaultComponentsEvent event) -> {
			for (var def : GenItems.INSTANCE.getItems()) {
				if (def.get() instanceof ITooltipProvider provider) {
					event.modify(def.get(), (builder, lookup, item) -> provider.addTooltipComponents(builder));
				}
			}
			for (var def : GenBlocks.INSTANCE.getBlocks()) {
				if (def.get() instanceof ITooltipProvider provider) {
					event.modify(def.asItem(), (builder, lookup, item) -> provider.addTooltipComponents(builder));
				}
			}
		});
	}

	@Override
	public Collection<ServerPlayer> getPlayers () {
		var server = getCurrentServer();

		if (server != null) {
			return server.getPlayerList().getPlayers();
		}

		return Collections.emptyList();
	}

	@Nullable
	@Override
	public MinecraftServer getCurrentServer () {
		return ServerLifecycleHooks.getCurrentServer();
	}

}
