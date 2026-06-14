package general.mechanics;

import general.api.mod.GenAPI;
import general.api.resources.Resource;
import general.api.tab.TabBuilder;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenSounds;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class Base implements GenMech {

	static GenMech INSTANCE;

	@Getter
	private final IEventBus bus;

	@Getter
	private final ModContainer container;

	public Base (ModContainer container, IEventBus bus) {
		if (INSTANCE != null) throw new IllegalStateException(String.format("%s is already initialized", NAME));
		INSTANCE = this;
		this.container = container;
		this.bus = bus;

		registerModListeners();
		registerModRegistries();
	}

	private void registerModRegistries () {
		GenItems.INSTANCE.getRegistry().register(getBus());
		GenBlocks.INSTANCE.getRegistry().register(getBus());
		GenSounds.REGISTRY.register(getBus());
	}

	private void registerModListeners () {
		getBus().addListener((RegisterEvent event) -> event.register(Registries.CREATIVE_MODE_TAB, helper -> {
			var multitab = new TabBuilder.MultiTabBuilder();
			multitab.addTab(new TabBuilder.Builder().setTranslationKey(String.format("itemGroup.%s.items", GenAPI.getModId())).setResourceKey(Resource.get("items")).setCreateModeTab(GenItems.INSTANCE).build())
					.addTab(new TabBuilder.Builder().setTranslationKey(String.format("itemGroup.%s.blocks", GenAPI.getModId())).setResourceKey(Resource.get("blocks")).setCreateModeTab(GenBlocks.INSTANCE).build());
			multitab.build(helper);
		}));
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
