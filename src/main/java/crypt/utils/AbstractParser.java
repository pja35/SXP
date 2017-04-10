package crypt.utils;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Hashtable;
import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptHashAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.HasherFactory;
import model.entity.ElGamalKey;
import model.entity.User;

/**
 * Abstract class that parse Entity fields for each annotation, and prepare fields for parser Action implemented in its child class.  
 *  
 * @author radoua abderrahim
 * @param <Entity> Type of Object generic type. 
 */
public abstract class AbstractParser<Entity> implements ParserAnnotation<Entity>{
	
	private User user;
	
	private Entity entity;
	private byte[] salt;
	
	private Hashtable<Field,CryptCryptAnnotation> fieldsToCrypt;
	private Hashtable<Field,CryptHashAnnotation> fieldsToHash;
	private Hashtable<Field,CryptSigneAnnotation> fieldsToSign;
	
	
	/**
	 * Constructor
	 * @param entity
	 */
	public AbstractParser(Entity entity){
		this.entity = entity;
		fieldsToCrypt = new Hashtable<>();
		fieldsToHash = new Hashtable<>();
		fieldsToSign = new Hashtable<>();
		init();
	}
	
	/**
	 * Constructor
	 * @param entity
	 */
	public AbstractParser(Entity entity,User user){
		this.entity = entity;
		fieldsToCrypt = new Hashtable<>();
		fieldsToHash = new Hashtable<>();
		fieldsToSign = new Hashtable<>();
		init();
	}
	
	
	/**
	 * Iterate on all fields and check for all possible annotations with each one
	 *  if an annotation found stored in Hashtable specific for that category.
	 */
	public void init(){
		
		Field [] tabsField = entity.getClass().getDeclaredFields(); 
		
		for (Field field : tabsField) {
			
			CryptHashAnnotation annotationHasher = field.getAnnotation(CryptHashAnnotation.class);
			CryptCryptAnnotation annotationCrypt = field.getAnnotation(CryptCryptAnnotation.class);
			CryptSigneAnnotation annotationSigne = field.getAnnotation(CryptSigneAnnotation.class);
			
			if(annotationHasher!=null){
				fieldsToHash.put(field, annotationHasher);
			}
			
			if(annotationCrypt!=null){
				fieldsToCrypt.put(field,annotationCrypt);
			}
			
			if(annotationSigne!=null){
				fieldsToSign.put(field, annotationSigne);
			}
		}
	}
	
	/**
	 * read the salt within the Entity
	 * <p>
	 *  	if no salt found within the Entity, it will return null.
	 * </p>
	 * @return byte[]
	 */
	public byte[] getSalt(){
	
		try {
			
			Field field = entity.getClass().getDeclaredField("salt");
			
			field.setAccessible(true);
			
			this.salt = (byte []) field.get(entity);
			
			return this.salt;
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * return the salt that will be used to hash the entity field.
	 * if the Entity has one, return that salt, if not generate a new one.
	 * @deprecated 
	 * @return byte[]
	 */
	public byte[] generateSalt(){
		
		if( salt == null || salt.length == 0){
			
			Field [] tabsField = entity.getClass().getDeclaredFields();      
		    
			
	        for (Field field : tabsField) {
	        	
	        	if(field.getName().equals("salt")){
	        		
	        		try {
	        			
	        			field.setAccessible(true);
	        			
	        			this.salt = (byte []) field.get(entity);
						
	        			if(this.salt == null || this.salt.length == 0){
	        				
	        				this.salt = HasherFactory.generateSalt();
	        				
	        				field.set(entity, this.salt);
	        			}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	        		
	        		break;
	        	}
	        }
			
		}
		
		return salt;
	}

	/**
	 * getter
	 * @return Entity the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Hashtable of fields that should be encrypted or decrypted
	 * Hashtable of field as key and annotation as value.
	 * @return Hashtable<Field, CryptCryptAnnotation>
	 */
	public Hashtable<Field, CryptCryptAnnotation> getFieldsToCrypt() {
		return fieldsToCrypt;
	}

	/**
	 * Hashtable of fields that should be hashed
	 * Hashtable of field as key and annotation as value.
	 * @return Hashtable<Field, CryptHashAnnotation>
	 */
	public Hashtable<Field, CryptHashAnnotation> getFieldsToHash() {
		return fieldsToHash;
	}

	/**
	 * Hashtable of fields that should be signed or checked
	 * Hashtable of field as key and annotation as value.
	 * @return Hashtable<Field, CryptSigneAnnotation>
	 */
	public Hashtable<Field, CryptSigneAnnotation> getFieldsToSign() {
		return fieldsToSign;
	}
	
	
	/**
	 * private key of user
	 * @return BigInteger
	 */
	public BigInteger getPrivateKey(){
		return user.getKey().getPrivateKey();
	}
	
	/**
	 * public key of user
	 * @return BigInteger
	 */
	public BigInteger getPublicKey(){
		return user.getKey().getPublicKey();
	}
	
	/**
	 * ElGamalKey of user
	 * Composite key public and private key 
	 * @return ElGamalKey
	 */
	public ElGamalKey getKey(){
		return user.getKey();
	}
	
}
