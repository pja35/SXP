package network.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare a field that is a message field
 * @author Julien Prudhomme
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface MessageElement {
	/**
	 * Name of the field
	 * @return
	 */
	public String value(); 
}
