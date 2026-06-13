package general.mechanics.datagen.models;

import general.mechanics.GM;
import general.mechanics.api.block.BlockDefinition;
import general.mechanics.api.block.base.DecorativeBlock;
import general.mechanics.api.block.base.OreBlock;
import general.mechanics.api.block.ice.IceBlock;
import general.mechanics.api.block.machine.MachineFrameBlock;
import general.mechanics.api.block.plastic.ColoredPlasticBlock;
import general.mechanics.api.block.plastic.PlasticTypeBlock;
import general.mechanics.block.machine.HeatingElementBlock;
import general.mechanics.registries.CoreBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import com.mojang.math.Quadrant;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.createBooleanModelDispatch;
import static net.minecraft.client.data.models.BlockModelGenerators.createSimpleBlock;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

/**
 * Generates block state definitions, block models and block-item models for all
 * {@link CoreBlocks} blocks using the vanilla data model system introduced in 1.21.4 / NeoForge 26.1.
 * <p>
 * This provider owns everything under {@code blockstates/}, {@code models/block/} and the {@code items/}
 * entries for {@link BlockItem block items}. Non-block item models are handled by {@link ItemModelProvider}.
 * The two providers partition the mod's registry via {@link #getKnownBlocks()} / {@link #getKnownItems()}
 * so neither writes the same file and both satisfy the validation performed by {@code ModelProvider}.
 */
