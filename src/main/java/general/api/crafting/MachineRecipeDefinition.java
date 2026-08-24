package general.api.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import general.api.transfer.fluid.FluidResourceHandler;
import general.api.transfer.item.ItemResourceHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Registered recipe type plus its immutable schema and generated serializers.
 *
 * <p>The four standard resource maps and positive {@code duration} field are
 * serialized automatically. A type may add strongly typed fields through a
 * payload {@link MapCodec} and matching stream codec when it is registered.
 * Payload map fields share the recipe object's root, so their names must not
 * collide with {@code item_inputs}, {@code item_outputs}, {@code fluid_inputs},
 * {@code fluid_outputs}, or {@code duration}.</p>
 */
public final class MachineRecipeDefinition<D> {

	private static final int MAX_RESOURCE_FIELDS = 256;

	private static final Codec<Map<String, SizedIngredient>>      ITEM_INPUTS_CODEC    = Codec.unboundedMap(Codec.STRING, SizedIngredient.NESTED_CODEC);
	private static final Codec<Map<String, ItemStackTemplate>>    ITEM_OUTPUTS_CODEC   = Codec.unboundedMap(Codec.STRING, ItemStackTemplate.CODEC);
	private static final Codec<Map<String, SizedFluidIngredient>> FLUID_INPUTS_CODEC   = Codec.unboundedMap(Codec.STRING, SizedFluidIngredient.CODEC);
	private static final Codec<Map<String, FluidStackTemplate>>   FLUID_OUTPUTS_CODEC  = Codec.unboundedMap(Codec.STRING, FluidStackTemplate.CODEC);
	private static final Codec<Double>                            OUTPUT_CHANCE_CODEC  = Codec.DOUBLE.validate(chance -> Double.isFinite(chance) && chance > 0.0D && chance <= 1.0D ? DataResult.success(chance) : DataResult.error(() -> "Output chance must be greater than 0 and at most 1: " + chance));
	private static final Codec<Map<String, Double>>               OUTPUT_CHANCES_CODEC = Codec.unboundedMap(Codec.STRING, OUTPUT_CHANCE_CODEC);

	private final Identifier                                                           id;
	private final MachineRecipeSchema                                                  schema;
	private final DeferredHolder<RecipeType<?>, RecipeType<MachineRecipe>>             type;
	private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MachineRecipe>> serializer;
	private final MapCodec<D>                                                          dataCodec;
	private final StreamCodec<RegistryFriendlyByteBuf, D>                              dataStreamCodec;
	private final D                                                                    defaultData;
	private final MachineRecipeMatcher<D>                                              additionalMatcher;
	private final MapCodec<MachineRecipe>                                              recipeCodec;
	private final StreamCodec<RegistryFriendlyByteBuf, MachineRecipe>                  recipeStreamCodec;
	private final List<Supplier<? extends ItemLike>>                                   craftingStations                = new ArrayList<>();
	private final List<MachineRecipeSources.SourceSpec>                                additionalSources               = new ArrayList<>();
	private final List<List<String>>                                                   interchangeableItemOutputGroups = new ArrayList<>();
	private final Set<String>                                                          interchangeableItemOutputNames  = new HashSet<>();

	MachineRecipeDefinition (Identifier id, MachineRecipeSchema schema, DeferredHolder<RecipeType<?>, RecipeType<MachineRecipe>> type, DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MachineRecipe>> serializer, MapCodec<D> dataCodec, StreamCodec<RegistryFriendlyByteBuf, D> dataStreamCodec, D defaultData, MachineRecipeMatcher<D> additionalMatcher) {
		this.id = Objects.requireNonNull(id, "id");
		this.schema = Objects.requireNonNull(schema, "schema");
		this.type = Objects.requireNonNull(type, "type");
		this.serializer = Objects.requireNonNull(serializer, "serializer");
		this.dataCodec = Objects.requireNonNull(dataCodec, "dataCodec");
		this.dataStreamCodec = Objects.requireNonNull(dataStreamCodec, "dataStreamCodec");
		this.defaultData = defaultData;
		this.additionalMatcher = Objects.requireNonNull(additionalMatcher, "additionalMatcher");
		this.recipeCodec = createRecipeCodec();
		this.recipeStreamCodec = createRecipeStreamCodec();
	}

	public Identifier id () {
		return id;
	}

	public MachineRecipeSchema schema () {
		return schema;
	}

