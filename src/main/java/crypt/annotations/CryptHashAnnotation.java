package crypt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Object that use annotation CryptHashAnnotation on fields can be hashed by the Parser.
 * Example :
 * <p>
 *
 * @author radoua abderrahim
 * @CryptHashAnnotation FieldType fieldname;
 * </p>
 * <p>
 * fieldname will be hashed by parser.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptHashAnnotation {

}
