package general.api.item.materials;

import general.api.crafting.IRecipeProvider;
import general.api.resources.Resource;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class IngotItem extends Item implements IRecipeProvider {

	@Getter
	private final Properties properties;

	@Getter
	@Setter
	private DustItem dustItem;

	@Getter
	@Setter
	private NuggetItem nuggetItem;

	@Getter
	@Setter
	private PlateItem plateItem;

	@Getter
	@Setter
	private RawItem rawItem;

	@Getter
	@Setter
	private GearItem gearItem;

	public IngotItem (Properties properties) {
		super(properties);
		this.properties = properties;
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		var path = Resource.getFromItem(this).getPath();

		// Each recipe is emitted only when every sub-item it references exists — GenParts may skip forms
		// the material doesn't support, leaving the matching getter null.

		// Ingot -> Nugget
		if (getNuggetItem() != null) {
			ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getNuggetItem(), 9).requires(this).unlockedBy("has_element", criterion).save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_nugget"));
		}

		// Hammer + this -> Dust
		/*if (getDustItem() != null) {
			ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getDustItem(), 1)
					.requires(CoreTags.Items.HAMMERS)
					.requires(this)
					.unlockedBy("has_element", criterion)
					.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_dust"));
		}*/

		// Hammer + this + this -> Plate
		/*if (getPlateItem() != null) {
			ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, getPlateItem(), 1)
					.pattern("H")
					.pattern("I")
					.pattern("I")
					.define('H', CoreTags.Items.HAMMERS)
					.define('I', this)
					.unlockedBy("has_element", criterion)
					.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_plate"));
		}*/

		// Dust -> Raw
		/*if (getDustItem() != null && getRawItem() != null) {
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(this::getDustItem), RecipeCategory.MISC, CookingBookCategory.MISC, this::getRawItem, 0.6f, 200)
					.unlockedBy("has_element", criterion)
					.save(consumer, IRecipeProvider.createKey("materials/" + path + "_smelt_to_raw"));
		}

		// Raw -> Ingot
		if (getRawItem() != null) {
			SimpleCookingRecipeBuilder.smelting(Ingredient.of(this::getRawItem), RecipeCategory.MISC, CookingBookCategory.MISC, this, 0.6f, 250)
					.unlockedBy("has_element", criterion)
					.save(consumer, IRecipeProvider.createKey("materials/" + path + "_smelt_to_ingot"));
		}

		if (getGearItem() != null) {
			ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this::getGearItem, 1)
					.pattern("FPS")
					.pattern("P P")
					.pattern("DPH")
					.define('F', CoreTags.Items.FILES)
					.define('H', CoreTags.Items.HAMMERS)
					.define('S', CoreTags.Items.SAWS)
					.define('D', CoreTags.Items.SOCKET_DRIVERS)
					.define('P', this::getPlateItem)
					.unlockedBy("has_any", criterion)
					.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_gear"));
		}*/
	}

	@Override
	public ItemLike getCriterionItem () {
		// Fall back to the always-present ingot when this material has no pile form.
		return getRawItem() != null ? this::getRawItem : this;
	}
}