	/**
	 * Translation key used for recipe viewers and other machine-type UI.
	 */
	public String descriptionId () {
		return Util.makeDescriptionId("recipe_type", id);
	}

	/**
	 * English fallback derived from the registered path.
	 */
	public String defaultEnglishName () {
		String[] words = id.getPath().replace('/', '_').split("_");
		var result = new StringBuilder();
		for (String word : words) {
			if (word.isEmpty()) continue;
			if (!result.isEmpty()) result.append(' ');
			result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
		}
		return result.toString();
	}

	/**
	 * Adds an item that performs this recipe type. Suppliers allow deferred
	 * blocks and items to be referenced while recipe types are bootstrapped.
	 */
	public MachineRecipeDefinition<D> craftingStation (Supplier<? extends ItemLike> station) {
		craftingStations.add(Objects.requireNonNull(station, "station"));
		return this;
	}

	/**
	 * Adds an already-available item that performs this recipe type.
	 */
	public MachineRecipeDefinition<D> craftingStation (ItemLike station) {
		Objects.requireNonNull(station, "station");
		return craftingStation(() -> station);
	}

	/**
	 * Resolves the crafting stations in declaration order.
	 */
	public List<ItemLike> craftingStations () {
		var result = new ArrayList<ItemLike>(craftingStations.size());
		for (Supplier<? extends ItemLike> supplier : craftingStations) {
			result.add(Objects.requireNonNull(supplier.get(), "Machine recipe crafting station supplier returned null for " + id));
		}
		return List.copyOf(result);
	}

	/**
	 * Imports a native cooking recipe type as a lower-priority machine recipe
	 * source. Registered recipes for this definition remain authoritative; the
	 * cooking source is considered only when none of them match.
	 *
	 * <p>The predicate is evaluated against the live machine input and can reserve
	 * optional slots, such as requiring an empty catalyst for a vanilla smelting
	 * fallback. The definition must have a default payload value.</p>
	 */
	public <T extends AbstractCookingRecipe> MachineRecipeDefinition<D> cookingRecipes (RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate) {
		if (defaultData == null) throw new IllegalStateException("Machine recipe type " + id + " requires a cooking recipe data factory");
		return cookingRecipes(type, inputSlot, outputSlot, predicate, recipe -> defaultData);
	}

	/**
	 * Imports a native cooking recipe type without an additional machine-input
	 * condition.
	 */
	public <T extends AbstractCookingRecipe> MachineRecipeDefinition<D> cookingRecipes (RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot) {
		return cookingRecipes(type, inputSlot, outputSlot, input -> true);
	}

	/**
	 * Custom-data variant of {@link #cookingRecipes(RecipeType, MachineRecipeSlot.ItemInput, MachineRecipeSlot.ItemOutput, Predicate)}.
	 */
	public <T extends AbstractCookingRecipe> MachineRecipeDefinition<D> cookingRecipes (RecipeType<T> type, MachineRecipeSlot.ItemInput inputSlot, MachineRecipeSlot.ItemOutput outputSlot, Predicate<MachineRecipeInput> predicate, Function<? super T, ? extends D> dataFactory) {
		additionalSources.add(MachineRecipeSources.cookingSpec(this, type, inputSlot, outputSlot, predicate, dataFactory));
		return this;
	}

	/**
	 * Declares logical item-output slots whose bound physical slots may accept any
	 * recipe output in the group. Placement is ordered but backtracks when
	 * necessary, and remains part of the recipe's atomic transfer transaction.
	 */
	public MachineRecipeDefinition<D> interchangeableItemOutputs (MachineRecipeSlot.ItemOutput first, MachineRecipeSlot.ItemOutput... remaining) {
		Objects.requireNonNull(first, "first");
		Objects.requireNonNull(remaining, "remaining");
		var group = new ArrayList<String>(remaining.length + 1);
		group.add(requireInterchangeableItemOutput(first));
		for (MachineRecipeSlot.ItemOutput output : remaining) group.add(requireInterchangeableItemOutput(output));
		if (group.size() < 2) throw new IllegalArgumentException("An interchangeable item output group requires at least two slots");
		if (new HashSet<>(group).size() != group.size()) throw new IllegalArgumentException("An interchangeable item output group cannot contain duplicate slots");
		for (String name : group) {
			if (interchangeableItemOutputNames.contains(name)) throw new IllegalArgumentException("Interchangeable item output slot '" + name + "' is already assigned to a group");
		}
		interchangeableItemOutputNames.addAll(group);
		interchangeableItemOutputGroups.add(List.copyOf(group));
		return this;
	}

