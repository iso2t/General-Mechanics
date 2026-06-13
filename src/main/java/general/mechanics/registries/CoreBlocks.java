package general.mechanics.registries;

import general.mechanics.GM;
import general.mechanics.api.block.BlockDefinition;
import general.mechanics.api.block.base.BaseBlock;
import general.mechanics.api.block.base.OreBlock;
import general.mechanics.api.block.ice.Ice7Block;
import general.mechanics.api.block.ice.IceBlock;
import general.mechanics.api.block.machine.MachineFrameBlock;
import general.mechanics.api.block.plastic.ColoredPlasticBlock;
import general.mechanics.api.block.plastic.PlasticTypeBlock;
import general.mechanics.api.item.ItemDefinition;
import general.mechanics.api.item.base.BaseBlockItem;
import general.mechanics.api.item.element.ElementType;
import general.mechanics.api.item.plastic.PlasticType;
import general.mechanics.block.machine.HeatingElementBlock;
import general.mechanics.block.machine.MatterFabricatorBlock;
import general.mechanics.tab.CoreTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Block definitions are blocks that are shared across all child mods.
 * If your block is specific to a child mod, it should be registered in that mod's block registry.
 */
@SuppressWarnings("unused")
public class CoreBlocks {

    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(GM.MODID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    //################################
    //||            Ores            ||
    //################################
    public static final BlockDefinition<OreBlock> LITHIUM_ORE_BLOCK = registerOre(ElementType.LITHIUM);
    public static final BlockDefinition<OreBlock> BERYLLIUM_ORE_BLOCK = registerOre(ElementType.BERYLLIUM);
    public static final BlockDefinition<OreBlock> BORON_ORE_BLOCK = registerOre(ElementType.BORON);

    public static final BlockDefinition<OreBlock> SODIUM_ORE_BLOCK = registerOre(ElementType.SODIUM);
    public static final BlockDefinition<OreBlock> MAGNESIUM_ORE_BLOCK = registerOre(ElementType.MAGNESIUM);
    public static final BlockDefinition<OreBlock> ALUMINUM_ORE_BLOCK = registerOre(ElementType.ALUMINUM);
    public static final BlockDefinition<OreBlock> SILICON_ORE_BLOCK = registerOre(ElementType.SILICON);
    public static final BlockDefinition<OreBlock> PHOSPHORUS_ORE_BLOCK = registerOre(ElementType.PHOSPHORUS);
    public static final BlockDefinition<OreBlock> SULFUR_ORE_BLOCK = registerOre(ElementType.SULFUR);

    public static final BlockDefinition<OreBlock> POTASSIUM_ORE_BLOCK = registerOre(ElementType.POTASSIUM);
    public static final BlockDefinition<OreBlock> CALCIUM_ORE_BLOCK = registerOre(ElementType.CALCIUM);
    public static final BlockDefinition<OreBlock> SCANDIUM_ORE_BLOCK = registerOre(ElementType.SCANDIUM);
    public static final BlockDefinition<OreBlock> TITANIUM_ORE_BLOCK = registerOre(ElementType.TITANIUM);
    public static final BlockDefinition<OreBlock> VANADIUM_ORE_BLOCK = registerOre(ElementType.VANADIUM);
    public static final BlockDefinition<OreBlock> CHROMIUM_ORE_BLOCK = registerOre(ElementType.CHROMIUM);
    public static final BlockDefinition<OreBlock> MANGANESE_ORE_BLOCK = registerOre(ElementType.MANGANESE);
    public static final BlockDefinition<OreBlock> IRON_ORE_BLOCK = registerOre(ElementType.IRON);
    public static final BlockDefinition<OreBlock> COBALT_ORE_BLOCK = registerOre(ElementType.COBALT);
    public static final BlockDefinition<OreBlock> NICKEL_ORE_BLOCK = registerOre(ElementType.NICKEL);
    public static final BlockDefinition<OreBlock> COPPER_ORE_BLOCK = registerOre(ElementType.COPPER);
    public static final BlockDefinition<OreBlock> ZINC_ORE_BLOCK = registerOre(ElementType.ZINC);
    public static final BlockDefinition<OreBlock> GALLIUM_ORE_BLOCK = registerOre(ElementType.GALLIUM);
    public static final BlockDefinition<OreBlock> GERMANIUM_ORE_BLOCK = registerOre(ElementType.GERMANIUM);
    public static final BlockDefinition<OreBlock> ARSENIC_ORE_BLOCK = registerOre(ElementType.ARSENIC);
    public static final BlockDefinition<OreBlock> SELENIUM_ORE_BLOCK = registerOre(ElementType.SELENIUM);
    public static final BlockDefinition<OreBlock> BROMINE_ORE_BLOCK = registerOre(ElementType.BROMINE);

    public static final BlockDefinition<OreBlock> RUBIDIUM_ORE_BLOCK = registerOre(ElementType.RUBIDIUM);
    public static final BlockDefinition<OreBlock> STRONTIUM_ORE_BLOCK = registerOre(ElementType.STRONTIUM);
    public static final BlockDefinition<OreBlock> YTTRIUM_ORE_BLOCK = registerOre(ElementType.YTTRIUM);
    public static final BlockDefinition<OreBlock> ZIRCONIUM_ORE_BLOCK = registerOre(ElementType.ZIRCONIUM);
    public static final BlockDefinition<OreBlock> NIOBIUM_ORE_BLOCK = registerOre(ElementType.NIOBIUM);
    public static final BlockDefinition<OreBlock> MOLYBDENUM_ORE_BLOCK = registerOre(ElementType.MOLYBDENUM);
    public static final BlockDefinition<OreBlock> RUTHENIUM_ORE_BLOCK = registerOre(ElementType.RUTHENIUM);
    public static final BlockDefinition<OreBlock> RHODIUM_ORE_BLOCK = registerOre(ElementType.RHODIUM);
    public static final BlockDefinition<OreBlock> PALLADIUM_ORE_BLOCK = registerOre(ElementType.PALLADIUM);
    public static final BlockDefinition<OreBlock> SILVER_ORE_BLOCK = registerOre(ElementType.SILVER);
    public static final BlockDefinition<OreBlock> CADMIUM_ORE_BLOCK = registerOre(ElementType.CADMIUM);
    public static final BlockDefinition<OreBlock> INDIUM_ORE_BLOCK = registerOre(ElementType.INDIUM);
    public static final BlockDefinition<OreBlock> TIN_ORE_BLOCK = registerOre(ElementType.TIN);
    public static final BlockDefinition<OreBlock> ANTIMONY_ORE_BLOCK = registerOre(ElementType.ANTIMONY);
    public static final BlockDefinition<OreBlock> TELLURIUM_ORE_BLOCK = registerOre(ElementType.TELLURIUM);
    public static final BlockDefinition<OreBlock> IODINE_ORE_BLOCK = registerOre(ElementType.IODINE);

    public static final BlockDefinition<OreBlock> CESIUM_ORE_BLOCK = registerOre(ElementType.CESIUM);
    public static final BlockDefinition<OreBlock> BARIUM_ORE_BLOCK = registerOre(ElementType.BARIUM);
    public static final BlockDefinition<OreBlock> LANTHANUM_ORE_BLOCK = registerOre(ElementType.LANTHANUM);
    public static final BlockDefinition<OreBlock> CERIUM_ORE_BLOCK = registerOre(ElementType.CERIUM);
    public static final BlockDefinition<OreBlock> PRASEODYMIUM_ORE_BLOCK = registerOre(ElementType.PRASEODYMIUM);
    public static final BlockDefinition<OreBlock> NEODYMIUM_ORE_BLOCK = registerOre(ElementType.NEODYMIUM);
    public static final BlockDefinition<OreBlock> SAMARIUM_ORE_BLOCK = registerOre(ElementType.SAMARIUM);
    public static final BlockDefinition<OreBlock> EUROPIUM_ORE_BLOCK = registerOre(ElementType.EUROPIUM);
    public static final BlockDefinition<OreBlock> GADOLINIUM_ORE_BLOCK = registerOre(ElementType.GADOLINIUM);
    public static final BlockDefinition<OreBlock> TERBIUM_ORE_BLOCK = registerOre(ElementType.TERBIUM);
    public static final BlockDefinition<OreBlock> DYSPROSIUM_ORE_BLOCK = registerOre(ElementType.DYSPROSIUM);
    public static final BlockDefinition<OreBlock> HOLMIUM_ORE_BLOCK = registerOre(ElementType.HOLMIUM);
    public static final BlockDefinition<OreBlock> ERBIUM_ORE_BLOCK = registerOre(ElementType.ERBIUM);
    public static final BlockDefinition<OreBlock> THULIUM_ORE_BLOCK = registerOre(ElementType.THULIUM);
    public static final BlockDefinition<OreBlock> YTTERBIUM_ORE_BLOCK = registerOre(ElementType.YTTERBIUM);
    public static final BlockDefinition<OreBlock> LUTETIUM_ORE_BLOCK = registerOre(ElementType.LUTETIUM);
    public static final BlockDefinition<OreBlock> HAFNIUM_ORE_BLOCK = registerOre(ElementType.HAFNIUM);
    public static final BlockDefinition<OreBlock> TANTALUM_ORE_BLOCK = registerOre(ElementType.TANTALUM);
    public static final BlockDefinition<OreBlock> TUNGSTEN_ORE_BLOCK = registerOre(ElementType.TUNGSTEN);
    public static final BlockDefinition<OreBlock> RHENIUM_ORE_BLOCK = registerOre(ElementType.RHENIUM);
    public static final BlockDefinition<OreBlock> OSMIUM_ORE_BLOCK = registerOre(ElementType.OSMIUM);
    public static final BlockDefinition<OreBlock> IRIDIUM_ORE_BLOCK = registerOre(ElementType.IRIDIUM);
    public static final BlockDefinition<OreBlock> PLATINUM_ORE_BLOCK = registerOre(ElementType.PLATINUM);
    public static final BlockDefinition<OreBlock> GOLD_ORE_BLOCK = registerOre(ElementType.GOLD);
    public static final BlockDefinition<OreBlock> MERCURY_ORE_BLOCK = registerOre(ElementType.MERCURY);
    public static final BlockDefinition<OreBlock> THALLIUM_ORE_BLOCK = registerOre(ElementType.THALLIUM);
    public static final BlockDefinition<OreBlock> LEAD_ORE_BLOCK = registerOre(ElementType.LEAD);
    public static final BlockDefinition<OreBlock> BISMUTH_ORE_BLOCK = registerOre(ElementType.BISMUTH);
    public static final BlockDefinition<OreBlock> POLONIUM_ORE_BLOCK = registerOre(ElementType.POLONIUM);
    public static final BlockDefinition<OreBlock> ASTATINE_ORE_BLOCK = registerOre(ElementType.ASTATINE);

    public static final BlockDefinition<OreBlock> FRANCIUM_ORE_BLOCK = registerOre(ElementType.FRANCIUM);
    public static final BlockDefinition<OreBlock> RADIUM_ORE_BLOCK = registerOre(ElementType.RADIUM);
    public static final BlockDefinition<OreBlock> ACTINIUM_ORE_BLOCK = registerOre(ElementType.ACTINIUM);
    public static final BlockDefinition<OreBlock> THORIUM_ORE_BLOCK = registerOre(ElementType.THORIUM);
    public static final BlockDefinition<OreBlock> PROTACTINIUM_ORE_BLOCK = registerOre(ElementType.PROTACTINIUM);
    public static final BlockDefinition<OreBlock> URANIUM_ORE_BLOCK = registerOre(ElementType.URANIUM);

    // Plastic Blocks
    public static final BlockDefinition<PlasticTypeBlock> POLYETHYLENE_BLOCK = plasticTypeBlock("Polyethylene Block", PlasticType.POLYETHYLENE);
    public static final BlockDefinition<PlasticTypeBlock> POLYPROPYLENE_BLOCK = plasticTypeBlock("Polypropylene Block", PlasticType.POLYPROPYLENE);
    public static final BlockDefinition<PlasticTypeBlock> POLYSTYRENE_BLOCK = plasticTypeBlock("Polystyrene Block", PlasticType.POLYSTYRENE);
    public static final BlockDefinition<PlasticTypeBlock> POLYVINYL_CHLORIDE_BLOCK = plasticTypeBlock("Polyvinyl Chloride Block", PlasticType.POLYVINYL_CHLORIDE);
    public static final BlockDefinition<PlasticTypeBlock> POLYETHYLENE_TEREPHTHALATE_BLOCK = plasticTypeBlock("Polyethylene Terephthalate Block", PlasticType.POLYETHYLENE_TEREPHTHALATE);
    public static final BlockDefinition<PlasticTypeBlock> ACRYLONITRILE_BUTADIENE_STYRENE_BLOCK = plasticTypeBlock("Acrylonitrile Butadiene Styrene Block", PlasticType.ACRYLONITRILE_BUTADIENE_STYRENE);
    public static final BlockDefinition<PlasticTypeBlock> POLYCARBONATE_BLOCK = plasticTypeBlock("Polycarbonate Block", PlasticType.POLYCARBONATE);
    public static final BlockDefinition<PlasticTypeBlock> NYLON_BLOCK = plasticTypeBlock("Nylon Block", PlasticType.NYLON);
    public static final BlockDefinition<PlasticTypeBlock> POLYURETHANE_BLOCK = plasticTypeBlock("Polyurethane Block", PlasticType.POLYURETHANE);
    public static final BlockDefinition<PlasticTypeBlock> POLYTETRAFLUOROETHYLENE_BLOCK = plasticTypeBlock("Polytetrafluoroethylene Block", PlasticType.POLYTETRAFLUOROETHYLENE);
    public static final BlockDefinition<PlasticTypeBlock> POLYETHERETHERKETONE_BLOCK = plasticTypeBlock("Polyetheretherketone Block", PlasticType.POLYETHERETHERKETONE);

    // Ice
    public static final BlockDefinition<IceBlock> ICE2 = register("Ice II", "ice_2", IceBlock::new);
    public static final BlockDefinition<IceBlock> ICE3 = register("Ice III", "ice_3", IceBlock::new);
    public static final BlockDefinition<IceBlock> ICE4 = register("Ice IV", "ice_4", IceBlock::new);
    public static final BlockDefinition<IceBlock> ICE5 = register("Ice V", "ice_5", IceBlock::new);
    public static final BlockDefinition<IceBlock> ICE6 = register("Ice VI", "ice_6", IceBlock::new);
    public static final BlockDefinition<Ice7Block> ICE7 = register("Ice VII", "ice_7", () -> new Ice7Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ICE)));

    // Crafting Machines
    public static final BlockDefinition<MatterFabricatorBlock> MATTER_FABRICATOR = register("Matter Fabricator", MatterFabricatorBlock::new);

    // Machine block
    public static final BlockDefinition<MachineFrameBlock> POLYETHYLENE_MACHINE_FRAME = register("Polyethylene Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYETHYLENE));
    public static final BlockDefinition<MachineFrameBlock> POLYPROPYLENE_MACHINE_FRAME = register("Polypropylene Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYPROPYLENE));
    public static final BlockDefinition<MachineFrameBlock> POLYSTYRENE_MACHINE_FRAME = register("Polystyrene Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYSTYRENE));
    public static final BlockDefinition<MachineFrameBlock> POLYVINYL_CHLORIDE_MACHINE_FRAME = register("Polyvinyl Chloride Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYVINYL_CHLORIDE));
    public static final BlockDefinition<MachineFrameBlock> POLYETHYLENE_TEREPHTHALATE_MACHINE_FRAME = register("Polyethylene Terephthalate Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYETHYLENE_TEREPHTHALATE));
    public static final BlockDefinition<MachineFrameBlock> ACRYLONITRILE_BUTADIENE_STYRENE_MACHINE_FRAME = register("Acrylonitrile Butadiene Styrene Machine Frame", () -> new MachineFrameBlock(PlasticType.ACRYLONITRILE_BUTADIENE_STYRENE));
    public static final BlockDefinition<MachineFrameBlock> POLYCARBONATE_MACHINE_FRAME = register("Polycarbonate Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYCARBONATE));
    public static final BlockDefinition<MachineFrameBlock> NYLON_MACHINE_FRAME = register("Nylon Machine Frame", () -> new MachineFrameBlock(PlasticType.NYLON));
    public static final BlockDefinition<MachineFrameBlock> POLYURETHANE_MACHINE_FRAME = register("Polyurethane Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYURETHANE));
    public static final BlockDefinition<MachineFrameBlock> POLYTETRAFLUOROETHYLENE_MACHINE_FRAME = register("Polytetrafluoroethylene Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYTETRAFLUOROETHYLENE));
    public static final BlockDefinition<MachineFrameBlock> POLYETHERETHERKETONE_MACHINE_FRAME = register("Polyetheretherketone Machine Frame", () -> new MachineFrameBlock(PlasticType.POLYETHERETHERKETONE));

    // Misc
    public static final BlockDefinition<HeatingElementBlock> INDUSTRIAL_HEATING_ELEMENT = register("Industrial Heating Element", HeatingElementBlock::new);

    private static String formatColorName(String colorName) {
        String[] words = colorName.split("_");
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                formatted.append(" ");
            }
            formatted.append(words[i].substring(0, 1).toUpperCase())
                    .append(words[i].substring(1));
        }
        return formatted.toString();
    }

    @SuppressWarnings("unchecked")
    static <T extends PlasticTypeBlock> BlockDefinition<T> plasticTypeBlock(String name, PlasticType plasticType) {
        // Create the main plastic type block
        BlockDefinition<T> plasticTypeDef = register(name, () -> (T) new PlasticTypeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), plasticType));
        
        // Register all colored plastic block variants
        for (DyeColor color : PlasticType.getAllColors()) {
            String coloredName = formatColorName(color.getName()) + " " + name;
            String coloredResourceName = color.getName().toLowerCase() + "_" + name.toLowerCase().replace(' ', '_');
            register(coloredName, GM.getResource(coloredResourceName), () -> {
                T plasticTypeBlock = plasticTypeDef.block();
                return (Block) plasticTypeBlock.getColoredVariant(color);
            }, null, true);
        }
        
        return plasticTypeDef;
    }

    /**
     * Get all colored plastic blocks for a specific plastic type
     */
    public static List<ColoredPlasticBlock> getColoredPlasticBlocksForType(PlasticType plasticType) {
        List<ColoredPlasticBlock> coloredVariants = new ArrayList<>();
        for (var block : BLOCKS) {
            if (block.block() instanceof ColoredPlasticBlock colored) {
                if (colored.getPlasticType() == plasticType) {
                    coloredVariants.add(colored);
                }
            }
        }
        return coloredVariants;
    }

    public static List<OreBlock> getOreBlocks() {
        List<OreBlock> ores = new ArrayList<>();
        for (var block : BLOCKS) {
            if (block.block() instanceof OreBlock ore) {
                ores.add(ore);
            }
        }
        return ores;
    }

    /**
     * Get all colored plastic blocks
     */
    public static List<ColoredPlasticBlock> getAllColoredPlasticBlocks() {
        List<ColoredPlasticBlock> allColored = new ArrayList<>();
        for (var block : BLOCKS) {
            if (block.block() instanceof ColoredPlasticBlock colored) {
                allColored.add(colored);
            }
        }
        return allColored;
    }

    public static List<PlasticTypeBlock> getAllPlasticTypeBlocks() {
        List<PlasticTypeBlock> allPlastic = new ArrayList<>();
        for (var block : BLOCKS) {
            if (block.block() instanceof PlasticTypeBlock plastic) {
                allPlastic.add(plastic);
            }
        }
        return allPlastic;
    }

    private static BlockDefinition<OreBlock> registerOre(ElementType element) {
        if (element.isAlloy() || !element.isNatural()) {
            throw new IllegalArgumentException("Attempted to register ore for invalid element: " + element.name());
        }
        String name = element.getDisplayName() + " Ore";
        String resourceName = name.toLowerCase().replace(' ', '_');
        BlockDefinition<OreBlock> definition = register(name, GM.getResource(resourceName), () -> new OreBlock(element), null, false);
        CoreTab.addElements(definition.item());
        return definition;
    }

    public static List<BlockDefinition<?>> getBlocks () {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static <T extends Block> BlockDefinition<T> register(final String name, final Supplier<T> supplier) {
        String resourceFriendly = name.toLowerCase().replace(' ', '_');
        return register(name, GM.getResource(resourceFriendly), supplier, null, true);
    }

    public static <T extends Block> BlockDefinition<T> register(final String name, String resourceName, final Supplier<T> supplier) {
        return register(name, GM.getResource(resourceName), supplier, null, true);
    }

    public static <T extends Block> BlockDefinition<T> register(final String name, Identifier id, final Supplier<T> supplier, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory, boolean addToTab) {
        return register(name, id, supplier, itemFactory, addToTab, CoreTab.MAIN);
    }

    public static <T extends Block> BlockDefinition<T> register(final String name, Identifier id, final Supplier<T> supplier, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory, boolean addToTab, @Nullable ResourceKey<CreativeModeTab> group) {
        var deferredBlock = REGISTRY.register(id.getPath(), supplier);
        var deferredItem = CoreItems.REGISTRY.register(id.getPath(), () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
            if (itemFactory != null) {
                var item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalArgumentException("BlockItem factory for " + id + " returned null.");
                }
                return item;
            } else if (block instanceof BaseBlock) {
                return new BaseBlockItem(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });
        var itemDef = new ItemDefinition<>(name, deferredItem);
        if (addToTab) {
            if (Objects.equals(group, CoreTab.MAIN)) {
                CoreTab.add(itemDef);
            } else if (Objects.equals(group, CoreTab.FLUIDS)) {
                CoreTab.addFluids(itemDef);
            } else if (group != null) {
                CoreTab.addExternal(group, itemDef);
            }
        }
        BlockDefinition<T> definition = new BlockDefinition<>(name, deferredBlock, itemDef);
        BLOCKS.add(definition);
        return definition;
    }

}
