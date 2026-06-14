package general.mechanics;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;

public interface GenMech {

	String NAME   = "General Mechanics";
	String MOD_ID = "generalmechanics";

	Logger LOGGER = LoggerFactory.getLogger(NAME);

	static GenMech getInstance () {
		return Base.INSTANCE;
	}

	static Path getGameDirectory () {
		return FMLPaths.GAMEDIR.get();
	}

	static Path getConfigDirectory () {
		return FMLPaths.CONFIGDIR.get();
	}

	static Path getModsDirectory () {
		return FMLPaths.MODSDIR.get();
	}

	Collection<ServerPlayer> getPlayers ();

	Level getClientLevel ();

	MinecraftServer getCurrentServer ();

}