	/**
	 * Creates fresh ordered recipe sources for one processor. Native imports are
	 * kept here so machine execution and recipe-viewer discovery cannot drift.
	 */
	public List<MachineRecipeSource> createRecipeSources () {
		var result = new ArrayList<MachineRecipeSource>(additionalSources.size() + 1);
		result.add(MachineRecipeSources.registered(this));
		for (MachineRecipeSources.SourceSpec source : additionalSources) result.add(source.create());
		return List.copyOf(result);
	}

	/**
	 * Adapts every synchronized native recipe imported by this definition for
	 * recipe viewers.
	 */
	public List<RecipeHolder<MachineRecipe>> importedRecipes (RecipeMap recipes) {
		Objects.requireNonNull(recipes, "recipes");
		var result = new ArrayList<RecipeHolder<MachineRecipe>>();
		for (MachineRecipeSources.SourceSpec source : additionalSources) result.addAll(source.recipes(recipes));
		return List.copyOf(result);
	}

	Set<RecipeType<?>> importedRecipeTypes () {
		var result = Collections.newSetFromMap(new IdentityHashMap<RecipeType<?>, Boolean>());
		for (MachineRecipeSources.SourceSpec source : additionalSources) result.add(source.nativeType());
		return Set.copyOf(result);
	}

	List<List<String>> interchangeableItemOutputGroups () {
		return List.copyOf(interchangeableItemOutputGroups);
	}

	public RecipeType<MachineRecipe> type () {
		return type.get();
	}

	public RecipeSerializer<MachineRecipe> serializer () {
		return serializer.get();
	}

	public MapCodec<MachineRecipe> codec () {
		return recipeCodec;
	}

	public StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> streamCodec () {
		return recipeStreamCodec;
	}

	/**
	 * Starts a validated programmatic/datagen recipe builder. Types registered
	 * without a default payload must call {@link MachineRecipeBuilder#data(Object)}.
	 */
	public MachineRecipeBuilder<D> recipeBuilder () {
		return new MachineRecipeBuilder<>(this, defaultData);
	}

	/**
	 * Creates a same-name binding to both definition-backed handlers.
	 */
	public MachineRecipeBinding bind (ItemResourceHandler items, FluidResourceHandler fluids) {
		return bindingBuilder().items(items).fluids(fluids).build();
	}

	public MachineRecipeBinding bind (ItemResourceHandler items) {
		return bindingBuilder().items(items).build();
	}

	public MachineRecipeBinding bind (FluidResourceHandler fluids) {
		return bindingBuilder().fluids(fluids).build();
	}

	public MachineRecipeProcessor processor (ItemResourceHandler items, FluidResourceHandler fluids, Runnable changeCallback) {
		return bind(items, fluids).processor(changeCallback);
	}

	public MachineRecipeProcessor processor (ItemResourceHandler items, FluidResourceHandler fluids, MachineWorkRequirement workRequirement, Runnable changeCallback) {
		return bind(items, fluids).processor(workRequirement, changeCallback);
	}

	public MachineRecipeProcessor processor (ItemResourceHandler items, Runnable changeCallback) {
		return bind(items).processor(changeCallback);
	}

	public MachineRecipeProcessor processor (ItemResourceHandler items, MachineWorkRequirement workRequirement, Runnable changeCallback) {
		return bind(items).processor(workRequirement, changeCallback);
	}

	public MachineRecipeProcessor processor (FluidResourceHandler fluids, Runnable changeCallback) {
		return bind(fluids).processor(changeCallback);
	}

	public MachineRecipeProcessor processor (FluidResourceHandler fluids, MachineWorkRequirement workRequirement, Runnable changeCallback) {
		return bind(fluids).processor(workRequirement, changeCallback);
	}

	/**
	 * Creates a binding builder for custom logical-to-physical slot mappings.
	 */
	public MachineRecipeBinding.Builder bindingBuilder () {
		return new MachineRecipeBinding.Builder(this);
	}

	/**
	 * Retrieves this definition's strongly typed custom data and rejects recipes
	 * belonging to another registered definition.
	 */
	public D data (MachineRecipe recipe) {
		requireOwner(recipe);
		return castData(recipe.dataValue());
	}

	boolean matchesAdditional (MachineRecipe recipe, MachineRecipeInput input, Level level) {
		return additionalMatcher.matches(data(recipe), input, level);
	}

