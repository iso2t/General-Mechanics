package general.api.machine.upgrade;

import general.api.machine.MachineDefinition;
import general.api.multiblock.MultiblockInstance;
import net.minecraft.world.level.Level;

import java.util.Objects;

/**
 * Common multiblock-to-machine-upgrade resolution strategies.
 */
public final class MachineUpgradeResolvers {

	private MachineUpgradeResolvers () {
	}

	/**
	 * Resolves the provider installed at the first occurrence of a pattern symbol.
	 * Pair this with a uniform pattern symbol when every core position must use the
	 * same tier.
	 */
	public static MachineDefinition.UpgradeResolver firstProviderAt (char symbol) {
		return (level, instance) -> resolveFirstProviderAt(level, instance, symbol);
	}

	public static MachineUpgradeProfile resolveFirstProviderAt (Level level, MultiblockInstance instance, char symbol) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(instance, "instance");
		var positions = instance.definition().get().pattern().getWorldPositions(symbol, instance.anchor(), instance.facing());
		if (positions.isEmpty()) return MachineUpgradeProfile.identity();
		var block = level.getBlockState(positions.getFirst()).getBlock();
		return block instanceof MachineUpgradeProvider provider ? Objects.requireNonNull(provider.getUpgradeProfile(), "Machine upgrade provider returned null") : MachineUpgradeProfile.identity();
	}
}
