package general.api.tab;

import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;

public interface ICreativeModeTab {

	void buildDisplayItems (CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output);

	default void init (TabBuilder builder, Registry<CreativeModeTab> registry) {
		builder.build(registry);
	}

}