public non-sealed class BlockModelProvider extends ModelProviders {

    public static final Identifier MACHINE_BOTTOM = GM.getResource("block/machine/machine_bottom");
    public static final Identifier MACHINE_TOP = GM.getResource("block/machine/machine_top");
    public static final Identifier MACHINE_SIDE = GM.getResource("block/machine/machine_side");

    // Custom texture slots used by the layered ore models and machine-frame overlay models.
    private static final TextureSlot BASE = TextureSlot.create("base");
    private static final TextureSlot OVERLAY = TextureSlot.create("overlay");

    private BlockModelGenerators generators;

    public BlockModelProvider(PackOutput output) {
        super(output);
    }

    @Override
    public String getName() {
        return "Model Definitions - " + GM.MODID + " (blocks)";
    }

    /** This provider only emits item models for block items; everything else is left to {@link ItemModelProvider}. */
    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> holder.getKey().identifier().getNamespace().equals(GM.MODID))
                .filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerBlockModels(@NotNull BlockModelGenerators blockGenerator, @NotNull ItemModelGenerators itemGenerator) {
        this.generators = blockGenerator;

        for (var block : CoreBlocks.getBlocks()) {
            if (block.block() instanceof DecorativeBlock) {
                blockWithItem(block);
            } else if (block.block() instanceof IceBlock) {
                iceBlockWithItem(block);
            } else if (block.block() instanceof PlasticTypeBlock) {
                plasticBlockWithItem(block);
            } else if (block.block() instanceof ColoredPlasticBlock) {
                plasticBlockWithItem(block);
            } else if (block.block() instanceof MachineFrameBlock) {
                machineFrame(block);
            } else if (block.block() instanceof OreBlock) {
                oreBlock(block);
            } else if (block.block() instanceof LiquidBlock) {
                // Liquid blocks render through the fluid renderer and have no model; none are currently registered.
            } else if (block.block() instanceof HeatingElementBlock) {
                heater(block);
            }
        }

        machine(CoreBlocks.MATTER_FABRICATOR);
    }

    // ------------------------------------------------------------------------------------------------
    // Simple full-cube blocks
    // ------------------------------------------------------------------------------------------------

    /** A plain cube_all block whose item model is auto-generated from the block model at {@code block/<name>}. */
    private void blockWithItem(BlockDefinition<?> block) {
        var model = TexturedModel.CUBE.create(block.block(), generators.modelOutput);
        generators.blockStateOutput.accept(createSimpleBlock(block.block(), plainVariant(model)));
    }

    /** Translucent ice cube. Render type is carried on the texture material via {@code forceAllTranslucent()}. */
    private void iceBlockWithItem(BlockDefinition<?> block) {
        var model = TexturedModel.CUBE
                .updateTexture(TextureMapping::forceAllTranslucent)
                .create(block.block(), generators.modelOutput);
        generators.blockStateOutput.accept(createSimpleBlock(block.block(), plainVariant(model)));
    }

    /** Greyscale plastic cube; every face is tinted (tintindex 0) and recoloured at runtime by a block color handler. */
    private void plasticBlockWithItem(BlockDefinition<?> block) {
        var plastic = mat(GM.getResource("block/plastic_block"));
        var mapping = new TextureMapping().put(TextureSlot.ALL, plastic);

        var model = ModelTemplates.CUBE_ALL.extend()
                .element(element -> element
                        .from(0, 0, 0).to(16, 16, 16)
                        .allFaces((direction, face) -> face
                                .texture(TextureSlot.ALL)
                                .tintindex(0)
                                .cullface(direction)))
                .build()
                .create(block.block(), mapping, generators.modelOutput);

        generators.blockStateOutput.accept(createSimpleBlock(block.block(), plainVariant(model)));
    }

    // ------------------------------------------------------------------------------------------------
    // Ore blocks (base texture + tinted overlay element)
    // ------------------------------------------------------------------------------------------------

    private void oreBlock(BlockDefinition<?> def) {
        var base = mat(GM.getResource("block/ore/ore_block_base"));
        var overlay = mat(GM.getResource("block/ore/ore_block_overlay"));
        var mapping = new TextureMapping()
                .put(BASE, base)
                .put(OVERLAY, overlay)
                .put(TextureSlot.PARTICLE, base);

        float eps = 0.001f;
        var model = ExtendedModelTemplateBuilder.builder()
                .parent(GM.getMinecraftResource("block/block"))
                .guiLight(UnbakedModel.GuiLight.SIDE)
                .requiredTextureSlot(BASE)
                .requiredTextureSlot(OVERLAY)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .element(element -> element
                        .from(eps, eps, eps).to(16f - eps, 16f - eps, 16f - eps)
                        .allFaces((direction, face) -> face.texture(BASE).uvs(0, 0, 16, 16)))
                .element(element -> element
                        .from(0, 0, 0).to(16, 16, 16)
                        .allFaces((direction, face) -> face.texture(OVERLAY).uvs(0, 0, 16, 16).tintindex(1)))
                .build()
                .create(def.block(), mapping, generators.modelOutput);

        generators.blockStateOutput.accept(createSimpleBlock(def.block(), plainVariant(model)));
        // Item model is auto-generated from the block model located at block/<name>.
    }

    // ------------------------------------------------------------------------------------------------
    // Directional machines (model differs by a boolean state, oriented by FACING)
    // ------------------------------------------------------------------------------------------------

    private void heater(BlockDefinition<?> def) {
        var path = def.id().getPath();
        var on = GM.getResource("block/ihe/" + path + "_on");
        var off = GM.getResource("block/ihe/" + path + "_off");

        var onModel = ModelTemplates.CUBE_ALL.create(
                GM.getResource("block/machine/" + path + "/" + path + "_on"),
                new TextureMapping().put(TextureSlot.ALL, mat(on)), generators.modelOutput);
        var offModel = ModelTemplates.CUBE_ALL.create(
                GM.getResource("block/machine/" + path + "/" + path + "_off"),
                new TextureMapping().put(TextureSlot.ALL, mat(off)), generators.modelOutput);

        generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(def.block())
                .with(createBooleanModelDispatch(HeatingElementBlock.HEATING, plainVariant(onModel), plainVariant(offModel)))
                .with(facingDispatch()));

        generators.registerSimpleItemModel(def.block(), offModel);
    }

    private void machine(BlockDefinition<?> def) {
        var path = def.id().getPath();
        var on = GM.getResource("block/machine/" + path + "/on");
        var off = GM.getResource("block/machine/" + path + "/off");

        var onModel = ModelTemplates.CUBE.extend()
                .guiLight(UnbakedModel.GuiLight.FRONT)
                .build()
                .create(GM.getResource("block/machine/" + path + "/" + path + "_on"), machineMapping(on), generators.modelOutput);
        var offModel = ModelTemplates.CUBE.extend()
                .guiLight(UnbakedModel.GuiLight.FRONT)
                .build()
                .create(GM.getResource("block/machine/" + path + "/" + path + "_off"), machineMapping(off), generators.modelOutput);

        generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(def.block())
                .with(createBooleanModelDispatch(BlockStateProperties.POWERED, plainVariant(onModel), plainVariant(offModel)))
                .with(facingDispatch()));

        generators.registerSimpleItemModel(def.block(), onModel);
    }

    /** Machine cube faces: bottom/top from the shared machine textures, {@code front} on the NORTH face, sides shared. */
    private TextureMapping machineMapping(Identifier front) {
        var side = mat(MACHINE_SIDE);
        return new TextureMapping()
                .put(TextureSlot.DOWN, mat(MACHINE_BOTTOM))
                .put(TextureSlot.UP, mat(MACHINE_TOP))
                .put(TextureSlot.NORTH, mat(front))
                .put(TextureSlot.SOUTH, side)
                .put(TextureSlot.EAST, side)
                .put(TextureSlot.WEST, side)
                .put(TextureSlot.PARTICLE, side);
    }

    /** Rotates the NORTH-facing base model to match {@link BlockStateProperties#FACING}. Mirrors the old applyRotation logic. */
    private static PropertyDispatch<VariantMutator> facingDispatch() {
        return PropertyDispatch.modify(BlockStateProperties.FACING)
                .select(Direction.DOWN, BlockModelGenerators.X_ROT_90)
                .select(Direction.UP, BlockModelGenerators.X_ROT_270)
                .select(Direction.NORTH, BlockModelGenerators.NOP)
                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                .select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
                .select(Direction.EAST, BlockModelGenerators.Y_ROT_90);
    }

    // ------------------------------------------------------------------------------------------------
    // Machine frame (multipart of a base cube plus rotated cutout overlays per visible face/edge/corner)
    // ------------------------------------------------------------------------------------------------

    private void machineFrame(BlockDefinition<?> def) {
        var path = def.id().getPath();

        // Base fill for the cube (underlay for the overlays).
        var frameSide = mat(GM.getResource("block/machine/frame/frame"));
        var frameBase = ModelTemplates.CUBE.create(
                GM.getResource("block/machine/frame/" + path + "/base"),
                new TextureMapping()
                        .put(TextureSlot.DOWN, frameSide)
                        .put(TextureSlot.UP, frameSide)
                        .put(TextureSlot.NORTH, frameSide)
                        .put(TextureSlot.SOUTH, frameSide)
                        .put(TextureSlot.EAST, frameSide)
                        .put(TextureSlot.WEST, frameSide)
                        .put(TextureSlot.PARTICLE, frameSide),
                generators.modelOutput);

        // Overlay models (only the UP face is textured; rotations re-use them for the other faces).
        var edgeTop = frameOverlay("edge_top");
        var edgeBottom = frameOverlay("edge_bottom");
        var edgeLeft = frameOverlay("edge_left");
        var edgeRight = frameOverlay("edge_right");

        var cornerTL = frameOverlay("corner_top_left");
        var cornerTR = frameOverlay("corner_top_right");
        var cornerBL = frameOverlay("corner_bottom_left");
        var cornerBR = frameOverlay("corner_bottom_right");
        var none = frameOverlay("edge_none");
        var frame = frameOverlay("frame");

        var column = frameOverlay("column");
        var row = frameOverlay("row");
        var uTop = frameOverlay("u_top");
        var uBottom = frameOverlay("u_bottom");
        var uLeft = frameOverlay("u_left");
        var uRight = frameOverlay("u_right");

        var multipart = MultiPartGenerator.multiPart(def.block());
        multipart.with(plainVariant(frameBase)); // base is always present

        // UP face (plane NORTH/SOUTH/EAST/WEST)
        addFaceOverlays(multipart, edgeTop, edgeBottom, edgeLeft, edgeRight, cornerTL, cornerTR, cornerBL, cornerBR,
                0, 0,
                MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceNone(multipart, none, 0, 0,
                MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceExact(multipart, frame, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, column, 0, 0,
                MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, row, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, true);
        addFaceExact(multipart, uTop, 0, 0,
                MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uBottom, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uLeft, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uRight, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, true);

        // DOWN face (rotate 180 around X); top/bottom mapping flips
        addFaceOverlays(multipart, edgeTop, edgeBottom, edgeLeft, edgeRight, cornerTL, cornerTR, cornerBL, cornerBR,
                180, 0,
                MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceNone(multipart, none, 180, 0,
                MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceExact(multipart, frame, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, column, 180, 0,
                MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, row, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, true);
        addFaceExact(multipart, uTop, 180, 0,
                MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uBottom, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uLeft, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uRight, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, true);

        // NORTH face (rotate 90 around X) - swap left/right mapping
        addFaceOverlays(multipart, edgeTop, edgeBottom, edgeLeft, edgeRight, cornerTL, cornerTR, cornerBL, cornerBR,
                90, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.EAST, MachineFrameBlock.WEST);
        addFaceNone(multipart, none, 90, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.EAST, MachineFrameBlock.WEST);
        addFaceExact(multipart, frame, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, column, 90, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, row, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, true, MachineFrameBlock.WEST, true);
        addFaceExact(multipart, uTop, 90, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, uBottom, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, uLeft, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, true, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, uRight, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, true);

        // SOUTH face (rotate 270 X) - swap left/right mapping
        addFaceOverlays(multipart, edgeTop, edgeBottom, edgeLeft, edgeRight, cornerTL, cornerTR, cornerBL, cornerBR,
                270, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceNone(multipart, none, 270, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceExact(multipart, frame, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, column, 270, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, row, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, true);
        addFaceExact(multipart, uTop, 270, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uBottom, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uLeft, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, uRight, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, true);

        // WEST face (rotate 90 X, 270 Y)
        addFaceOverlays(multipart, edgeTop, edgeBottom, edgeLeft, edgeRight, cornerTL, cornerTR, cornerBL, cornerBR,
                90, 270,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH);
        addFaceNone(multipart, none, 90, 270,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH);
        addFaceExact(multipart, frame, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, column, 90, 270,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, row, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, true);
        addFaceExact(multipart, uTop, 90, 270,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, uBottom, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, uLeft, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, uRight, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, true);

        // EAST face (rotate 90 X, 90 Y)
        addFaceOverlays(multipart, edgeTop, edgeBottom, edgeLeft, edgeRight, cornerTL, cornerTR, cornerBL, cornerBR,
                90, 90,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH);
        addFaceNone(multipart, none, 90, 90,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH);
        addFaceExact(multipart, frame, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, column, 90, 90,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, row, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, true);
        addFaceExact(multipart, uTop, 90, 90,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, uBottom, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, uLeft, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, uRight, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, true);

        generators.blockStateOutput.accept(multipart);

        // Item model uses the base cube.
        generators.registerSimpleItemModel(def.block(), frameBase);
    }

    /** A single-face (UP) cutout overlay model. Returns the generated model location. */
    private Identifier frameOverlay(String name) {
        var target = GM.getResource("block/machine/frame/overlay/" + name);
        var mapping = new TextureMapping()
                .put(OVERLAY, mat(GM.getResource("block/machine/frame/" + name)))
                .put(TextureSlot.PARTICLE, mat(MACHINE_SIDE));

        ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(OVERLAY)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .element(element -> element
                        .from(0, 0, 0).to(16, 16, 16)
                        .face(Direction.UP, face -> face.texture(OVERLAY).cullface(Direction.UP)))
                .build()
                .create(target, mapping, generators.modelOutput);
        return target;
    }

    private void addFaceOverlays(MultiPartGenerator multipart,
                                 Identifier edgeTop, Identifier edgeBottom, Identifier edgeLeft, Identifier edgeRight,
                                 Identifier cornerTL, Identifier cornerTR, Identifier cornerBL, Identifier cornerBR,
                                 int rotX, int rotY,
                                 BooleanProperty top, BooleanProperty bottom, BooleanProperty left, BooleanProperty right) {
        // edges
        multipart.with(cond(top, false), oriented(edgeTop, rotX, rotY));
        multipart.with(cond(bottom, false), oriented(edgeBottom, rotX, rotY));
        multipart.with(cond(left, false), oriented(edgeLeft, rotX, rotY));
        multipart.with(cond(right, false), oriented(edgeRight, rotX, rotY));
        // corners (require both adjacent edges to be visible)
        multipart.with(cond(top, false).term(left, false), oriented(cornerTL, rotX, rotY));
        multipart.with(cond(top, false).term(right, false), oriented(cornerTR, rotX, rotY));
        multipart.with(cond(bottom, false).term(left, false), oriented(cornerBL, rotX, rotY));
        multipart.with(cond(bottom, false).term(right, false), oriented(cornerBR, rotX, rotY));
    }

    private void addFaceNone(MultiPartGenerator multipart, Identifier none, int rotX, int rotY,
                             BooleanProperty top, BooleanProperty bottom, BooleanProperty left, BooleanProperty right) {
        multipart.with(cond(top, true).term(bottom, true).term(left, true).term(right, true), oriented(none, rotX, rotY));
    }

    private void addFaceExact(MultiPartGenerator multipart, Identifier exact, int rotX, int rotY,
                              BooleanProperty top, boolean topValue, BooleanProperty bottom, boolean bottomValue,
                              BooleanProperty left, boolean leftValue, BooleanProperty right, boolean rightValue) {
        multipart.with(cond(top, topValue).term(bottom, bottomValue).term(left, leftValue).term(right, rightValue),
                oriented(exact, rotX, rotY));
    }

    // ------------------------------------------------------------------------------------------------
    // Small helpers
    // ------------------------------------------------------------------------------------------------

    private static ConditionBuilder cond(BooleanProperty property, boolean value) {
        return new ConditionBuilder().term(property, value);
    }

    /** Wraps a model location in a uv-locked multi-variant rotated by the given X/Y angles. */
    private static MultiVariant oriented(Identifier model, int rotX, int rotY) {
        var variant = plainVariant(model);
        if (rotX != 0) {
            variant = variant.with(VariantMutator.X_ROT.withValue(quadrant(rotX)));
        }
        if (rotY != 0) {
            variant = variant.with(VariantMutator.Y_ROT.withValue(quadrant(rotY)));
        }
        return variant.with(VariantMutator.UV_LOCK.withValue(true));
    }

    private static Quadrant quadrant(int angle) {
        return switch (((angle % 360) + 360) % 360) {
            case 0 -> Quadrant.R0;
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Invalid angle: " + angle);
        };
    }

    private static Material mat(Identifier texture) {
        return new Material(texture);
    }
}
