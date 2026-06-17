package general.api.item.materials;

import general.api.crafting.IRecipeProvider;
import general.api.formula.core.Material;
import general.api.formula.tooltip.FormulaTooltip;
import general.api.resources.Resource;
import general.api.tag.CoreTags;
import general.mechanics.registries.GenComponents;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class IngotItem extends Item implements IRecipeProvider, IMaterialItem {

	@Getter
	private final ResourceKey<Material> material;

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
	private PileItem pileItem;

	@Getter
	@Setter
	private PlateItem plateItem;

	@Getter
	@Setter
	private RawItem rawItem;

	@Getter
	@Setter
	private RodItem rodItem;

	@Getter
	@Setter
	private BoltItem boltItem;

	@Getter
	@Setter
	private ScrewItem screwItem;

	public IngotItem (Properties properties, ResourceKey<Material> material) {
		super(properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(material)));
		this.properties = properties.component(GenComponents.FORMULA_TOOLTIP.get(), FormulaTooltip.ofMaterial(material));
		this.material = material;
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		var path = Resource.getFromItem(this).getPath();

		// Ingot -> Nugget
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getNuggetItem(), 9)
				.requires(this)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_nugget"));

		// Hammer + this -> Dust
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, getDustItem(), 1)
				.requires(CoreTags.Items.HAMMERS)
				.requires(this)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_dust"));

		// Hammer + this + this -> Plate
		ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, getPlateItem(), 1)
				.pattern("H")
				.pattern("I")
				.pattern("I")
				.define('H', CoreTags.Items.HAMMERS)
				.define('I', this)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_plate"));

		// Dust -> Raw
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(this::getDustItem), RecipeCategory.MISC, CookingBookCategory.MISC, this::getRawItem, 0.6f, 200)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_smelt_to_raw"));

		// Raw -> Ingot
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(this::getRawItem), RecipeCategory.MISC, CookingBookCategory.MISC, this, 0.6f, 250)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_smelt_to_ingot"));

		// Dust -> Pile
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, this::getPileItem, 4)
				.requires(CoreTags.Items.HAMMERS)
				.requires(this::getDustItem)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_dust_to_pile"));

		// File + this -> Rod
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, this::getRodItem, 1)
				.requires(CoreTags.Items.FILES)
				.requires(this)
				.unlockedBy("has_element", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_rod"));

		// Bolt
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, this::getBoltItem)
				.requires(this::getScrewItem)
				.requires(CoreTags.Items.FILES)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_bolt"));

		// Screw
		ShapelessRecipeBuilder.shapeless(holder, RecipeCategory.MISC, this::getScrewItem, 2)
				.requires(CoreTags.Items.FILES)
				.requires(CoreTags.Items.SAWS)
				.requires(this)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("materials/" + path + "_to_screw"));
	}

	@Override
	public ItemLike getCriterionItem () {
		return this::getPileItem;
	}
}
