package general.api.client.color;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.resources.Identifier;

public interface IClientColorProvider {

	void registerColor (Identifier identifier, MapCodec<? extends ItemTintSource> codec);

}
