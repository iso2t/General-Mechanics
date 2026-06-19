package general.mechanics.item.tools;

import general.api.crafting.IRecipeProvider;
import general.api.item.ToolItem;
import general.api.tag.CoreTags;
import general.mechanics.block.RubberLogBlock;
import general.mechanics.registries.GenItems;
import general.mechanics.registries.GenParts;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SawItem extends ToolItem {

	public SawItem (Properties properties) {
		super(properties.axe(ToolMaterial.IRON, 1.5f, -3.f), 143);
	}

	@Override
	public boolean mineBlock (@NonNull ItemStack stack, @NonNull Level level, @NonNull BlockState state, @NonNull BlockPos pos, @NonNull LivingEntity owner) {
		boolean result = super.mineBlock(stack, level, state, pos, owner);
		if (level.isClientSide() || !(owner instanceof Player player)) return result;

		if (state.is(BlockTags.PLANKS)) {
			dropSawdust(level, pos, 0.15f);
		} else if (state.is(BlockTags.LOGS_THAT_BURN)) {
			dropSawdust(level, pos, 0.15f);
			if (RubberLogBlock.isSappy(state)) dropRubberResin(level, pos);
			if (!player.isCrouching()) fellTreeUpward(stack, level, pos, state.getBlock(), player);
		}

		return result;
	}

	private void fellTreeUpward (ItemStack stack, Level level, BlockPos origin, Block logBlock, Player player) {
		int minY = origin.getY();
		Set<BlockPos> visited = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		visited.add(origin);
		queue.add(origin);

		int felled = 0;
		while (!queue.isEmpty() && felled < 256) {
			BlockPos cur = queue.poll();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						if (dx == 0 && dy == 0 && dz == 0) continue;
						BlockPos n = cur.offset(dx, dy, dz);
						if (n.getY() < minY || !visited.add(n)) continue;

						BlockState ns = level.getBlockState(n);
						if (!ns.is(logBlock)) continue;

						if (RubberLogBlock.isSappy(ns)) dropRubberResin(level, n);
						level.destroyBlock(n, true, player);
						dropSawdust(level, n, 0.15f);
						stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
						if (stack.isEmpty()) return; // saw broke — stop

						queue.add(n);
						felled++;
					}
				}
			}
		}
	}

	@Override
	public void registerCraftingRecipes (HolderGetter<Item> holder, RecipeOutput consumer, Criterion<?> criterion) {
		ShapedRecipeBuilder.shaped(holder, RecipeCategory.MISC, this, 1).pattern("PPS").pattern("FHS").define('P', GenParts.STEEL.get().getPlateItem()).define('S', Items.STICK).define('F', CoreTags.Items.FILES).define('H', CoreTags.Items.HAMMERS).unlockedBy("has_any", criterion).save(consumer, IRecipeProvider.createKey("tools/saw"));
	}

	@Override
	public boolean canPerformAction (@NonNull ItemInstance stack, @NonNull ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
	}

	public @NonNull InteractionResult useOn (UseOnContext context) {
		var level = context.getLevel();
		var pos = context.getClickedPos();
		var player = context.getPlayer();

		var newBlock = this.evaluateNewBlockState(level, pos, player, level.getBlockState(pos), context);
		if (newBlock.isEmpty()) {
			return InteractionResult.PASS;
		} else {
			var itemInHand = context.getItemInHand();
			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, itemInHand);
			}

			level.setBlock(pos, newBlock.get(), 11);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlock.get()));
			if (player != null) {
				itemInHand.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
			}
			dropSawdust(level, pos, 1.f);
			return InteractionResult.SUCCESS;
		}
	}

	private Optional<BlockState> evaluateNewBlockState (Level level, BlockPos pos, @Nullable Player player, BlockState oldState, UseOnContext context) {
		var strippedBlock = Optional.ofNullable(oldState.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false));
		if (strippedBlock.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			return strippedBlock;
		} else {
			var scrapedBlock = Optional.ofNullable(oldState.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false));
			if (scrapedBlock.isPresent()) {
				spawnSoundAndParticle(level, pos, player, oldState, SoundEvents.AXE_SCRAPE, 3005);
				return scrapedBlock;
			} else {
				var waxoffBlock = Optional.ofNullable(oldState.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false));
				if (waxoffBlock.isPresent()) {
					spawnSoundAndParticle(level, pos, player, oldState, SoundEvents.AXE_WAX_OFF, 3004);
					return waxoffBlock;
				} else {
					return Optional.empty();
				}
			}
		}
	}

	private static void spawnSoundAndParticle (Level level, BlockPos pos, @Nullable Player player, BlockState oldState, SoundEvent soundEvent, int particle) {
		level.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.levelEvent(player, particle, pos, 0);
		if (oldState.getBlock() instanceof ChestBlock && oldState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
			var neighborPos = ChestBlock.getConnectedBlockPos(pos, oldState);
			level.gameEvent(GameEvent.BLOCK_CHANGE, neighborPos, GameEvent.Context.of(player, level.getBlockState(neighborPos)));
			level.levelEvent(player, particle, neighborPos, 0);
		}

	}

	/**
	 * Drops sawdust at the specified block position in the world when certain conditions are met.
	 * The method only executes on the server side and has a random chance to spawn sawdust items.
	 *
	 * @param level The game world where the sawdust should be dropped.
	 * @param pos   The position of the block where the sawdust is dropped.
	 */
	private void dropSawdust (Level level, BlockPos pos, float chance) {
		if (level.isClientSide()) return;

		if (level.getRandom().nextFloat() < chance) {
			Block.popResource(level, pos, new ItemStack(GenItems.SAWDUST.get(), level.getRandom().nextInt(1, 4)));
		}
	}

	/**
	 * Drops rubber resin at the specified block position in the world if certain conditions are met.
	 * The method does not execute on the client side and has a random chance to spawn rubber resin items.
	 *
	 * @param level The game world where the rubber resin should be dropped.
	 * @param pos   The position of the block where the rubber resin is dropped.
	 */
	private void dropRubberResin (Level level, BlockPos pos) {
		if (level.isClientSide()) return;

		if (level.getRandom().nextFloat() < 0.5f) {
			Block.popResource(level, pos, new ItemStack(GenItems.TREE_SAP.get(), level.getRandom().nextInt(1, 3)));
		}
	}

}
