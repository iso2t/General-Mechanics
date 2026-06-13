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
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public non-sealed class BlockModelProvider extends ModelProviders {

    public static final Identifier MACHINE_BOTTOM = GM.getResource("block/machine/machine_bottom");
    public static final Identifier MACHINE_TOP = GM.getResource("block/machine/machine_top");
    public static final Identifier MACHINE_SIDE = GM.getResource("block/machine/machine_side");

    public BlockModelProvider(PackOutput output) {
        super(output);
    }

    @Override
	protected void registerBlockModels (@NotNull BlockModelGenerators blockGenerator, @NotNull ItemModelGenerators itemGenerator) {
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
                simpleItem(block);
            } else if (block.block() instanceof HeatingElementBlock) {
                heater(block);
            }
        }

        machine(CoreBlocks.MATTER_FABRICATOR);
    }

    private void simpleItem(BlockDefinition<?> block) {
        itemModels().getBuilder(block.id().getPath())
                .parent(models().getExistingFile(GM.getMinecraftResource("item/generated")))
                .texture("layer0", GM.getMinecraftResource("block/water_still"));
    }

    private void blockWithItem(BlockDefinition<?> block) {
        err(List.of(block.id()));
        simpleBlockWithItem(block.block(), cubeAll(block.block()));
    }

    private void iceBlockWithItem(BlockDefinition<?> block) {
        err(List.of(block.id()));
        var model = models().cubeAll(block.id().getPath(), GM.getResource("block/" + block.id().getPath())).renderType("translucent").texture("particle", GM.getResource("block/" + block.id().getPath()));

        simpleBlockWithItem(block.block(), model);
    }

    private void plasticBlockWithItem(BlockDefinition<?> block) {
        err(List.of(block.id()));
        var model = models().cubeAll(block.id().getPath(), GM.getResource("block/plastic_block")).texture("particle", GM.getResource("block/plastic_block"))
                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .face(Direction.NORTH).texture("#all").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.EAST).texture("#all").tintindex(0).cullface(Direction.EAST).end()
                .face(Direction.SOUTH).texture("#all").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).texture("#all").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.UP).texture("#all").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.DOWN).texture("#all").tintindex(0).cullface(Direction.DOWN).end()
                .end();
        simpleBlockWithItem(block.block(), model);
    }

    private void heater(BlockDefinition<?> block) {
        var texture_on = modLoc("block/ihe/" + block.id().getPath() + "_on");
        var texture_off = modLoc("block/ihe/" + block.id().getPath() + "_off");

        err(List.of(texture_on));

        var model_on = models().cube("block/machine/" + block.id().getPath() + "/" + block.id().getPath() + "_on", texture_on, texture_on, texture_on, texture_on, texture_on, texture_on).texture("particle", texture_on);
        var model_off = models().cube("block/machine/" + block.id().getPath() + "/" + block.id().getPath() + "_off", texture_off, texture_off, texture_off, texture_off, texture_off, texture_off).texture("particle", texture_off);
        directionBlock(block.block(), (state, builder) -> builder.modelFile(state.getValue(HeatingElementBlock.HEATING) ? model_on : model_off));

        simpleBlockItem(block.block(), model_off);
    }

    private void machine(BlockDefinition<?> block) {
        var on = modLoc("block/machine/" + block.id().getPath() + "/on");
        var off = modLoc("block/machine/" + block.id().getPath() + "/off");

        err(List.of(on, off));

        BlockModelBuilder modelOn = models().cube("block/machine/" + block.id().getPath() + "/" + block.id().getPath() + "_on", MACHINE_BOTTOM, MACHINE_TOP, on, MACHINE_SIDE, MACHINE_SIDE, MACHINE_SIDE)
                .texture("particle", MACHINE_SIDE)
                .guiLight(BlockModel.GuiLight.FRONT);
        BlockModelBuilder modelOff = models().cube("block/machine/" + block.id().getPath() + "/" + block.id().getPath() + "_off", MACHINE_BOTTOM, MACHINE_TOP, off, MACHINE_SIDE, MACHINE_SIDE, MACHINE_SIDE)
                .texture("particle", MACHINE_SIDE)
                .guiLight(BlockModel.GuiLight.FRONT);
        directionBlock(block.block(), (state, builder) -> builder.modelFile(state.getValue(BlockStateProperties.POWERED) ? modelOn : modelOff));

        simpleBlockItem(block.block(), modelOn);
    }

    private void oreBlock(BlockDefinition<?> def) {
        var base = modLoc("block/ore/ore_block_base");
        var overlay = modLoc("block/ore/ore_block_overlay");
        ModelFile blockModel = twoLayerCubeBlockModel(def.getRegistryFriendlyName(), base, overlay);
        simpleBlock(def.block(), blockModel);
        twoLayerCubeItemModel(def.getRegistryFriendlyName());
    }

    /**
     * One block model with two elements (base + overlay).
     */
    private ModelFile twoLayerCubeBlockModel(String name, Identifier baseTex, Identifier overlayTex) {
        float eps = 0.001f;
        BlockModelBuilder b = models().getBuilder("block/" + name)
                .parent(new ModelFile.ExistingModelFile(mcLoc("block/block"), existingFileHelper))
                .renderType("minecraft:cutout")
                .texture("base", baseTex)
                .texture("overlay", overlayTex)
                .texture("particle", baseTex)
                .guiLight(BlockModel.GuiLight.SIDE);

        b.element()
                .from(eps, eps, eps).to(16f - eps, 16f - eps, 16f - eps)
                .allFaces((d, f) -> f.texture("#base").uvs(0, 0, 16, 16))
                .end();

        b.element()
                .from(0f, 0f, 0f).to(16f, 16f, 16f)
                .allFaces((d, f) -> f.texture("#overlay").uvs(0, 0, 16, 16).tintindex(1))
                .end();

        return b;
    }

    private void twoLayerCubeItemModel(String name) {
        itemModels().getBuilder(name)
                .parent(models().getExistingFile(modLoc("block/" + name)));
    }


    private void machineFrame(BlockDefinition<?> def) {
        // Base fill for the cube (underlay for overlays)
        var frame_side = GM.getResource("block/machine/frame/frame");
        var frameBase = models().cube("block/machine/frame/" + def.id().getPath() + "/base",
                        frame_side, frame_side, frame_side, frame_side, frame_side, frame_side)
                .texture("particle", frame_side);

        // Overlay models (only UP face textured; we rotate for other faces)
        var overlayEdgeTop = frameOverlay("edge_top");
        var overlayEdgeBottom = frameOverlay("edge_bottom");
        var overlayEdgeLeft = frameOverlay("edge_left");
        var overlayEdgeRight = frameOverlay("edge_right");

        var overlayCornerTL = frameOverlay("corner_top_left");
        var overlayCornerTR = frameOverlay("corner_top_right");
        var overlayCornerBL = frameOverlay("corner_bottom_left");
        var overlayCornerBR = frameOverlay("corner_bottom_right");
        var overlayNone = frameOverlay("edge_none");
        var overlayFrame = frameOverlay("frame");

        // Combined overlays for exact patterns
        var overlayColumn = frameOverlay("column");
        var overlayRow = frameOverlay("row");
        var overlayUTop = frameOverlay("u_top");
        var overlayUBottom = frameOverlay("u_bottom");
        var overlayULeft = frameOverlay("u_left");
        var overlayURight = frameOverlay("u_right");

        // Track textures so missing ones don't fail generation
        err(List.of(
                GM.getResource("block/machine/frame/edge_top"),
                GM.getResource("block/machine/frame/edge_bottom"),
                GM.getResource("block/machine/frame/edge_left"),
                GM.getResource("block/machine/frame/edge_right"),
                GM.getResource("block/machine/frame/corner_top_left"),
                GM.getResource("block/machine/frame/corner_top_right"),
                GM.getResource("block/machine/frame/corner_bottom_left"),
                GM.getResource("block/machine/frame/corner_bottom_right"),
                GM.getResource("block/machine/frame/edge_none"),
                GM.getResource("block/machine/frame/frame"),
                GM.getResource("block/machine/frame/column"),
                GM.getResource("block/machine/frame/row"),
                GM.getResource("block/machine/frame/u_top"),
                GM.getResource("block/machine/frame/u_bottom"),
                GM.getResource("block/machine/frame/u_left"),
                GM.getResource("block/machine/frame/u_right")
        ));

        var multipart = getMultipartBuilder(def.block());
        // Base always
        multipart.part().modelFile(frameBase).addModel();

        // Helper: add overlays for a face given mapping of top/bottom/left/right to properties
        // UP face (plane NORTH/SOUTH/EAST/WEST)
        addFaceOverlays(multipart, overlayEdgeTop, overlayEdgeBottom, overlayEdgeLeft, overlayEdgeRight,
                overlayCornerTL, overlayCornerTR, overlayCornerBL, overlayCornerBR,
                0, 0,
                MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceNone(multipart, overlayNone, 0, 0,
                MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceExact(multipart, overlayFrame, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        // exact patterns
        addFaceExact(multipart, overlayColumn, 0, 0,
                MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayRow, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, true);
        addFaceExact(multipart, overlayUTop, 0, 0,
                MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayUBottom, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayULeft, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayURight, 0, 0,
                MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, true);

        // DOWN face (rotate 180 around X); mapping flips top/bottom
        addFaceOverlays(multipart, overlayEdgeTop, overlayEdgeBottom, overlayEdgeLeft, overlayEdgeRight,
                overlayCornerTL, overlayCornerTR, overlayCornerBL, overlayCornerBR,
                180, 0,
                MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceNone(multipart, overlayNone, 180, 0,
                MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceExact(multipart, overlayFrame, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayColumn, 180, 0,
                MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayRow, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, true);
        addFaceExact(multipart, overlayUTop, 180, 0,
                MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayUBottom, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayULeft, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayURight, 180, 0,
                MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, true);

        // NORTH face (rotate 90 around X) - swap left/right mapping
        addFaceOverlays(multipart, overlayEdgeTop, overlayEdgeBottom, overlayEdgeLeft, overlayEdgeRight,
                overlayCornerTL, overlayCornerTR, overlayCornerBL, overlayCornerBR,
                90, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.EAST, MachineFrameBlock.WEST);
        addFaceNone(multipart, overlayNone, 90, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.EAST, MachineFrameBlock.WEST);
        addFaceExact(multipart, overlayFrame, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, overlayColumn, 90, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, overlayRow, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, true, MachineFrameBlock.WEST, true);
        addFaceExact(multipart, overlayUTop, 90, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, overlayUBottom, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, overlayULeft, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, true, MachineFrameBlock.WEST, false);
        addFaceExact(multipart, overlayURight, 90, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.EAST, false, MachineFrameBlock.WEST, true);
        addFaceNone(multipart, overlayNone, 90, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.EAST, MachineFrameBlock.WEST);

        // SOUTH face (rotate 270 X) - swap left/right mapping
        addFaceOverlays(multipart, overlayEdgeTop, overlayEdgeBottom, overlayEdgeLeft, overlayEdgeRight,
                overlayCornerTL, overlayCornerTR, overlayCornerBL, overlayCornerBR,
                270, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceNone(multipart, overlayNone, 270, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.WEST, MachineFrameBlock.EAST);
        addFaceExact(multipart, overlayFrame, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayColumn, 270, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayRow, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, true);
        addFaceExact(multipart, overlayUTop, 270, 0,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayUBottom, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayULeft, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, true, MachineFrameBlock.EAST, false);
        addFaceExact(multipart, overlayURight, 270, 0,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.WEST, false, MachineFrameBlock.EAST, true);
        addFaceNone(multipart, overlayNone, 270, 0,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.WEST, MachineFrameBlock.EAST);

        // WEST face (rotate 90 X, 270 Y)
        addFaceOverlays(multipart, overlayEdgeTop, overlayEdgeBottom, overlayEdgeLeft, overlayEdgeRight,
                overlayCornerTL, overlayCornerTR, overlayCornerBL, overlayCornerBR,
                90, 270,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH);
        addFaceNone(multipart, overlayNone, 90, 270,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH);
        addFaceExact(multipart, overlayFrame, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, overlayColumn, 90, 270,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, overlayRow, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, true);
        addFaceExact(multipart, overlayUTop, 90, 270,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, overlayUBottom, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, overlayULeft, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, true, MachineFrameBlock.SOUTH, false);
        addFaceExact(multipart, overlayURight, 90, 270,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.NORTH, false, MachineFrameBlock.SOUTH, true);
        addFaceNone(multipart, overlayNone, 90, 270,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.NORTH, MachineFrameBlock.SOUTH);

        // EAST face (rotate 90 X, 90 Y)
        addFaceOverlays(multipart, overlayEdgeTop, overlayEdgeBottom, overlayEdgeLeft, overlayEdgeRight,
                overlayCornerTL, overlayCornerTR, overlayCornerBL, overlayCornerBR,
                90, 90,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH);
        addFaceNone(multipart, overlayNone, 90, 90,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH);
        addFaceExact(multipart, overlayFrame, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, overlayColumn, 90, 90,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, true, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, overlayRow, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, true);
        addFaceExact(multipart, overlayUTop, 90, 90,
                MachineFrameBlock.UP, true, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, overlayUBottom, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, true, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, overlayULeft, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, true, MachineFrameBlock.NORTH, false);
        addFaceExact(multipart, overlayURight, 90, 90,
                MachineFrameBlock.UP, false, MachineFrameBlock.DOWN, false, MachineFrameBlock.SOUTH, false, MachineFrameBlock.NORTH, true);
        addFaceNone(multipart, overlayNone, 90, 90,
                MachineFrameBlock.UP, MachineFrameBlock.DOWN, MachineFrameBlock.SOUTH, MachineFrameBlock.NORTH);

        // Item model uses the base
        simpleBlockItem(def.block(), frameBase);
    }

    private BlockModelBuilder frameOverlay(String name) {
        var tex = GM.getResource("block/machine/frame/" + name);
        return models().getBuilder("block/machine/frame/overlay/" + name)
                .renderType("cutout")
                .texture("particle", MACHINE_SIDE)
                .texture("overlay", tex)
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.UP).texture("#overlay").cullface(Direction.UP).end()
                .end();
    }

    private void addFaceOverlays(MultiPartBlockStateBuilder multipart, ModelFile edgeTop, ModelFile edgeBottom, ModelFile edgeLeft, ModelFile edgeRight, ModelFile cornerTL, ModelFile cornerTR, ModelFile cornerBL, ModelFile cornerBR, int rotX, int rotY, BooleanProperty topProp, BooleanProperty bottomProp, BooleanProperty leftProp, BooleanProperty rightProp) {
        // edges
        multipart.part().modelFile(edgeTop).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(topProp, false).end();
        multipart.part().modelFile(edgeBottom).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(bottomProp, false).end();
        multipart.part().modelFile(edgeLeft).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(leftProp, false).end();
        multipart.part().modelFile(edgeRight).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(rightProp, false).end();

        // corners (require both adjacent edges to be visible)
        multipart.part().modelFile(cornerTL).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(topProp, false).condition(leftProp, false).end();
        multipart.part().modelFile(cornerTR).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(topProp, false).condition(rightProp, false).end();
        multipart.part().modelFile(cornerBL).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(bottomProp, false).condition(leftProp, false).end();
        multipart.part().modelFile(cornerBR).rotationX(rotX).rotationY(rotY).uvLock(true).addModel().condition(bottomProp, false).condition(rightProp, false).end();
    }

    private void addFaceNone(MultiPartBlockStateBuilder multipart, ModelFile none, int rotX, int rotY, BooleanProperty topProp, BooleanProperty bottomProp, BooleanProperty leftProp, BooleanProperty rightProp) {
        multipart.part().modelFile(none).rotationX(rotX).rotationY(rotY).uvLock(true).addModel()
                .condition(topProp, true)
                .condition(bottomProp, true)
                .condition(leftProp, true)
                .condition(rightProp, true)
                .end();
    }

    private void addFaceExact(MultiPartBlockStateBuilder multipart, ModelFile exact, int rotX, int rotY, BooleanProperty topProp, boolean top, BooleanProperty bottomProp, boolean bottom, BooleanProperty leftProp, boolean left, BooleanProperty rightProp, boolean right) {
        multipart.part().modelFile(exact).rotationX(rotX).rotationY(rotY).uvLock(true).addModel()
                .condition(topProp, top)
                .condition(bottomProp, bottom)
                .condition(leftProp, left)
                .condition(rightProp, right)
                .end();
    }

    private VariantBlockStateBuilder directionBlock(Block block, BiConsumer<BlockState, ConfiguredModel.Builder<?>> model) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        builder.forAllStates(state -> {
            ConfiguredModel.Builder<?> bld = ConfiguredModel.builder();
            model.accept(state, bld);
            applyRotationBld(bld, state.getValue(BlockStateProperties.FACING));
            return bld.build();
        });
        return builder;
    }

    private void applyRotationBld(ConfiguredModel.Builder<?> builder, Direction direction) {
        switch (direction) {
            case DOWN -> builder.rotationX(90);
            case UP -> builder.rotationX(-90);
            case NORTH -> {
            }
            case SOUTH -> builder.rotationY(180);
            case WEST -> builder.rotationY(270);
            case EAST -> builder.rotationY(90);
        }
    }

    /**
     * Ignores missing textures so we can still build data without the texture present.
     *
     * @param list a list of Identifiers that we know will exist but currently don't.
     */
    private void err (List<Identifier> list) {
        for (var res : list) {
            existingFileHelper.trackGenerated(res, PackType.CLIENT_RESOURCES, ".png", "textures");
        }
    }

}
