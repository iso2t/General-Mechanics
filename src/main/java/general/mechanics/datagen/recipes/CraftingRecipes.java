package general.mechanics.datagen.recipes;

import general.mechanics.api.item.element.metallic.ElementItem;
import general.mechanics.api.item.tools.ToolItem;
import general.mechanics.api.tags.CoreTags;
import general.mechanics.registries.CoreBlocks;
import general.mechanics.registries.CoreElements;
import general.mechanics.registries.CoreItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags;

import static general.mechanics.registries.CoreBlocks.*;
import static general.mechanics.registries.CoreItems.*;
import static net.minecraft.world.item.Items.CRAFTER;
import static net.minecraft.world.item.Items.REDSTONE;

public class CraftingRecipes extends CoreRecipeProvider {

    public CraftingRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        block(consumer);
        machine(consumer);
        color(consumer);
        tools(consumer);
        misc(consumer);

        for (var item : CoreElements.getElements()) {
            if (item.get() instanceof ElementItem element) {
                element.getRecipes(items, consumer, has(element::getDustItem));
            }
        }
    }

    private void block(RecipeOutput consumer) {

        shaped(RecipeCategory.MISC, POLYETHYLENE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYETHYLENE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYETHYLENE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pe_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYPROPYLENE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYPROPYLENE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYPROPYLENE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pp_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYSTYRENE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYSTYRENE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYSTYRENE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/ps_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYVINYL_CHLORIDE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYVINYL_CHLORIDE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYVINYL_CHLORIDE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pvc_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYETHYLENE_TEREPHTHALATE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYETHYLENE_TEREPHTHALATE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYETHYLENE_TEREPHTHALATE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pet_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, ACRYLONITRILE_BUTADIENE_STYRENE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.ACRYLONITRILE_BUTADIENE_STYRENE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.ACRYLONITRILE_BUTADIENE_STYRENE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/abs_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYCARBONATE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYCARBONATE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYCARBONATE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pc_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, NYLON_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.NYLON)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.NYLON))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pa_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYURETHANE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYURETHANE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYURETHANE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/pu_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYTETRAFLUOROETHYLENE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYTETRAFLUOROETHYLENE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYTETRAFLUOROETHYLENE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/ptfe_machine_frame_from_plastic"));

        shaped(RecipeCategory.MISC, POLYETHERETHERKETONE_MACHINE_FRAME, 1)
                .pattern("IPI")
                .pattern("PRP")
                .pattern("IPI")
                .define('P', CoreTags.Items.POLYETHERETHERKETONE)
                .define('I', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('R', REDSTONE)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_plastic", has(CoreTags.Items.POLYETHERETHERKETONE))
                .unlockedBy("has_redstone", has(REDSTONE))
                .save(consumer, createKey("crafting/peek_machine_frame_from_plastic"));

    }

    protected void machine(RecipeOutput consumer) {
        shaped(RecipeCategory.MISC, MATTER_FABRICATOR, 1)
                .pattern("PPP")
                .pattern("VUV")
                .pattern("VFV")
                .define('P', CoreTags.Items.PLASTIC_BLOCKS)
                .define('V', CoreElements.VANADIUM_INGOT)
                .define('F', POLYETHYLENE_MACHINE_FRAME)
                .define('U', CRAFTER)
                .unlockedBy("has_vanadium_ingot", has(CoreElements.VANADIUM_INGOT))
                .unlockedBy("has_plastic", has(CoreTags.Items.PLASTIC_BLOCKS))
                .unlockedBy("has_crafter", has(CRAFTER))
                .unlockedBy("has_machine_frame", has(POLYETHYLENE_MACHINE_FRAME))
                .save(consumer, createKey("crafting/matter_fabricator_from_plastic"));

        shaped(RecipeCategory.MISC, INDUSTRIAL_HEATING_ELEMENT, 1)
                .pattern("BSB")
                .pattern("SFS")
                .pattern("BSB")
                .define('S', CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem())
                .define('F', POLYETHYLENE_MACHINE_FRAME)
                .define('B', CoreTags.Items.BOLTS)
                .unlockedBy("has_stainless_steel_plate", has(CoreElements.STAINLESS_STEEL_INGOT.get().getPlateItem()))
                .unlockedBy("has_lava_bucket", has(Tags.Items.BUCKETS_LAVA))
                .save(consumer, createKey("crafting/industrial_heating_element_from_stainless_steel_plate"));
    }

    protected void color(RecipeOutput consumer) {
        // Lazily creating all recipes for raw plastic item dyes.
        /*for (var rawPlastic : CoreItems.getAllColoredRawPlastics()) {
            RawPlasticItem.getRecipeFrom(rawPlastic, consumer, has(CoreTags.Items.RAW_PLASTIC));
        }*/

        // Lazily creating all recipes for plastic blocks.
        var coloredPlasticBlocks = CoreBlocks.getAllColoredPlasticBlocks();
        var coloredPlastics = CoreItems.getAllColoredPlastics();
        for (int i = 0; i < coloredPlasticBlocks.size() && i < coloredPlastics.size(); i++) {
            var plasticBlock = coloredPlasticBlocks.get(i);
            var plastic = coloredPlastics.get(i);
            // TODO: Implement recipe generation for new plastic block system
            // This will need to be updated to work with the new ColoredPlasticBlock system
        }
    }

    protected void tools(RecipeOutput consumer) {
        for (var item : CoreItems.getItems()) {
            if (item.get() instanceof ToolItem tool) {
                tool.registerCraftingRecipe(items, consumer, has(Tags.Items.INGOTS));
            }
        }
    }

    protected void misc(RecipeOutput consumer) {
        shapeless(RecipeCategory.MISC, CoreElements.STAINLESS_STEEL_INGOT.get().getDustItem(), 1)
                .requires(CoreElements.IRON_INGOT.get().getDustItem())
                .requires(CoreElements.CHROMIUM_INGOT.get().getDustItem())
                .requires(CoreElements.NICKEL_INGOT.get().getDustItem())
                .unlockedBy("has_iron_dust", has(CoreElements.IRON_INGOT.get().getDustItem()))
                .unlockedBy("has_chromium_dust", has(CoreElements.CHROMIUM_INGOT.get().getDustItem()))
                .unlockedBy("has_nickel_dust", has(CoreElements.NICKEL_INGOT.get().getDustItem()))
                .save(consumer, createKey("crafting/stainless_steel_dust_from_dusts"));

        shapeless(RecipeCategory.MISC, SCREW, 2)
                .requires(CoreTags.Items.SAWS)
                .requires(CoreTags.Items.FILES)
                .requires(CoreElements.STAINLESS_STEEL_INGOT.get().getRodItem())
                .unlockedBy("has_saw", has(CoreTags.Items.SAWS))
                .unlockedBy("has_file", has(CoreTags.Items.FILES))
                .unlockedBy("has_stainless_steel_rod", has(CoreElements.STAINLESS_STEEL_INGOT.get().getRodItem()))
                .save(consumer, createKey("crafting/screws"));

        shapeless(RecipeCategory.MISC, BOLT, 1)
                .requires(CoreTags.Items.SCREWS)
                .requires(CoreTags.Items.FILES)
                .unlockedBy("has_screw", has(SCREW))
                .unlockedBy("has_file", has(CoreTags.Items.FILES))
                .save(consumer, createKey("crafting/bolts"));

        shapeless(RecipeCategory.MISC, CARBON_DUST, 2)
                .requires(CoreTags.Items.CARBON)
                .requires(CoreTags.Items.HAMMERS)
                .unlockedBy("has_carbon", has(CoreTags.Items.CARBON))
                .unlockedBy("has_hammer", has(CoreTags.Items.HAMMERS))
                .save(consumer, createKey("crafting/carbon_dust"));

        shapeless(RecipeCategory.MISC, CoreElements.STEEL_INGOT.get().getDustItem(), 1)
                .requires(CoreElements.IRON_INGOT.get().getDustItem())
                .requires(CARBON_DUST)
                .unlockedBy("has_iron_dust", has(CoreElements.IRON_INGOT.get().getDustItem()))
                .unlockedBy("has_carbon_dust", has(CARBON_DUST))
                .save(consumer, createKey("crafting/steel_dust_from_dusts"));
    }
}