	MachineRecipe create (Map<String, SizedIngredient> itemInputs, Map<String, ItemStackTemplate> itemOutputs, Map<String, Double> itemOutputChances, Map<String, SizedFluidIngredient> fluidInputs, Map<String, FluidStackTemplate> fluidOutputs, Map<String, Double> fluidOutputChances, int duration, D data) {
		if (duration <= 0) throw new IllegalArgumentException("Machine recipe duration must be positive: " + duration);
		Objects.requireNonNull(data, "Machine recipe data is required for type " + id);

		Map<String, SizedIngredient> checkedItemInputs = validateFields("item input", schema.itemInputs(), itemInputs);
		Map<String, ItemStackTemplate> checkedItemOutputs = validateFields("item output", schema.itemOutputs(), itemOutputs);
		Map<String, SizedFluidIngredient> checkedFluidInputs = validateFields("fluid input", schema.fluidInputs(), fluidInputs);
		Map<String, FluidStackTemplate> checkedFluidOutputs = validateFields("fluid output", schema.fluidOutputs(), fluidOutputs);
		Map<String, Double> checkedItemOutputChances = validateOutputChances("item", checkedItemOutputs.keySet(), itemOutputChances);
		Map<String, Double> checkedFluidOutputChances = validateOutputChances("fluid", checkedFluidOutputs.keySet(), fluidOutputChances);

		// Ingredient constructors/codecs validate directly resolvable empty sets. Do not enumerate
		// values here: named holder sets cannot be dereferenced while datagen recipes are constructed.
		for (var entry : checkedItemInputs.entrySet()) {
			SizedIngredient ingredient = Objects.requireNonNull(entry.getValue(), "Item ingredient '" + entry.getKey() + "'");
			Objects.requireNonNull(ingredient.ingredient(), "Item input '" + entry.getKey() + "' ingredient");
		}
		for (var entry : checkedItemOutputs.entrySet()) {
			ItemStackTemplate template = Objects.requireNonNull(entry.getValue(), "Item output '" + entry.getKey() + "'");
			if (template.count() <= 0) throw new IllegalArgumentException("Item output '" + entry.getKey() + "' must be non-empty");
		}
		for (var entry : checkedFluidInputs.entrySet()) {
			SizedFluidIngredient ingredient = Objects.requireNonNull(entry.getValue(), "Fluid ingredient '" + entry.getKey() + "'");
			Objects.requireNonNull(ingredient.ingredient(), "Fluid input '" + entry.getKey() + "' ingredient");
		}
		for (var entry : checkedFluidOutputs.entrySet()) {
			FluidStackTemplate template = Objects.requireNonNull(entry.getValue(), "Fluid output '" + entry.getKey() + "'");
			if (template.amount() <= 0) throw new IllegalArgumentException("Fluid output '" + entry.getKey() + "' must be non-empty");
		}

		return new MachineRecipe(this, checkedItemInputs, checkedItemOutputs, checkedItemOutputChances, checkedFluidInputs, checkedFluidOutputs, checkedFluidOutputChances, duration, data);
	}

	private MapCodec<MachineRecipe> createRecipeCodec () {
		MapCodec<Serialized<D>> serializedCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(ITEM_INPUTS_CODEC.optionalFieldOf("item_inputs", Map.of()).forGetter(Serialized<D>::itemInputs), ITEM_OUTPUTS_CODEC.optionalFieldOf("item_outputs", Map.of()).forGetter(Serialized<D>::itemOutputs), OUTPUT_CHANCES_CODEC.optionalFieldOf("item_output_chances", Map.of()).forGetter(Serialized<D>::itemOutputChances), FLUID_INPUTS_CODEC.optionalFieldOf("fluid_inputs", Map.of()).forGetter(Serialized<D>::fluidInputs), FLUID_OUTPUTS_CODEC.optionalFieldOf("fluid_outputs", Map.of()).forGetter(Serialized<D>::fluidOutputs), OUTPUT_CHANCES_CODEC.optionalFieldOf("fluid_output_chances", Map.of()).forGetter(Serialized<D>::fluidOutputChances), ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(Serialized<D>::duration), dataCodec.forGetter(Serialized<D>::data)).apply(instance, Serialized::new));

