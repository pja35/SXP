package network.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define an attribute to be key/value of an {@link Advertisement}
 * @author Julien Prudhomme
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface AdvertisementAttribute {
	/**
	 * The attribute is enabled and is part of the final advertisement
	 * @return
	 */
	public boolean enabled() default true;
	
	/**
	 * The attribute is indexed
	 * @return
	 */
	public boolean indexed() default false;
	
	/**
	 * The attribute participate for the signature
	 * @return
	 */
	public boolean signable() default true;
}
