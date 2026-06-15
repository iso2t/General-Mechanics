package general.api.formula.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import general.api.formula.GenFormula;
import general.api.formula.core.*;
import general.api.formula.core.FormulaPart.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;

import java.util.*;
import java.util.function.Function;

/**
 * Codecs for the Formula API model. Reference fields (category, traits, atoms, compounds, materials)
 * resolve against their datapack registries via {@link RegistryFixedCodec}, so JSON refers to other
 * entries by id and the same codecs drive both data load and client sync.
 */
public final class FormulaCodecs {

	private FormulaCodecs () {
	}

	public static final Codec<Holder<Category>> CATEGORY_REF = RegistryFixedCodec.create(GenFormula.CATEGORY_REGISTRY);
	public static final Codec<Holder<Trait>>    TRAIT_REF    = RegistryFixedCodec.create(GenFormula.TRAIT_REGISTRY);
	public static final Codec<Holder<Atom>>     ATOM_REF     = RegistryFixedCodec.create(GenFormula.ATOM_REGISTRY);
	public static final Codec<Holder<Compound>> COMPOUND_REF = RegistryFixedCodec.create(GenFormula.COMPOUND_REGISTRY);
	public static final Codec<Holder<Material>> MATERIAL_REF = RegistryFixedCodec.create(GenFormula.MATERIAL_REGISTRY);

	private static final Codec<Set<Holder<Trait>>> TRAIT_SET = TRAIT_REF.listOf().xmap(LinkedHashSet::new, List::copyOf);

	public static final Codec<FormulaPart> PART = Codec.recursive("general.api.formula.FormulaPart", self -> {
		Codec<Formula> formula = self.listOf().xmap(Formula::new, Formula::parts);

		MapCodec<AtomPart> atom = RecordCodecBuilder.mapCodec(i -> i.group(ATOM_REF.fieldOf("atom").forGetter(AtomPart::atom), count(AtomPart::count)).apply(i, AtomPart::new));

		MapCodec<CompoundPart> compound = RecordCodecBuilder.mapCodec(i -> i.group(COMPOUND_REF.fieldOf("compound").forGetter(CompoundPart::compound), count(CompoundPart::count)).apply(i, CompoundPart::new));

		MapCodec<MaterialPart> material = RecordCodecBuilder.mapCodec(i -> i.group(MATERIAL_REF.fieldOf("material").forGetter(MaterialPart::material), count(MaterialPart::count)).apply(i, MaterialPart::new));

		MapCodec<FictionalPart> fictional = RecordCodecBuilder.mapCodec(i -> i.group(Codec.STRING.fieldOf("symbol").forGetter(FictionalPart::symbol), count(FictionalPart::count)).apply(i, FictionalPart::new));

		MapCodec<GroupPart> group = RecordCodecBuilder.mapCodec(i -> i.group(formula.fieldOf("group").forGetter(GroupPart::group), count(GroupPart::count)).apply(i, GroupPart::new));

		return Codec.STRING.dispatch("type", FormulaCodecs::typeOf, type -> switch (type) {
			case "atom" -> atom;
			case "compound" -> compound;
			case "material" -> material;
			case "fictional" -> fictional;
			case "group" -> group;
			default -> throw new IllegalArgumentException("Unknown formula part type: " + type);
		});
	});

	public static final Codec<Formula> FORMULA = PART.listOf().xmap(Formula::new, Formula::parts);

	public static final Codec<Category> CATEGORY = RecordCodecBuilder.create(i -> i.group(Codec.STRING.fieldOf("name").forGetter(Category::name), Codec.INT.optionalFieldOf("color", Category.DEFAULT_COLOR).forGetter(Category::color), CATEGORY_REF.optionalFieldOf("parent").forGetter(Category::parent)).apply(i, Category::new));

	public static final Codec<Trait> TRAIT = RecordCodecBuilder.create(i -> i.group(Codec.STRING.fieldOf("name").forGetter(Trait::name), Codec.INT.optionalFieldOf("color", Trait.DEFAULT_COLOR).forGetter(Trait::color), Codec.STRING.optionalFieldOf("description").forGetter(Trait::description)).apply(i, Trait::new));

