package general.api.mod;

import net.neoforged.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GeneralMod {

	String value();

	Dist[] dist() default { Dist.CLIENT, Dist.DEDICATED_SERVER };

	String[] depends() default {};

}
