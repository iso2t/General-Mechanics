package general.mechanics.datagen.model;

import com.mojang.math.Quadrant;
import general.api.block.DecorativeBlock;
import general.api.block.materials.MetalBlock;
import general.api.block.util.ILitProvider;
import general.api.definitions.BlockDefinition;
import general.api.definitions.FluidDefinition;
import general.api.fluid.BaseFluid;
import general.api.mod.GenAPI;
import general.api.model.IBasicModel;
import general.api.model.IConfigurableMachineModel;
import general.api.model.IMachineModel;
import general.api.resources.Resource;
import general.api.rotation.BlockRotationStrategies;
import general.api.rotation.BlockRotationStrategy;
import general.api.rotation.IRotatableBlock;
import general.mechanics.client.color.ElementItemTintSource;
import general.mechanics.client.model.CableModelLoader;
import general.mechanics.client.model.ConfigurableMachineModelLoader;
import general.mechanics.common.block.misc.EncasedFluidBlock;
import general.mechanics.common.block.misc.HeatingElementBlock;
import general.mechanics.common.block.misc.RubberWood;
import general.mechanics.registries.GenBlocks;
import general.mechanics.registries.GenFluids;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public final class BlockModelProvider extends ModelProviders {

	public static final Identifier MACHINE_BOTTOM = Resource.get("block/machine/machine_bottom");
	public static final Identifier MACHINE_TOP    = Resource.get("block/machine/machine_top");
	public static final Identifier MACHINE_SIDE   = Resource.get("block/machine/machine_side");

	// Custom texture slots used by layered encased-fluid, ore, and machine-frame models.
	private static final TextureSlot BASE                = TextureSlot.create("base");
	private static final TextureSlot OVERLAY             = TextureSlot.create("overlay");
	// Resin-spot face texture for a full rubber log.
	private static final TextureSlot RESIN               = TextureSlot.create("resin");
	private static final int         DEFAULT_WATER_COLOR = 0xFF3F76E4;

	private BlockModelGenerators generators;

	public BlockModelProvider (PackOutput output) {
		super(output);
	}

	@Override
	public @NonNull String getName () {
		return "Model Definitions - " + GenAPI.getModId() + " (blocks)";
	}

	@Override
	protected @NotNull Stream<? extends Holder<Item>> getKnownItems () {
		return BuiltInRegistries.ITEM.listElements().filter(holder -> holder.getKey().identifier().getNamespace().equals(GenAPI.getModId())).filter(holder -> holder.value() instanceof BlockItem);
	}

	@Override
	protected void registerModels (@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
		this.generators = blockModels;

		for (var block : GenBlocks.INSTANCE.getBlocks()) {
			if (block.get() instanceof EncasedFluidBlock encasedFluid) {
				registerEncasedFluid(block, encasedFluid);
			} else if (block.get() instanceof MetalBlock metal) {
				registerMetalBlock(block, metal);
			} else if (block.get() instanceof DecorativeBlock || block.get() instanceof IBasicModel) {
				blockWithItem(block);
			} else if (block.get() instanceof IConfigurableMachineModel machine) {
				registerConfigurableMachine(block, machine);
			} else if (block.get() instanceof IMachineModel machine) {
				registerMachine(block, machine);
			}/*else if (block.get() instanceof MachineFrameBlock) {
				machineFrame(block);
			} else if (block.get() instanceof OreBlock) {
				oreBlock(block);
			} else if (block.get() instanceof LiquidBlock) {
				// Liquid blocks render through the fluid renderer and have no model; none are currently registered.
			}*/ else if (block.get() instanceof HeatingElementBlock) {
				heater(block);
			}
		}

		for (var fluid : GenFluids.getFluids()) {
			registerFluid(fluid);
		}

		registerCable();

		// Rubber tree set
		blockModels.createTintedLeaves(GenBlocks.RUBBER_LEAVES.get(), TexturedModel.LEAVES, -12012264);
		blockModels.createPlantWithDefaultItem(GenBlocks.RUBBER_SAPLING.get(), GenBlocks.POTTED_RUBBER_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);
		rubberLogWithResin(GenBlocks.RUBBER_LOG.get(), "rubber_log", Resource.get("block/rubber_log_top"));
		rubberLogWithResin(GenBlocks.RUBBER_WOOD.get(), "rubber_wood", Resource.get("block/rubber_log"));
		blockModels.woodProvider(GenBlocks.STRIPPED_RUBBER_LOG.get()).logWithHorizontal(GenBlocks.STRIPPED_RUBBER_LOG.get()).wood(GenBlocks.STRIPPED_RUBBER_WOOD.get());
	}

	private void registerMetalBlock (BlockDefinition<?> definition, MetalBlock metal) {
		var texture = mat(metal.getTopTexture());
		var model = ExtendedModelTemplateBuilder.builder().parent(Resource.getMinecraftResource("block/block")).requiredTextureSlot(TextureSlot.ALL).requiredTextureSlot(TextureSlot.PARTICLE).element(element -> element.from(0, 0, 0).to(16, 16, 16).allFaces((direction, face) -> face.texture(TextureSlot.ALL).uvs(0, 0, 16, 16).cullface(direction).tintindex(0))).build().create(Resource.get("block/" + definition.getId().getPath()), new TextureMapping().put(TextureSlot.ALL, texture).put(TextureSlot.PARTICLE, texture), generators.modelOutput);

		registerBlockState(definition, model);
		generators.registerSimpleTintedItemModel(definition.get(), model, ElementItemTintSource.INSTANCE);
	}

	private void registerEncasedFluid (BlockDefinition<?> definition, EncasedFluidBlock encasedFluid) {
		var fluid = encasedFluid.getFluid();
		var isWater = fluid.isSame(Fluids.WATER);
		var isLava = fluid.isSame(Fluids.LAVA);
		if (!isWater && !isLava) {
			throw new IllegalStateException("No encased-fluid model texture is defined for " + BuiltInRegistries.FLUID.getKey(fluid));
		}

		var casing = mat(Resource.get("block/encased_fluid_block"));
		var blockModel = ModelTemplates.CUBE_ALL.create(definition.get(), TextureMapping.cube(casing), generators.modelOutput);
		registerBlockState(definition, blockModel);

		var stillTexture = Resource.getMinecraftResource(isWater ? "block/water_still" : "block/lava_still");
		var fluidMaterial = mat(stillTexture).withForceTranslucent(isWater);
		var itemModel = ExtendedModelTemplateBuilder.builder().parent(Resource.getMinecraftResource("block/block")).requiredTextureSlot(BASE).requiredTextureSlot(OVERLAY).requiredTextureSlot(TextureSlot.PARTICLE).element(element -> element.from(0.01F, 0.01F, 0.01F).to(15.99F, 15.99F, 15.99F).allFaces((_, face) -> {
			face.texture(BASE).uvs(0, 0, 16, 16);
			if (isWater) face.tintindex(0);
			if (isLava) face.lightEmission(15);
		})).element(element -> element.from(0, 0, 0).to(16, 16, 16).textureAll(OVERLAY)).build().create(Resource.get("block/" + definition.getId().getPath() + "_item"), new TextureMapping().put(BASE, fluidMaterial).put(OVERLAY, casing).put(TextureSlot.PARTICLE, casing), generators.modelOutput);

		if (isWater) {
			generators.itemModelOutput.accept(definition.get().asItem(), ItemModelUtils.tintedModel(itemModel, ItemModelUtils.constantTint(DEFAULT_WATER_COLOR)));
		} else {
			generators.itemModelOutput.accept(definition.get().asItem(), ItemModelUtils.plainModel(itemModel));
		}
	}

	private void registerFluid (FluidDefinition definition) {
		if (!(definition.type().get() instanceof BaseFluid fluid)) {
			throw new IllegalStateException("Fluid definition '" + definition.englishName() + "' does not use BaseFluid");
		}

		var model = ModelTemplates.PARTICLE_ONLY.create(definition.block().get(), new TextureMapping().put(TextureSlot.PARTICLE, new Material(fluid.getStillTexture())), generators.modelOutput);
		generators.blockStateOutput.accept(createSimpleBlock(definition.block().get(), plainVariant(model)));
	}

	private void registerCable () {
		generators.blockStateOutput.accept(new BlockModelDefinitionGenerator() {
			@Override
			public @NonNull Block block () {
				return GenBlocks.CABLE.get();
			}

			@Override
			public @NonNull BlockStateModelDispatcher create () {
				return new BlockStateModelDispatcher(CableModelLoader.INSTANCE);
			}
		});
	}

	private void registerMachine (BlockDefinition<?> block, IMachineModel machine) {
		var path = block.getId().getPath();

		var model = ModelTemplates.CUBE.create(Resource.get("block/machine/" + path), machineMapping(machine), generators.modelOutput);
		if (block.get() instanceof ILitProvider litProvider) {
			var litModel = ModelTemplates.CUBE.create(Resource.get("block/machine/" + path + "_lit"), machineMapping(machine, litProvider.getLitTexture()), generators.modelOutput);
			registerLitBlockState(block, model, litModel);
		} else {
			registerBlockState(block, model);
		}

		generators.registerSimpleItemModel(block.get(), model);
	}

	private void registerConfigurableMachine (BlockDefinition<?> block, IConfigurableMachineModel machine) {
		var path = block.getId().getPath();
		var itemModel = ModelTemplates.CUBE.create(Resource.get("block/machine/" + path), machineMapping(machine), generators.modelOutput);
		generators.blockStateOutput.accept(new BlockModelDefinitionGenerator() {
			@Override
			public @NonNull Block block () {
				return block.get();
			}

			@Override
			public @NonNull BlockStateModelDispatcher create () {
				return new BlockStateModelDispatcher(ConfigurableMachineModelLoader.INSTANCE);
			}
		});
		generators.registerSimpleItemModel(block.get(), itemModel);
	}

	private void blockWithItem (BlockDefinition<?> block) {
		var model = TexturedModel.CUBE.create(block.get(), generators.modelOutput);
		registerBlockState(block, model);
		generators.registerSimpleItemModel(block.get(), model);
	}

	/**
	 * Emits every orientation variant required by a block that opted into a rotation strategy.
	 */
	private void registerBlockState (BlockDefinition<?> block, Identifier model) {
		var variant = plainVariant(model);
		if (block.get() instanceof IRotatableBlock rotatable) {
			generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get()).with(rotationDispatch(rotatable.getRotationStrategy(), variant)));
		} else {
			generators.blockStateOutput.accept(createSimpleBlock(block.get(), variant));
		}
	}

	/**
	 * Selects the machine's normal or lit model first, then applies its orientation. Keeping the
	 * orientation as a mutator lets Minecraft generate the full cartesian product of both properties.
	 */
	private void registerLitBlockState (BlockDefinition<?> block, Identifier model, Identifier litModel) {
		var litDispatch = createBooleanModelDispatch(ILitProvider.LIT, plainVariant(litModel), plainVariant(model));
		if (block.get() instanceof IRotatableBlock rotatable) {
			generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get()).with(litDispatch).with(rotationMutatorDispatch(rotatable.getRotationStrategy())));
		} else {
			generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get()).with(litDispatch));
		}
	}

	private static PropertyDispatch<MultiVariant> rotationDispatch (BlockRotationStrategy strategy, MultiVariant variant) {
		if (strategy == BlockRotationStrategies.HORIZONTAL_FACING) {
			return PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING).select(Direction.NORTH, variant).select(Direction.SOUTH, variant.with(Y_ROT_180)).select(Direction.WEST, variant.with(Y_ROT_270)).select(Direction.EAST, variant.with(Y_ROT_90));
		}
		if (strategy == BlockRotationStrategies.FACING) {
			return PropertyDispatch.initial(BlockStateProperties.FACING).select(Direction.DOWN, variant.with(X_ROT_90)).select(Direction.UP, variant.with(X_ROT_270)).select(Direction.NORTH, variant).select(Direction.SOUTH, variant.with(Y_ROT_180)).select(Direction.WEST, variant.with(Y_ROT_270)).select(Direction.EAST, variant.with(Y_ROT_90));
		}
		if (strategy == BlockRotationStrategies.AXIS) {
			return PropertyDispatch.initial(BlockStateProperties.AXIS).select(Direction.Axis.X, variant).select(Direction.Axis.Y, variant).select(Direction.Axis.Z, variant);
		}
		throw new IllegalArgumentException("No generated blockstate dispatch for rotation strategy " + strategy);
	}

	private static PropertyDispatch<VariantMutator> rotationMutatorDispatch (BlockRotationStrategy strategy) {
		if (strategy == BlockRotationStrategies.HORIZONTAL_FACING) {
			return PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.NORTH, NOP).select(Direction.SOUTH, Y_ROT_180).select(Direction.WEST, Y_ROT_270).select(Direction.EAST, Y_ROT_90);
		}
		if (strategy == BlockRotationStrategies.FACING) {
			return facingDispatch();
		}
		if (strategy == BlockRotationStrategies.AXIS) {
			return PropertyDispatch.modify(BlockStateProperties.AXIS).select(Direction.Axis.X, NOP).select(Direction.Axis.Y, NOP).select(Direction.Axis.Z, NOP);
		}
		throw new IllegalArgumentException("No generated blockstate dispatch for rotation strategy " + strategy);
	}

	/**
	 * Rubber log/wood with sap states. Renders the plain pillar by {@code AXIS}; an upright log at
	 * {@link RubberWood.RubberLogBlock#MAX_SAP} swaps to a model whose north face shows {@code rubber_log_resin},
	 * rotated to {@link RubberWood.RubberLogBlock#RESIN_FACING}. The plain {@code SAP 0/1/2} variants and the resin
	 * variants are mutually exclusive, so the resin model fully replaces the face (no overlay z-fighting).
	 */
	private void rubberLogWithResin (Block block, String name, Identifier endTex) {
		var sideTex = Resource.get("block/rubber_log");
		var resinTex = Resource.get("block/rubber_log_resin");

		// Plain pillar model (also the inventory icon).
		var plain = ModelTemplates.CUBE_COLUMN.create(Resource.get("block/" + name), TextureMapping.column(mat(sideTex), mat(endTex)), generators.modelOutput);

		// Resin variant: identical cube, but the north face uses the resin texture.
		var resin = ExtendedModelTemplateBuilder.builder().parent(Resource.getMinecraftResource("block/block")).requiredTextureSlot(TextureSlot.SIDE).requiredTextureSlot(TextureSlot.END).requiredTextureSlot(RESIN).requiredTextureSlot(TextureSlot.PARTICLE).element(element -> element.from(0, 0, 0).to(16, 16, 16).face(Direction.DOWN, face -> face.texture(TextureSlot.END).uvs(0, 0, 16, 16).cullface(Direction.DOWN)).face(Direction.UP, face -> face.texture(TextureSlot.END).uvs(0, 0, 16, 16).cullface(Direction.UP)).face(Direction.NORTH, face -> face.texture(RESIN).uvs(0, 0, 16, 16).cullface(Direction.NORTH)).face(Direction.SOUTH, face -> face.texture(TextureSlot.SIDE).uvs(0, 0, 16, 16).cullface(Direction.SOUTH)).face(Direction.WEST, face -> face.texture(TextureSlot.SIDE).uvs(0, 0, 16, 16).cullface(Direction.WEST)).face(Direction.EAST, face -> face.texture(TextureSlot.SIDE).uvs(0, 0, 16, 16).cullface(Direction.EAST))).build().create(Resource.get("block/" + name + "_resin"), new TextureMapping().put(TextureSlot.SIDE, mat(sideTex)).put(TextureSlot.END, mat(endTex)).put(RESIN, mat(resinTex)).put(TextureSlot.PARTICLE, mat(sideTex)), generators.modelOutput);

		var axis = RotatedPillarBlock.AXIS;
		generators.blockStateOutput.accept(MultiPartGenerator.multiPart(block).with(new ConditionBuilder().term(axis, Direction.Axis.X), rotated(plain, 90, 90)).with(new ConditionBuilder().term(axis, Direction.Axis.Z), rotated(plain, 90, 0)).with(new ConditionBuilder().term(axis, Direction.Axis.Y).term(RubberWood.RubberLogBlock.SAP, 0, 1, 2), plainVariant(plain)).with(new ConditionBuilder().term(axis, Direction.Axis.Y).term(RubberWood.RubberLogBlock.SAP, RubberWood.RubberLogBlock.MAX_SAP).term(RubberWood.RubberLogBlock.RESIN_FACING, Direction.NORTH), plainVariant(resin)).with(new ConditionBuilder().term(axis, Direction.Axis.Y).term(RubberWood.RubberLogBlock.SAP, RubberWood.RubberLogBlock.MAX_SAP).term(RubberWood.RubberLogBlock.RESIN_FACING, Direction.EAST), rotated(resin, 0, 90)).with(new ConditionBuilder().term(axis, Direction.Axis.Y).term(RubberWood.RubberLogBlock.SAP, RubberWood.RubberLogBlock.MAX_SAP).term(RubberWood.RubberLogBlock.RESIN_FACING, Direction.SOUTH), rotated(resin, 0, 180)).with(new ConditionBuilder().term(axis, Direction.Axis.Y).term(RubberWood.RubberLogBlock.SAP, RubberWood.RubberLogBlock.MAX_SAP).term(RubberWood.RubberLogBlock.RESIN_FACING, Direction.WEST), rotated(resin, 0, 270)));

		generators.registerSimpleItemModel(block, plain);
	}

	/**
	 * A model variant rotated by the given X/Y quadrant angles (no uv-lock — texture follows the face).
	 */
	private static MultiVariant rotated (Identifier model, int rotX, int rotY) {
		var variant = plainVariant(model);
		if (rotX != 0) variant = variant.with(VariantMutator.X_ROT.withValue(quadrant(rotX)));
		if (rotY != 0) variant = variant.with(VariantMutator.Y_ROT.withValue(quadrant(rotY)));
		return variant;
	}

	// ------------------------------------------------------------------------------------------------
	// Directional machines (model differs by a boolean state, oriented by FACING)
	// ------------------------------------------------------------------------------------------------

	private void heater (BlockDefinition<?> def) {
		var path = def.getId().getPath();
		var on = Resource.get("block/ihe/" + path + "_on");
		var off = Resource.get("block/ihe/" + path + "_off");

		var onModel = ModelTemplates.CUBE_ALL.create(Resource.get("block/machine/" + path + "/" + path + "_on"), new TextureMapping().put(TextureSlot.ALL, mat(on)), generators.modelOutput);
		var offModel = ModelTemplates.CUBE_ALL.create(Resource.get("block/machine/" + path + "/" + path + "_off"), new TextureMapping().put(TextureSlot.ALL, mat(off)), generators.modelOutput);

		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(def.get()).with(createBooleanModelDispatch(HeatingElementBlock.HEATING, plainVariant(onModel), plainVariant(offModel))));

		generators.registerSimpleItemModel(def.get(), offModel);
	}

	/**
	 * Maps the shared {@link IMachineModel} contract to the slots required by Minecraft's cube template.
	 * The model is north-facing: a directional machine can rotate this model through its blockstate,
	 * while a non-directional machine simply renders its front on the north face.
	 */
	private TextureMapping machineMapping (IMachineModel machine) {
		return machineMapping(machine, machine.getFrontTexture());
	}

	private TextureMapping machineMapping (IMachineModel machine, Identifier frontTexture) {
		var side = mat(machine.getSideTexture());
		return new TextureMapping().put(TextureSlot.DOWN, mat(machine.getBottomTexture())).put(TextureSlot.UP, mat(machine.getTopTexture())).put(TextureSlot.NORTH, mat(frontTexture)).put(TextureSlot.SOUTH, side).put(TextureSlot.EAST, side).put(TextureSlot.WEST, side).put(TextureSlot.PARTICLE, side);
	}

	/**
	 * Rotates the NORTH-facing base model to match {@link BlockStateProperties#FACING}. Mirrors the old applyRotation logic.
	 */
	private static PropertyDispatch<VariantMutator> facingDispatch () {
		return PropertyDispatch.modify(BlockStateProperties.FACING).select(Direction.DOWN, BlockModelGenerators.X_ROT_90).select(Direction.UP, BlockModelGenerators.X_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.EAST, BlockModelGenerators.Y_ROT_90);
	}

	private static Quadrant quadrant (int angle) {
		return switch (((angle % 360) + 360) % 360) {
			case 0 -> Quadrant.R0;
			case 90 -> Quadrant.R90;
			case 180 -> Quadrant.R180;
			case 270 -> Quadrant.R270;
			default -> throw new IllegalArgumentException("Invalid angle: " + angle);
		};
	}

	private static Material mat (Identifier texture) {
		return new Material(texture);
	}

}
