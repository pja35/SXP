package crypt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Using annotation CryptSigneAnnotation on field to generate signature, also to check the signature for that object.
 * @author radoua abderrahim
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptSigneAnnotation {
	
	/*
	 * return the name of attribute that contain the Id of user that own the Object.
	 * @return String : name of attribute
	 */
	//public String ownerAttribute();
	
	/**
	 * The name of field that has the key.
	 * @return String
	 */
	public String checkByKey() default "keys";
	
	/**
	 * Generating a signature from fields
	 * Example:
	 * <p>
	 *  	of using signeWithField in Entity :
	 * 		@CryptSigneAnnotation(signeWithFields={"field1","field2"})
	 * 		FieldType signatureField;
	 * 
	 * </p>
	 * @return
	 */
	public String [] signeWithFields();
}