		return serializedCodec.flatXmap(serialized -> {
			try {
				return DataResult.success(create(serialized.itemInputs(), serialized.itemOutputs(), serialized.itemOutputChances(), serialized.fluidInputs(), serialized.fluidOutputs(), serialized.fluidOutputChances(), serialized.duration(), serialized.data()));
			} catch (IllegalArgumentException exception) {
				return DataResult.error(exception::getMessage);
			}
		}, recipe -> {
			if (recipe.definition() != this) {
				return DataResult.error(() -> "Cannot encode recipe for " + recipe.definition().id() + " with serializer " + id);
			}
			return DataResult.success(serialized(recipe));
		});
	}

	private StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> createRecipeStreamCodec () {
		return new StreamCodec<>() {
			@Override
			public MachineRecipe decode (RegistryFriendlyByteBuf buffer) {
				Map<String, SizedIngredient> itemInputs = readMap(buffer, SizedIngredient.STREAM_CODEC);
				Map<String, ItemStackTemplate> itemOutputs = readMap(buffer, ItemStackTemplate.STREAM_CODEC);
				Map<String, Double> itemOutputChances = readDoubleMap(buffer);
				Map<String, SizedFluidIngredient> fluidInputs = readMap(buffer, SizedFluidIngredient.STREAM_CODEC);
				Map<String, FluidStackTemplate> fluidOutputs = readMap(buffer, FluidStackTemplate.STREAM_CODEC);
				Map<String, Double> fluidOutputChances = readDoubleMap(buffer);
				int duration = buffer.readVarInt();
				D data = dataStreamCodec.decode(buffer);
				return create(itemInputs, itemOutputs, itemOutputChances, fluidInputs, fluidOutputs, fluidOutputChances, duration, data);
			}

			@Override
			public void encode (RegistryFriendlyByteBuf buffer, MachineRecipe recipe) {
				requireOwner(recipe);
				writeMap(buffer, recipe.itemInputs(), SizedIngredient.STREAM_CODEC);
				writeMap(buffer, internalItemOutputs(recipe), ItemStackTemplate.STREAM_CODEC);
				writeDoubleMap(buffer, recipe.internalItemOutputChances());
				writeMap(buffer, recipe.fluidInputs(), SizedFluidIngredient.STREAM_CODEC);
				writeMap(buffer, internalFluidOutputs(recipe), FluidStackTemplate.STREAM_CODEC);
				writeDoubleMap(buffer, recipe.internalFluidOutputChances());
				buffer.writeVarInt(recipe.duration());
				dataStreamCodec.encode(buffer, data(recipe));
			}
		};
	}

	private Serialized<D> serialized (MachineRecipe recipe) {
		return new Serialized<>(recipe.itemInputs(), internalItemOutputs(recipe), recipe.internalItemOutputChances(), recipe.fluidInputs(), internalFluidOutputs(recipe), recipe.internalFluidOutputChances(), recipe.duration(), data(recipe));
	}

	private Map<String, ItemStackTemplate> internalItemOutputs (MachineRecipe recipe) {
		var result = new LinkedHashMap<String, ItemStackTemplate>();
		for (MachineRecipeSchema.Slot slot : schema.itemOutputs()) {
			ItemStackTemplate template = recipe.internalItemOutputTemplate(slot.name());
			if (template != null) result.put(slot.name(), template);
		}
		return result;
	}

	private Map<String, FluidStackTemplate> internalFluidOutputs (MachineRecipe recipe) {
		var result = new LinkedHashMap<String, FluidStackTemplate>();
		for (MachineRecipeSchema.Slot slot : schema.fluidOutputs()) {
			FluidStackTemplate template = recipe.internalFluidOutputTemplate(slot.name());
			if (template != null) result.put(slot.name(), template);
		}
		return result;
	}

	private void requireOwner (MachineRecipe recipe) {
		Objects.requireNonNull(recipe, "recipe");
		if (recipe.definition() != this) {
			throw new IllegalArgumentException("Recipe belongs to " + recipe.definition().id() + ", not " + id);
		}
	}

	private String requireInterchangeableItemOutput (MachineRecipeSlot.ItemOutput output) {
		Objects.requireNonNull(output, "output");
		String name = output.name();
		if (schema.itemOutputs().stream().noneMatch(slot -> slot.name().equals(name))) {
			throw new IllegalArgumentException("Interchangeable item output slot '" + name + "' is not declared as an item output by " + id);
		}
		return name;
	}

	@SuppressWarnings("unchecked")
	private D castData (Object data) {
		return (D) data;
	}

	private static <V> Map<String, V> validateFields (String description, java.util.List<MachineRecipeSchema.Slot> slots, Map<String, V> supplied) {
		Objects.requireNonNull(supplied, description + " fields");
		Set<String> expected = new LinkedHashSet<>();
		for (MachineRecipeSchema.Slot slot : slots) expected.add(slot.name());
		for (String name : supplied.keySet()) {
			if (!expected.contains(name)) throw new IllegalArgumentException("Unknown machine recipe " + description + " slot '" + name + "'");
		}
		var ordered = new LinkedHashMap<String, V>();
		for (MachineRecipeSchema.Slot slot : slots) {
			V value = supplied.get(slot.name());
			if (value == null) {
				if (slot.required()) throw new IllegalArgumentException("Missing required machine recipe " + description + " slot '" + slot.name() + "'");
			} else {
				ordered.put(slot.name(), value);
			}
		}
		return ordered;
	}

	private static <V> Map<String, V> readMap (RegistryFriendlyByteBuf buffer, StreamCodec<RegistryFriendlyByteBuf, V> valueCodec) {
		int size = buffer.readVarInt();
		if (size < 0 || size > MAX_RESOURCE_FIELDS) throw new IllegalArgumentException("Invalid machine recipe resource field count: " + size);
		var result = new LinkedHashMap<String, V>(size);
		for (int index = 0; index < size; index++) {
			String name = buffer.readUtf(256);
			V previous = result.put(name, valueCodec.decode(buffer));
			if (previous != null) throw new IllegalArgumentException("Duplicate machine recipe resource field '" + name + "'");
		}
		return result;
	}

	private static <V> void writeMap (RegistryFriendlyByteBuf buffer, Map<String, V> values, StreamCodec<RegistryFriendlyByteBuf, V> valueCodec) {
		if (values.size() > MAX_RESOURCE_FIELDS) throw new IllegalArgumentException("Too many machine recipe resource fields: " + values.size());
		buffer.writeVarInt(values.size());
		for (var entry : values.entrySet()) {
			buffer.writeUtf(entry.getKey(), 256);
			valueCodec.encode(buffer, entry.getValue());
		}
	}

	private static Map<String, Double> readDoubleMap (RegistryFriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		if (size < 0 || size > MAX_RESOURCE_FIELDS) throw new IllegalArgumentException("Invalid machine recipe chance field count: " + size);
		var result = new LinkedHashMap<String, Double>(size);
		for (int index = 0; index < size; index++) {
			String name = buffer.readUtf(256);
			double chance = buffer.readDouble();
			if (!Double.isFinite(chance) || chance <= 0.0D || chance > 1.0D) throw new IllegalArgumentException("Invalid machine recipe output chance for '" + name + "': " + chance);
			if (result.put(name, chance) != null) throw new IllegalArgumentException("Duplicate machine recipe output chance field '" + name + "'");
		}
		return result;
	}

	private static void writeDoubleMap (RegistryFriendlyByteBuf buffer, Map<String, Double> values) {
		if (values.size() > MAX_RESOURCE_FIELDS) throw new IllegalArgumentException("Too many machine recipe chance fields: " + values.size());
		buffer.writeVarInt(values.size());
		for (var entry : values.entrySet()) {
			buffer.writeUtf(entry.getKey(), 256);
			buffer.writeDouble(entry.getValue());
		}
	}

	private static Map<String, Double> validateOutputChances (String resourceName, Set<String> outputs, Map<String, Double> supplied) {
		Objects.requireNonNull(supplied, resourceName + " output chances");
		var checked = new LinkedHashMap<String, Double>();
		for (var entry : supplied.entrySet()) {
			if (!outputs.contains(entry.getKey())) throw new IllegalArgumentException("Chance declared for missing machine recipe " + resourceName + " output '" + entry.getKey() + "'");
			double chance = Objects.requireNonNull(entry.getValue(), resourceName + " output chance '" + entry.getKey() + "'");
			if (!Double.isFinite(chance) || chance <= 0.0D || chance > 1.0D) throw new IllegalArgumentException("Machine recipe " + resourceName + " output chance must be greater than 0 and at most 1: " + chance);
			checked.put(entry.getKey(), chance);
		}
		return Map.copyOf(checked);
	}

	private record Serialized<D>(Map<String, SizedIngredient> itemInputs, Map<String, ItemStackTemplate> itemOutputs, Map<String, Double> itemOutputChances, Map<String, SizedFluidIngredient> fluidInputs,
	                             Map<String, FluidStackTemplate> fluidOutputs, Map<String, Double> fluidOutputChances, int duration, D data) {
	}
}
