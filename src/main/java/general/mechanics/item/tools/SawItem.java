package general.mechanics.item.tools;

import general.api.crafting.IRecipeProvider;
import general.api.item.ToolItem;
import general.api.tag.CoreTags;
import general.mechanics.registries.GenParts;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.NonNull;

public class SawItem extends ToolItem {

	public SawItem (Properties properties) {
		super(properties, 143);
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1)
				.pattern("PPS")
				.pattern("FHS")
				.define('P', GenParts.STEEL.get().getPlateItem())
				.define('S', Items.STICK).define('F', CoreTags.Items.FILES)
				.define('H', CoreTags.Items.HAMMERS)
				.unlockedBy("has_any", criterion)
				.save(consumer, IRecipeProvider.createKey("tools/saw"));
	}

	@Override
	public boolean canPerformAction (@NonNull ItemInstance stack, @NonNull ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
	}

	@Override
	public @NonNull InteractionResult useOn (UseOnContext context) {
		var level = context.getLevel();
		var pos = context.getClickedPos();
		var player = context.getPlayer();
		var block = level.getBlockState(pos);

		if (player == null || !player.getActiveItem().is(CoreTags.Items.SAWS) || !(block.is(BlockTags.LOGS_THAT_BURN) || block.is(BlockTags.PLANKS))) return InteractionResult.PASS;
		if (level.destroyBlock(pos, true, player)) {
			this.damageItem(player.getActiveItem(), 1, player, _ -> player.getActiveItem().hurtAndBreak(1, player, EquipmentSlot.MAINHAND));
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

}
