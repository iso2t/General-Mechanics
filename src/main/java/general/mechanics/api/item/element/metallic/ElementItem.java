package general.mechanics.api.item.element.metallic;

import general.mechanics.GM;
import general.mechanics.api.item.base.BaseItem;
import general.mechanics.api.item.element.ElementType;
import general.mechanics.api.tags.CoreTags;
import lombok.Getter;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ElementItem extends BaseItem {

    @Getter
    private final ElementType element;

    @Getter
    private final ElementDustItem dustItem;

    @Getter
    private final ElementPlateItem plateItem;

    @Getter
    private final ElementNuggetItem nuggetItem;

    @Getter
    private final ElementRawItem rawItem;

    @Getter
    private final ElementPileItem pileItem;

    @Getter
    private final ElementRodItem rodItem;

    @Getter
    private final Properties properties;

    public ElementItem(Properties properties, ElementType element) {
        super(properties);
        this.properties = properties;
        this.element = element;
        this.dustItem = new ElementDustItem(this);
        this.plateItem = new ElementPlateItem(this);
        this.nuggetItem = new ElementNuggetItem(this);
        this.rawItem = new ElementRawItem(this);
        this.pileItem = new ElementPileItem(this);
        this.rodItem = new ElementRodItem(this);
    }

    public int getAtomicNumber() {
        return element.getAtomicNumber();
    }

    public String getAtomicSymbol() {
        return element.getSymbol();
    }

    public int getTint() {
        return element.getTintColor();
    }

    @Override
	public void appendHoverText (@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        builder.accept(Component.literal(String.format("§e" + getAtomicSymbol())));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }

    public static int getColor(ItemStack stack, int index) {
        Item item = stack.getItem();

        if (item instanceof ElementItem element) {
            return element.getTint();
        }

        return -1;
    }

    public void getRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<InventoryChangeTrigger.TriggerInstance> has) {
        // Ingot -> Nugget
        ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getNuggetItem(), 9)
                .requires(this)
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_to_nugget")));

        // Hammer + this -> Dust
        ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getDustItem(), 1)
                .requires(CoreTags.Items.HAMMERS)
                .requires(this)
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_to_dust")));

        // Hammer + this + this -> Plate
        ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, getPlateItem(),  1)
                .pattern("H")
                .pattern("I")
                .pattern("I")
                .define('H', CoreTags.Items.HAMMERS)
                .define('I', this)
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_to_plate")));

        // Dust -> Raw
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(getDustItem()), RecipeCategory.MISC, CookingBookCategory.MISC, getRawItem(), 0.6f, 200)
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_smelt_to_raw")));

        // Raw -> Ingot
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(getRawItem()), RecipeCategory.MISC, CookingBookCategory.MISC, this, 0.6f, 250)
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_smelt_to_ingot")));

        // Dust -> Pile
        ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getPileItem(), 4)
                .requires(CoreTags.Items.HAMMERS)
                .requires(getDustItem())
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_dust_to_pile")));

        // File + this -> Rod
        ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getRodItem(), 1)
                .requires(CoreTags.Items.FILES)
                .requires(this)
                .unlockedBy("has_element", has)
                .save(consumer, ResourceKey.create(Registries.RECIPE, GM.getResource("elements/" + getRegistryName().getPath() + "_to_rod")));
    }

}
