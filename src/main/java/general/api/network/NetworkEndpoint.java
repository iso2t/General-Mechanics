package general.api.network;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * A world block used by, but not necessarily part of, a network interface.
 */
public record NetworkEndpoint(@NotNull BlockPos pos) {
}
