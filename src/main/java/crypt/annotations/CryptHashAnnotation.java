package crypt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Object that use annotation CryptHashAnnotation on fields can be hashed by the Parser.
 * Example : 
 * <p>
 * 		@CryptHashAnnotation
 * 		FieldType fieldname;
 * </p>
 * 
 * fieldname will be hashed by parser. 
 * 
 * @author radoua abderrahim
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptHashAnnotation {
	
}
