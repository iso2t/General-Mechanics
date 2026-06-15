package general.mechanics.item;

import general.api.crafting.IRecipeProvider;
import general.mechanics.registries.GenItems;
import lombok.Getter;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class WireSpoolItem extends Item implements IRecipeProvider {

	public static final int MAX_USES = 5;

	@Getter
	private final Item heldWire;

	public WireSpoolItem (Properties properties, Item heldWire) {
		super(properties.stacksTo(1).durability(MAX_USES));
		this.heldWire = heldWire;
	}

	@Override
	public @Nullable ItemStackTemplate getCraftingRemainder (@NonNull ItemInstance instance) {
		var itemStack = (instance instanceof ItemStackTemplate template) ? template.create() : (ItemStack) instance;
		if (itemStack.getDamageValue() >= MAX_USES - 1) {
			// Return basic spool when uses are exhausted
			return new ItemStackTemplate(GenItems.WIRE_SPOOL.get());
		} else {
			// Damage the item by 1 use
			ItemStack result = itemStack.copy();
			result.setDamageValue(itemStack.getDamageValue() + 1);
			return new ItemStackTemplate(result.getItem(), result.getDamageValue());
		}
	}

	@Override
	public boolean isBarVisible (ItemStack stack) {
		return stack.getDamageValue() > 0;
	}

	@Override
	public int getBarWidth (ItemStack stack) {
		return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) MAX_USES);
	}

	@Override
	public int getBarColor (ItemStack stack) {
		float f = Math.max(0.0F, (float) (MAX_USES - stack.getDamageValue()) / (float) MAX_USES);
		return java.awt.Color.HSBtoRGB(f / 3.0F, 1.0F, 1.0F);
	}

	/**
	 * Checks if this wire spool is a copper wire spool
	 */
	public boolean isCopperWireSpool (ItemStack stack) {
		return stack.getItem() == GenItems.COPPER_WIRE_SPOOL.get();
	}

	/**
	 * Checks if this wire spool is a redstone wire spool
	 */
	public boolean isRedstoneWireSpool (ItemStack stack) {
		return stack.getItem() == GenItems.REDSTONE_WIRE_SPOOL.get();
	}

	/**
	 * Checks if this wire spool is a basic spool
	 */
	public boolean isBasicSpool (ItemStack stack) {
		return stack.getItem() == GenItems.WIRE_SPOOL.get();
	}

	/**
	 * Gets the remaining uses of the wire spool
	 */
	public int getRemainingUses (ItemStack stack) {
		return MAX_USES - stack.getDamageValue();
	}

	/**
	 * Checks if the wire spool has any uses remaining
	 */
	public boolean hasUsesRemaining (ItemStack stack) {
		return getRemainingUses(stack) > 0;
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, this).requires(GenItems.WIRE_SPOOL.get()).requires(getHeldWire()).unlockedBy("has_any", criterion).save(consumer, createKey(this.descriptionId + "_from_spool"));
	}

	@Override
	public ItemLike getCriterionItem () {
		return getHeldWire();
	}
}
