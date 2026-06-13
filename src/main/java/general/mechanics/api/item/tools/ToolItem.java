package general.mechanics.api.item.tools;

import general.mechanics.api.item.base.BaseItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class ToolItem extends BaseItem {

    private final int durability;

    public ToolItem (Properties properties, int durability) {
        super(properties.durability(durability).setNoCombineRepair().stacksTo(1));
        this.durability = durability;
    }

    @Override
    public boolean isDamageable (@NotNull ItemStack stack) {
        return true;
    }

	@Override
	public @Nullable ItemStackTemplate getCraftingRemainder (@NonNull ItemInstance instance) {
		var template = new ItemStackTemplate(this);
		var itemStack = new ItemStack(template.item());

		if (itemStack.getDamageValue() >= durability - 1) {
			return ItemStackTemplate.fromNonEmptyStack(ItemStack.EMPTY);
		} else {
			ItemStack result = itemStack.copy();
			result.setDamageValue(itemStack.getDamageValue() + 1);
			return ItemStackTemplate.fromNonEmptyStack(result);
		}
	}

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }

    public int getRemainingUses(ItemStack stack) {
        return durability - stack.getDamageValue();
    }

    public abstract void registerCraftingRecipe (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> has);

}
