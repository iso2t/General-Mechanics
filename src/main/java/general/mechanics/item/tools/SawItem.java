package general.mechanics.item.tools;

import general.api.crafting.IRecipeProvider;
import general.api.item.ToolItem;
import general.api.tag.CoreTags;
import general.mechanics.block.RubberLogBlock;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenParts;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.NonNull;

public class SawItem extends ToolItem {

	public SawItem (Properties properties) {
		super(properties.axe(ToolMaterial.IRON, 1.5f, -3.f), 143);
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
			context.getItemInHand().hurtAndBreak(1, player, context.getHand());
			dropSawdust(level, pos);

			if (RubberLogBlock.isSappy(block)) dropRubberResin(level, pos);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	/**
	 * Drops sawdust at the specified block position in the world when certain conditions are met.
	 * The method only executes on the server side and has a random chance to spawn sawdust items.
	 *
	 * @param level The game world where the sawdust should be dropped.
	 * @param pos The position of the block where the sawdust is dropped.
	 */
	private void dropSawdust (Level level, BlockPos pos) {
		if (level.isClientSide()) return;

		if (level.getRandom().nextFloat() < 0.15f) {
			Block.popResource(level, pos, new ItemStack(GenItems.SAWDUST.get(), level.getRandom().nextInt(1, 4)));
		}
	}

	/**
	 * Drops rubber resin at the specified block position in the world if certain conditions are met.
	 * The method does not execute on the client side and has a random chance to spawn rubber resin items.
	 *
	 * @param level The game world where the rubber resin should be dropped.
	 * @param pos The position of the block where the rubber resin is dropped.
	 */
	private void dropRubberResin (Level level, BlockPos pos) {
		if (level.isClientSide()) return;

		if (level.getRandom().nextFloat() < 0.5f) {
			Block.popResource(level, pos, new ItemStack(GenItems.TREE_SAP.get(), level.getRandom().nextInt(1, 3)));
		}
	}

}
