package general.api.crafting;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

/**
 * Complete, machine-neutral context supplied to a {@link RecipeDataProvider}.
 * It exposes the full registry lookup while centralizing common unlock, save,
 * and namespace behavior.
 */
public final class RecipeGenerationContext {

	public static final String DEFAULT_UNLOCK_CRITERION = "has_any";

	private final HolderLookup.Provider registries;
	private final RecipeOutput          output;
	private final Identifier            ownerId;
	private final ItemLike              unlockItem;
	private final Criterion<?>          unlockCriterion;

	public RecipeGenerationContext (HolderLookup.Provider registries, RecipeOutput output, Identifier ownerId, ItemLike unlockItem) {
		this.registries = Objects.requireNonNull(registries, "registries");
		this.output = Objects.requireNonNull(output, "output");
		this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
		this.unlockItem = Objects.requireNonNull(unlockItem, "unlockItem");
		this.unlockCriterion = criterion(unlockItem);
	}

	public HolderLookup.Provider registries () {
		return registries;
	}

	public HolderGetter<Item> items () {
		return registries.lookupOrThrow(Registries.ITEM);
	}

	public HolderGetter<Fluid> fluids () {
		return registries.lookupOrThrow(Registries.FLUID);
	}

	public RecipeOutput output () {
		return output;
	}

	/**
	 * Registry identifier of the item or block that owns this callback.
	 */
	public Identifier ownerId () {
		return ownerId;
	}

	public ItemLike unlockItem () {
		return unlockItem;
	}

	public Criterion<?> unlockCriterion () {
		return unlockCriterion;
	}

	public Criterion<?> criterion (ItemLike... items) {
		Objects.requireNonNull(items, "items");
		return InventoryChangeTrigger.TriggerInstance.hasItems(items);
	}

	public Criterion<?> criterion (TagKey<Item> tag) {
		Objects.requireNonNull(tag, "tag");
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items(), tag));
	}

	/**
	 * Adds the context's standard unlock criterion and returns the same builder.
	 */
	public <B extends RecipeBuilder> B unlocked (B builder) {
		return unlocked(builder, DEFAULT_UNLOCK_CRITERION, unlockCriterion);
	}

	/**
	 * Adds a named unlock criterion and returns the same builder.
	 */
	public <B extends RecipeBuilder> B unlocked (B builder, String criterionName, Criterion<?> criterion) {
		Objects.requireNonNull(builder, "builder").unlockedBy(requireCriterionName(criterionName), Objects.requireNonNull(criterion, "criterion"));
		return builder;
	}

	/**
	 * Applies the standard unlock criterion and saves with the builder's default ID.
	 */
	public void save (RecipeBuilder builder) {
		unlocked(builder).save(output);
	}

	/**
	 * Applies the standard unlock criterion and saves under this owner's namespace.
	 */
	public void save (RecipeBuilder builder, String path) {
		unlocked(builder).save(output, recipeKey(path));
	}

	/**
	 * Applies a custom criterion name with the context's default criterion.
	 */
	public void save (RecipeBuilder builder, String path, String criterionName) {
		unlocked(builder, criterionName, unlockCriterion).save(output, recipeKey(path));
	}

	/**
	 * Saves a machine recipe under this owner's namespace.
	 */
	public void save (MachineRecipeBuilder<?> builder, String path) {
		Objects.requireNonNull(builder, "builder").save(output, recipeKey(path));
	}

	public ResourceKey<Recipe<?>> recipeKey (String path) {
		Objects.requireNonNull(path, "path");
		Identifier id = Identifier.tryBuild(ownerId.getNamespace(), path);
		if (id == null) throw new IllegalArgumentException("Invalid recipe identifier '" + ownerId.getNamespace() + ":" + path + "'");
		return ResourceKey.create(Registries.RECIPE, id);
	}

	private static String requireCriterionName (String name) {
		Objects.requireNonNull(name, "criterionName");
		if (name.isBlank()) throw new IllegalArgumentException("Recipe unlock criterion name must not be blank");
		return name;
	}
}
