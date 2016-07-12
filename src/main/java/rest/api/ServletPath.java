package rest.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Define the route on the Servlet will be accessible
 * @author Julien Prudhomme
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ServletPath {
	String value();
}
