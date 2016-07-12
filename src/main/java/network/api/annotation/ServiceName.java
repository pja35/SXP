package network.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
/**
 * Define which service use an advertisement
 * @author Julien Prudhomme
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceName {
	String name();
}
