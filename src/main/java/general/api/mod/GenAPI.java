package general.api.mod;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class GenAPI {

	private static final Type GENERAL_MOD_TYPE = Type.getType(GeneralMod.class);

	private static volatile Map<String, String> registry;

	private GenAPI () {
	}

	/**
	 * Resolves the mod id of the calling mod by walking the stack to find the first frame outside
	 * the GeneralAPI itself, then mapping that class to its owning {@code @GeneralMod} mod.
	 */
	public static String getModId () {
		return getModId(callerClass());
	}

	/**
	 * Resolves the mod id of the mod that owns the given class. The class must belong to a mod
	 * whose class set contains a class annotated with {@link GeneralMod}.
	 */
	public static String getModId (Class<?> owner) {
		String modId = registry().get(owner.getName());
		if (modId == null) {
			throw new IllegalStateException("No @GeneralMod mod owns " + owner.getName());
		}
		return modId;
	}

	private static Class<?> callerClass () {
		return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames -> frames.map(StackWalker.StackFrame::getDeclaringClass).filter(c -> !c.getName().startsWith("general.api.")).findFirst()).orElseThrow(() -> new IllegalStateException("Could not determine the calling mod for getModId()"));
	}

	private static Map<String, String> registry () {
		Map<String, String> local = registry;
		if (local == null) {
			synchronized (GenAPI.class) {
				local = registry;
				if (local == null) {
					local = buildRegistry();
					registry = local;
				}
			}
		}
		return local;
	}

	private static Map<String, String> buildRegistry () {
		Map<String, String> map = new HashMap<>();
		for (ModFileScanData scanData : ModList.get().getAllScanData()) {
			String modId = findModId(scanData);
			if (modId == null) {
				continue;
			}
			for (ModFileScanData.ClassData classData : scanData.getClasses()) {
				map.put(classData.clazz().getClassName(), modId);
			}
		}
		return map;
	}

	private static String findModId (ModFileScanData scanData) {
		for (ModFileScanData.AnnotationData annotation : scanData.getAnnotations()) {
			if (GENERAL_MOD_TYPE.equals(annotation.annotationType())) {
				Object value = annotation.annotationData().get("value");
				if (value instanceof String s) {
					return s;
				}
			}
		}
		return null;
	}

	public static boolean isModLoaded (String modId) {
		return ModList.get().isLoaded(modId);
	}

	public static boolean isDevelopmentEnvironment () {
		return !FMLEnvironment.isProduction();
	}

}
