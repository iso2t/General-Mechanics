package general.mechanics.compat.jei;

import general.api.crafting.MachineRecipeCatalog;
import general.api.resources.Resource;
import general.api.screens.screen.AbstractScreen;
import general.mechanics.client.crafting.ClientMachineRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
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

	@Override
	public void registerGuiHandlers (@NonNull IGuiHandlerRegistration registration) {
		registration.addGenericGuiContainerHandler(AbstractScreen.class, new MachineScreenGuiHandler());
	}

	private List<MachineRecipeCategory> getCategories () {
		if (categories == null) throw new IllegalStateException("JEI machine categories were accessed before category registration");
		return categories;
	}

	private static final class MachineScreenGuiHandler implements IGuiContainerHandler<AbstractScreen<?>> {

		@Override
		public @NonNull Collection<IGuiClickableArea> getGuiClickableAreas (AbstractScreen<?> screen, double guiMouseX, double guiMouseY) {
			if (!screen.isRecipeViewerButtonHovered(guiMouseX, guiMouseY)) return List.of();

			IRecipeType<?>[] recipeTypes = screen.getMenu().getRecipeDefinitions().stream().map(definition -> (IRecipeType<?>) IRecipeHolderType.create(definition.type())).toArray(IRecipeType<?>[]::new);
			return List.of(IGuiClickableArea.createBasic(AbstractScreen.RECIPE_VIEWER_BUTTON_X, AbstractScreen.RECIPE_VIEWER_BUTTON_Y, AbstractScreen.RECIPE_VIEWER_BUTTON_WIDTH, AbstractScreen.RECIPE_VIEWER_BUTTON_HEIGHT, recipeTypes));
		}

		@Override
		public @NonNull List<Rect2i> getGuiExtraAreas (AbstractScreen<?> screen) {
			List<Rect2i> areas = new ArrayList<>(2);
			if (screen.hasRecipeViewerButton()) areas.add(screen.getRecipeViewerButtonArea());
			if (screen.hasItemLockButton()) areas.add(screen.getItemLockButtonArea());
			return List.copyOf(areas);
		}
	}

}
