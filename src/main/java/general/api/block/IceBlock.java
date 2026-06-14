package general.api.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class IceBlock extends BaseBlock {

	public static final MapCodec<IceBlock> CODEC = simpleCodec(IceBlock::new);

	public @NotNull MapCodec<? extends IceBlock> codec () {
		return CODEC;
	}

	public IceBlock (Properties properties) {
		super(properties.pushReaction(PushReaction.DESTROY).isValidSpawn((_, _, _, _) -> false).explosionResistance(0));
	}

	public IceBlock () {
		this(Blocks.ICE.properties());
	}

	public boolean canMelt () {
		return true;
	}

	public static BlockState meltsInto () {
		return Blocks.WATER.defaultBlockState();
	}

	@Override
	public void playerDestroy (@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack stack) {
		super.playerDestroy(level, player, pos, state, te, stack);
		if (!EnchantmentHelper.hasTag(stack, EnchantmentTags.PREVENTS_ICE_MELTING)) {
			if (level.getBiome(pos).is(Tags.Biomes.IS_HOT)) {
				level.removeBlock(pos, false);
				return;
			}
			level.setBlockAndUpdate(pos, meltsInto());
		}
	}

	@Override
	public void onDestroyedByPushReaction (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Direction pushDirection, @NotNull FluidState fluid) {
		super.onDestroyedByPushReaction(state, level, pos, pushDirection, fluid);
		if (level.getBiome(pos).is(Tags.Biomes.IS_HOT)) {
			level.removeBlock(pos, false);
			return;
		}
		level.setBlockAndUpdate(pos, meltsInto());
	}

	@Override
	public @Nullable PushReaction getPistonPushReaction (@NotNull BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	protected void randomTick (@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		if (!canMelt()) return;

		if (level.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightEmission(level, pos)) {
			this.melt(level, pos);
		}
	}

	@Override
	protected boolean skipRendering (@NotNull BlockState state, BlockState adjacentBlockState, @NotNull Direction side) {
		return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
	}

	protected void melt (Level level, BlockPos pos) {
		if (level.getBiome(pos).is(Tags.Biomes.IS_HOT)) {
			level.removeBlock(pos, false);
		} else {
			level.setBlockAndUpdate(pos, meltsInto());
			level.neighborChanged(pos, meltsInto().getBlock(), null);
		}

	}
}