	public static final Codec<Atom> ATOM = RecordCodecBuilder.create(i -> i.group(Codec.STRING.fieldOf("symbol").forGetter(Atom::symbol), Codec.STRING.fieldOf("name").forGetter(Atom::name), CATEGORY_REF.fieldOf("category").forGetter(Atom::category), Codec.INT.optionalFieldOf("atomic_number").forGetter(a -> box(a.atomicNumber())), Codec.DOUBLE.optionalFieldOf("atomic_mass").forGetter(a -> box(a.mass())), Codec.INT.optionalFieldOf("color", Atom.DEFAULT_COLOR).forGetter(Atom::color), Codec.BOOL.optionalFieldOf("fictional", false).forGetter(Atom::fictional), TRAIT_SET.optionalFieldOf("traits", Set.of()).forGetter(Atom::traits)).apply(i, (symbol, name, category, atomicNumber, mass, color, fictional, traits) -> new Atom(symbol, name, category, unboxInt(atomicNumber), unboxDouble(mass), color, fictional, traits)));

	public static final Codec<Compound> COMPOUND = RecordCodecBuilder.create(i -> i.group(Codec.STRING.fieldOf("name").forGetter(Compound::name), FORMULA.fieldOf("formula").forGetter(Compound::formula), CATEGORY_REF.optionalFieldOf("category").forGetter(Compound::category), TRAIT_SET.optionalFieldOf("traits", Set.of()).forGetter(Compound::traits), Codec.BOOL.optionalFieldOf("fictional", false).forGetter(Compound::fictional)).apply(i, Compound::new));

	public static final Codec<MaterialProperties> MATERIAL_PROPERTIES = RecordCodecBuilder.create(i -> i.group(Codec.DOUBLE.optionalFieldOf("density").forGetter(p -> box(p.density())), Codec.DOUBLE.optionalFieldOf("hardness").forGetter(p -> box(p.hardness())), Codec.DOUBLE.optionalFieldOf("conductivity").forGetter(p -> box(p.conductivity())), Codec.DOUBLE.optionalFieldOf("reactivity").forGetter(p -> box(p.reactivity()))).apply(i, (density, hardness, conductivity, reactivity) -> new MaterialProperties(unboxDouble(density), unboxDouble(hardness), unboxDouble(conductivity), unboxDouble(reactivity))));

	public static final Codec<Material> MATERIAL = RecordCodecBuilder.create(i -> i.group(Codec.STRING.fieldOf("name").forGetter(Material::name), CATEGORY_REF.fieldOf("category").forGetter(Material::category), FORMULA.fieldOf("formula").forGetter(Material::formula), PART.listOf().optionalFieldOf("components", List.of()).forGetter(Material::components), TRAIT_SET.optionalFieldOf("traits", Set.of()).forGetter(Material::traits), Codec.INT.optionalFieldOf("color", Material.DEFAULT_COLOR).forGetter(Material::color), MATERIAL_PROPERTIES.optionalFieldOf("properties", MaterialProperties.EMPTY).forGetter(Material::properties), Codec.BOOL.optionalFieldOf("fictional", false).forGetter(Material::fictional), ItemForm.CODEC.listOf().<Set<ItemForm>>xmap(LinkedHashSet::new, List::copyOf).optionalFieldOf("default_forms", Set.of()).forGetter(Material::defaultForms)).apply(i, Material::new));

	/**
	 * A {@code "count"} int field (defaulting to {@code 1}) bound to a part's count getter.
	 */
	private static <P extends FormulaPart> RecordCodecBuilder<P, Integer> count (Function<P, Integer> getter) {
		return Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(getter);
	}

	private static String typeOf (FormulaPart part) {
		return switch (part) {
			case AtomPart ignored -> "atom";
			case CompoundPart ignored -> "compound";
			case MaterialPart ignored -> "material";
			case FictionalPart ignored -> "fictional";
			case GroupPart ignored -> "group";
		};
	}

	private static Optional<Integer> box (OptionalInt value) {
		return value.isPresent() ? Optional.of(value.getAsInt()) : Optional.empty();
	}

	private static Optional<Double> box (OptionalDouble value) {
		return value.isPresent() ? Optional.of(value.getAsDouble()) : Optional.empty();
	}

	private static OptionalInt unboxInt (Optional<Integer> value) {
		return value.map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	private static OptionalDouble unboxDouble (Optional<Double> value) {
		return value.map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
	}
}
