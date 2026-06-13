package general.mechanics.compat.jei.categories;

import general.mechanics.GM;
import general.mechanics.recipes.FluidMixingRecipe;
import general.mechanics.registries.CoreBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class FluidMixingCategory implements IRecipeCategory<FluidMixingRecipe> {

    public static final Identifier UID     = GM.getResource("fluid_mixing");
    public static final Identifier TEXTURE = GM.getResource("textures/gui/fluid_mixer.png");

    public static final IRecipeType<FluidMixingRecipe> TYPE =
            IRecipeType.create(UID, FluidMixingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public FluidMixingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CoreBlocks.MATTER_FABRICATOR));
    }

    @Override
    public @NotNull IRecipeType<FluidMixingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.gm.fluid_mixer");
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
    public void draw (@NotNull FluidMixingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, FluidMixingRecipe recipe, @NotNull IFocusGroup focuses) {
        final int baseX = 20, baseY = 35, step = 18, cols = 3;

        var inputs = recipe.inputs();                // List<SizedFluidIngredient>
        int count = Math.min(inputs.size(), 3);

        for (int i = 0; i < count; i++) {
            var sfi = inputs.get(i);

            int col = i % cols;
            int x = baseX + col * step;
            int y = baseY;

            var displayStacks = Arrays.stream(sfi.ingredient().fluids().toArray()).toList().stream().map(holder -> new FluidStack((Fluid) holder, sfi.amount())).toList();

            builder.addSlot(RecipeIngredientRole.INPUT, x, y).setFluidRenderer(Math.max(1000, sfi.amount()), true, 16, 16).addIngredients(NeoForgeTypes.FLUID_STACK, displayStacks);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).setFluidRenderer(Math.max(1000, recipe.result().getAmount()), true, 16, 16).add(NeoForgeTypes.FLUID_STACK, recipe.result().copy());
    }
}