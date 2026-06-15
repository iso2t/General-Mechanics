package general.api.item;

import general.api.crafting.IRecipeProvider;
import lombok.NonNull;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ToolItem extends Item implements IRecipeProvider {

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

	@Override
	public ItemLike getCriterionItem () {
		return Items.STICK;
	}

	public int getRemainingUses(ItemStack stack) {
		return durability - stack.getDamageValue();
	}

}
