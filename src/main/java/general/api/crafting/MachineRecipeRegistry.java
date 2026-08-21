package general.api.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Paired recipe-type and serializer registry for declarative machine recipes.
 *
 * <p>Definitions must be added before {@link #register(IEventBus)} attaches the
 * two deferred registries. A mod normally owns one instance and may declare its
 * machine definitions in any registry class. Every definition is also published
 * to {@link MachineRecipeCatalog} for cross-mod runtime discovery.</p>
 */
public final class MachineRecipeRegistry {

	private final String                                modId;
	private final DeferredRegister<RecipeType<?>>       recipeTypes;
	private final DeferredRegister<RecipeSerializer<?>> serializers;
	private final List<MachineRecipeDefinition<?>>      definitions = new ArrayList<>();
	private       boolean                               attached;

	private MachineRecipeRegistry (String modId) {
		this.modId = Objects.requireNonNull(modId, "modId");
		this.recipeTypes = DeferredRegister.create(Registries.RECIPE_TYPE, modId);
		this.serializers = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modId);
	}

	public static MachineRecipeRegistry create (String modId) {
		return new MachineRecipeRegistry(modId);
	}

	/**
	 * Registers a type with no custom payload fields.
	 */
	public MachineRecipeDefinition<NoRecipeData> register (String name, MachineRecipeSchema schema) {
		return register(name, schema, MapCodec.unit(NoRecipeData.INSTANCE), StreamCodec.unit(NoRecipeData.INSTANCE), NoRecipeData.INSTANCE, (data, input, level) -> true);
	}

	/**
	 * Registers a type with required custom payload data. Programmatic recipe
	 * builders must set that data explicitly.
	 */
	public <D> MachineRecipeDefinition<D> register (String name, MachineRecipeSchema schema, MapCodec<D> dataCodec, StreamCodec<RegistryFriendlyByteBuf, D> dataStreamCodec) {
		return register(name, schema, dataCodec, dataStreamCodec, null, (data, input, level) -> true);
	}

	/**
	 * Registers a custom-data type whose programmatic builders begin with the
	 * supplied default value.
	 */
	public <D> MachineRecipeDefinition<D> register (String name, MachineRecipeSchema schema, MapCodec<D> dataCodec, StreamCodec<RegistryFriendlyByteBuf, D> dataStreamCodec, D defaultData) {
		return register(name, schema, dataCodec, dataStreamCodec, Objects.requireNonNull(defaultData, "defaultData"), (data, input, level) -> true);
	}

	/**
	 * Registers a custom-data type with an additional matching predicate and no
	 * default data value for programmatic builders.
	 */
	public <D> MachineRecipeDefinition<D> register (String name, MachineRecipeSchema schema, MapCodec<D> dataCodec, StreamCodec<RegistryFriendlyByteBuf, D> dataStreamCodec, MachineRecipeMatcher<D> additionalMatcher) {
		return register(name, schema, dataCodec, dataStreamCodec, null, additionalMatcher);
	}

	/**
	 * Registers a type with a default payload for programmatic builders and an
	 * optional type-specific predicate evaluated after resource matching.
	 */
	public <D> MachineRecipeDefinition<D> register (String name, MachineRecipeSchema schema, MapCodec<D> dataCodec, StreamCodec<RegistryFriendlyByteBuf, D> dataStreamCodec, D defaultData, MachineRecipeMatcher<D> additionalMatcher) {
		if (attached) throw new IllegalStateException("Cannot add machine recipe type '" + name + "' after registries have been attached");
		Objects.requireNonNull(name, "name");
		if (name.isBlank()) throw new IllegalArgumentException("Machine recipe type name must not be blank");
		Identifier id = Identifier.tryBuild(modId, name);
		if (id == null) throw new IllegalArgumentException("Invalid machine recipe type identifier '" + modId + ":" + name + "'");

		var definitionReference = new AtomicReference<MachineRecipeDefinition<D>>();
		var type = recipeTypes.<RecipeType<MachineRecipe>>register(name, () -> RecipeType.simple(id));
		var serializer = serializers.<RecipeSerializer<MachineRecipe>>register(name, () -> {
			MachineRecipeDefinition<D> definition = definitionReference.get();
			if (definition == null) throw new IllegalStateException("Machine recipe definition " + id + " was requested before construction completed");
			return new RecipeSerializer<>(definition.codec(), definition.streamCodec());
		});
		var definition = new MachineRecipeDefinition<>(id, Objects.requireNonNull(schema, "schema"), type, serializer, Objects.requireNonNull(dataCodec, "dataCodec"), Objects.requireNonNull(dataStreamCodec, "dataStreamCodec"), defaultData, Objects.requireNonNull(additionalMatcher, "additionalMatcher"));
		definitionReference.set(definition);
		definitions.add(definition);
		MachineRecipeCatalog.add(definition);
		return definition;
	}

	/**
	 * Attaches both vanilla registries to the mod event bus exactly once.
	 */
	public void register (IEventBus bus) {
		Objects.requireNonNull(bus, "bus");
		if (attached) throw new IllegalStateException("Machine recipe registries for " + modId + " are already attached");
		attached = true;
		recipeTypes.register(bus);
		serializers.register(bus);
	}

	public List<MachineRecipeDefinition<?>> definitions () {
		return List.copyOf(definitions);
	}
}
