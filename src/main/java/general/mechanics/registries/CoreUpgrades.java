package general.mechanics.registries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import general.mechanics.GM;
import general.mechanics.api.item.ItemDefinition;
import general.mechanics.api.upgrade.UpgradeBase;
import general.mechanics.api.upgrade.item.SpeedUpgrade;
import general.mechanics.api.upgrade.item.UpgradeEmpty;
import general.mechanics.tab.CoreTab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CoreUpgrades {

    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(GM.MODID);

    private static final List<ItemDefinition<?>> UPGRADES = new ArrayList<>();
    private static final List<Pair<ItemDefinition<?>, UpgradeFunction<?, ?>>> FUNCTIONS = new ArrayList<>();

    public static final ItemDefinition<UpgradeBase> EMPTY = create("Upgrade Base", "", UpgradeEmpty::new, null);
    public static final ItemDefinition<UpgradeBase> SPEED = create("Speed Upgrade", "Increases machine operation speed.", UpgradeBase::new, SpeedUpgrade::new);
    public static final ItemDefinition<UpgradeBase> CAPACITY = create("Capacity Upgrade", "Expands the operational size of machines.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> EFFICIENCY = create("Efficiency Upgrade", "Reduces fePerTick consumption per operation.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> OVERCLOCK = create("Overclock Upgrade", "Greatly increases speed but at a fePerTick efficiency cost.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> THERMAL_BUFFER = create("Thermal Buffer", "Allows the machine to operate under extreme conditions.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> AUTO_EJECTOR = create("Auto Ejector", "Automatically pushes output to connected inventories.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> INPUT_EXPANDER = create("Input Expander", "Allows the machine to accept input from multiple sides.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> MULTI_PROCESSOR_UNIT = create("Multi-Processor Unit", "Enables running multiple operations at once.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> SILENCING_COIL = create("Silencing Coil", "Suppresses sounds emitted from the machine.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> NANITE_INJECTOR = create("Nanite Injector", "Increases yield of byproducts or rare drops.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> PRECISION_GEARBOX = create("Precision Gearbox", "Increases accuracy for machines with chance-based outputs.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> REDSTONE_INTERFACE = create("Redstone Interface", "Adds advanced redstone control options.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> ECO_DRIVE = create("Eco-Drive", "Idle machines draw near-zero fePerTick.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> VOID_MOD = create("Void Mod", "Destroys overflow items instead of clogging the machine.", UpgradeBase::new, null);
    public static final ItemDefinition<UpgradeBase> REPLICATION_NODE = create("Replication Node", "Duplicates output at a high fePerTick cost.", UpgradeBase::new, null);

    public static List<ItemDefinition<?>> getUpgrades () {
        return Collections.unmodifiableList(UPGRADES);
    }

    public static List<Pair<ItemDefinition<?>, UpgradeFunction<?, ?>>> getFunctions () {
        return Collections.unmodifiableList(FUNCTIONS);
    }

    /**
     * Returns a list of registered upgrades for a given block.
     * @param block the block to check. This should be the main block and linked to a {@link net.minecraft.world.level.block.entity.BlockEntity}
     * @return an {@link ImmutableList} of upgrade pairs.
     */
    public static ImmutableList<Pair<ItemDefinition<UpgradeBase>, Integer>> getCompatibleUpgrades (Block block) {
        var id = BuiltInRegistries.BLOCK.getKey(block);

        var upgradeMap = CoreRegistries.UPGRADE_MAP_REGISTRY.get(id);
        return upgradeMap != null && !upgradeMap.upgrades().isEmpty() ? upgradeMap.upgrades() : ImmutableList.of();
    }

    public static <T extends UpgradeBase, I> ItemDefinition<T> create (String id, String desc, BiFunction<Item.Properties, String, T> factory, @Nullable UpgradeFunctionBuilder<T, I> caller) {
        String resourceFriendly = id.toLowerCase().replace(' ', '_');
        return create(id, desc, GM.getResource(resourceFriendly), factory, caller);
    }

    // Overloaded method for standard Function (backward compatibility)
    public static <T extends UpgradeBase, I> ItemDefinition<T> create (String id, String desc, Function<Item.Properties, T> factory, @Nullable UpgradeFunctionBuilder<T, I> caller) {
        String resourceFriendly = id.toLowerCase().replace(' ', '_');
        return create(id, desc, GM.getResource(resourceFriendly), factory, caller);
    }

    public static <T extends UpgradeBase, I> ItemDefinition<T> create (String name, String desc, Identifier id, BiFunction<Item.Properties, String, T> factory, @Nullable UpgradeFunctionBuilder<T, I> caller) {
        // Convert BiFunction to Function by providing the description as the second parameter
        Function<Item.Properties, T> registryFactory = properties -> factory.apply(properties, desc);
        
        var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), registryFactory));
        CoreTab.add(definition);

        UPGRADES.add(definition);
        if (caller != null) FUNCTIONS.add(new Pair<>(definition, caller.build()));
        return definition;
    }

    // Overloaded method for standard Function (backward compatibility)
    public static <T extends UpgradeBase, I> ItemDefinition<T> create (String name, String desc, Identifier id, Function<Item.Properties, T> factory, @Nullable UpgradeFunctionBuilder<T, I> caller) {
        var definition = new ItemDefinition<>(name, REGISTRY.registerItem(id.getPath(), factory));
        CoreTab.add(definition);

        UPGRADES.add(definition);
        if (caller != null) FUNCTIONS.add(new Pair<>(definition, caller.build()));
        return definition;
    }

    @FunctionalInterface
    public interface UpgradeFunctionBuilder<T extends UpgradeBase, I> {
        UpgradeFunction<T, I> build ();
    }

    public interface UpgradeFunction<T extends UpgradeBase, I> {
        void create (T base, I valueType);
        void work (I attribute);
    }

}
