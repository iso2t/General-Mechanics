package general.mechanics.api.item.tools;

import general.mechanics.GM;
import general.mechanics.api.tags.CoreTags;
import general.mechanics.datagen.recipes.CoreRecipeProvider;
import general.mechanics.registries.CoreElements;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;

public class WireCuttersItem extends ToolItem {

    public WireCuttersItem(Properties properties) {
        super(properties, 143);
    }

    @Override
    public void registerCraftingRecipe (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
        ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
                .pattern("PFP")
                .pattern("HPS")
                .pattern("RBR")
                .define('P', CoreElements.STEEL_INGOT.get().getPlateItem())
                .define('F', CoreTags.Items.FILES)
                .define('H', CoreTags.Items.HAMMERS)
                .define('S', CoreTags.Items.SOCKET_DRIVERS)
                .define('R', CoreElements.STEEL_INGOT.get().getRodItem())
                .define('B', CoreTags.Items.BOLTS)
                .unlockedBy("has_any", criterion)
                .save(consumer, CoreRecipeProvider.createKey("tools/wire_cutters"));
    }
}
