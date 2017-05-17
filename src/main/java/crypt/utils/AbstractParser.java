package crypt.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;

import controller.tools.LoggerUtilities;
import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptHashAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import crypt.api.annotation.ParserAnnotation;
import crypt.api.encryption.Encrypter;
import crypt.api.key.AsymKey;
import crypt.factories.EncrypterFactory;
import crypt.factories.HasherFactory;
import crypt.impl.encryption.ElGamalEncrypter;
import crypt.impl.encryption.ElGamalSerpentEncrypter;
import model.entity.ElGamalKey;
import model.entity.User;

/**
 * Abstract class that parse Entity fields for each annotation, and prepare fields for parser Action implemented in its child class.  
 *  
 * @author radoua abderrahim
 * @param <Entity> Type of Object generic type. 
 */
public abstract class AbstractParser<Entity> implements ParserAnnotation<Entity>{
	
	//private User user;
	
	
	private Entity entity;
	private byte[] salt;
	
	private Hashtable<Field,CryptCryptAnnotation> fieldsToCrypt;
	private Hashtable<Field,CryptHashAnnotation> fieldsToHash;
	private Hashtable<Field,CryptSigneAnnotation> fieldsToSign;
	
	private AsymKey<BigInteger> key;
	
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
	public AbstractParser(Entity entity,AsymKey<BigInteger> key){
		this.entity = entity;
	    this.key = key;
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
			LoggerUtilities.logStackTrace(e);
		} catch (SecurityException e) {
			LoggerUtilities.logStackTrace(e);
		} catch (IllegalArgumentException e) {
			LoggerUtilities.logStackTrace(e);
		} catch (IllegalAccessException e) {
			LoggerUtilities.logStackTrace(e);
		}
		
		return null;
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
		return this.key.getPrivateKey();
	}
	
	/**
	 * public key of user
	 * @return BigInteger
	 */
	public BigInteger getPublicKey(){
		return this.key.getPublicKey();
	}
	
	/**
	 * ElGamalKey of user
	 * Composite key public and private key 
	 * @return ElGamalKey
	 */
	public ElGamalKey getKey(){
		return (ElGamalKey) this.key;
	}
	
	public void setKey(AsymKey<BigInteger> key) {
		this.key = key;
	}

	/**
	 * if entity signature not correct set entity to null
	 */
	public void setEntityToNull() {
		this.entity = null;
		this.fieldsToCrypt = new Hashtable<>();
		this.fieldsToHash = new Hashtable<>();
		this.fieldsToSign = new Hashtable<>();
	}
	
	/**
	 * encrypt String data 
	 * using an asymmetric protocol to encrypt a password and encrypt data using this password by a symmetric system 
	 * @param data : String
	 * @return : encrypted data as a json String
	 */
	protected String encrypt(String data,boolean isKeyPublic){
		    
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
	 	
		encrypter.setKey(getKey());
		
		return new String(encrypter.encrypt(data.getBytes()));
    }
	
	/**
	 * decrypt String data 
	 * using an asymmetric protocol to decrypt a password and decrypt data using this password by a symmetric system 
	 * @param data : String as json format
	 * @return : decrypted data as String
	 */
	protected String decrypt(String data,boolean isKeyPublic){
    		
	 	Encrypter<ElGamalKey> decrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		
		decrypter.setKey(getKey());
		
		return new String(decrypter.decrypt(data.getBytes()));
    }
	
	
	protected String encrypt(String data,ElGamalKey key,boolean isKeyPublic){
		ElGamalSerpentEncrypter encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(getKey());
		return new String(encrypter.encryptMsg(data.getBytes(),key).getBytes());
    }
	
	protected String decrypt(String data,ElGamalKey key,boolean isKeyPublic){
		ElGamalSerpentEncrypter decrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		decrypter.setKey(getKey());
		return new String(decrypter.decryptMsg(new String(data.getBytes()),key));
    }
	
}
