package crypt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Object that use annotation CryptCryptAnnotation on fields can be crypted by the Parser.
 * @author radoua abderrahim
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptCryptAnnotation {
	
	/**
	 * 
	 */
	public boolean isEncryptKeyPublic() default true;
	
	
	/*
	 * specify that the decrypt key is public or private key
	 * @return Boolean  
	 */
	//public boolean isDecryptKeyPublic() default true;
	
	
	/**
	 * get the field name that contain the key to encrypt data
	 * @return String name of field
	 */
	public String secondKey() default "pbkey";
	
	/**
	 * 
	 * @return Boolean  
	 */
	public boolean isCryptBySecondKey() default false;
	
}
