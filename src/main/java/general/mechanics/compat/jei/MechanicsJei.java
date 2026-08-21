package general.mechanics.compat.jei;

import general.api.crafting.MachineRecipeCatalog;
import general.api.resources.Resource;
import general.mechanics.client.crafting.ClientMachineRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.List;

@JeiPlugin
public class MechanicsJei implements IModPlugin {
	private List<MachineRecipeCategory> categories;

	@Override
	public @NonNull Identifier getPluginUid () {
		return Resource.getMainMod("jei_plugin");
	}

	@Override
	public void registerCategories (@NonNull IRecipeCategoryRegistration registration) {
		categories = MachineRecipeCatalog.definitions().stream().map(definition -> new MachineRecipeCategory(registration.getJeiHelpers().getGuiHelper(), definition)).toList();
		registration.addRecipeCategories(categories.toArray(MachineRecipeCategory[]::new));
	}

	@Override
	public void registerRecipes (@NonNull IRecipeRegistration registration) {
		for (MachineRecipeCategory category : getCategories()) {
			registration.addRecipes(category.getRecipeType(), ClientMachineRecipes.get(category.definition()));
		}
	}

	@Override
	public void registerRecipeCatalysts (@NonNull IRecipeCatalystRegistration registration) {
		for (MachineRecipeCategory category : getCategories()) {
			List<ItemLike> stations = category.craftingStations();
			if (!stations.isEmpty()) registration.addCraftingStation(category.getRecipeType(), stations.toArray(ItemLike[]::new));
		}
	}

	private List<MachineRecipeCategory> getCategories () {
		if (categories == null) throw new IllegalStateException("JEI machine categories were accessed before category registration");
		return categories;
	}

}
