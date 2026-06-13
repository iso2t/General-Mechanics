package general.mechanics.compat.jei.categories;

import general.mechanics.GM;
import general.mechanics.recipes.CrushingRecipe;
import general.mechanics.registries.CoreBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrushingCategory implements IRecipeCategory<CrushingRecipe> {

    public static final Identifier       UID     = GM.getResource("crushing");
    public static final Identifier TEXTURE = GM.getResource("textures/gui/crusher.png");

    public static final IRecipeType<CrushingRecipe> TYPE = IRecipeType.create(UID, CrushingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CrushingCategory (IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CoreBlocks.MATTER_FABRICATOR));
    }

    @Override
    public @NotNull IRecipeType<CrushingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.gm.crusher");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void draw (@NotNull CrushingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
    }

	@Override
    public void setRecipe (@NotNull IRecipeLayoutBuilder builder, CrushingRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 35).add(recipe.input());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).add(recipe.output());
    }
}