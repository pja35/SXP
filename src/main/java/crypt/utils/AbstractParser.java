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
import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptHashAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import crypt.api.annotation.ParserAnnotation;
import crypt.api.encryption.Encrypter;
import crypt.factories.EncrypterFactory;
import crypt.factories.HasherFactory;
import crypt.impl.encryption.ElGamalEncrypter;
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
	    this.user = user;
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

	
	
	/**
	 * if entity signature not correct set to null
	 */
	public void setEntityToNull() {
		this.entity = null;
		this.fieldsToCrypt = null;
		this.fieldsToHash = null;
		this.fieldsToSign = null;
	}
	

	public String encrypt(String data){
		    
			int bitSize = getKey().getPublicKey().bitLength();
			
			StringBuilder resultat=new StringBuilder();
			
			Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalEncrypter();
		    
			encrypter.setKey(getKey());
			/*
			int readBytes = data.length();
			
			byte[] bytesToBeEncoded = new byte[(bitSize - 1) / 8];
			
			InputStream in = null;
			
			byte [] res = new byte[0];
			
			try {
				
				in = new ByteArrayInputStream(data.getBytes());
				
				while(readBytes != -1){
	                
					readBytes = in.read(bytesToBeEncoded, 0, bytesToBeEncoded.length);
					
					res = appendBytes(res, encrypter.encrypt(bytesToBeEncoded));
					//resultat.append(Base64.getEncoder().encodeToString(encrypter.encrypt(bytesToBeEncoded)));
	            }
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//resultat.append(Base64.getEncoder().encodeToString(encrypter.encrypt(data.getBytes())));
			    	
		    return Base64.getEncoder().encodeToString(res);
		    
		    */
			String encodedString = Base64.getEncoder().encodeToString(encrypter.encrypt(data.getBytes()));
			return encodedString;
    }
		 
    public String decrypt(String data){
    		
    		int bitSize = getKey().getPrivateKey().bitLength();
    		
    		StringBuilder resultat=new StringBuilder();
    		
    	 	Encrypter<ElGamalKey> decrypter = EncrypterFactory.createElGamalEncrypter();
			
			decrypter.setKey(getKey());
			/*
			int readBytes = data.length();
			
			byte[] bytesToBeDecoded = new byte[2 * ((bitSize + 7) / 8)];
			
			InputStream in = null;
			
			byte [] res = new byte[0];
			
			try {
				byte [] arrayToDecrypt = Base64.getDecoder().decode(data);
				
				in = new ByteArrayInputStream(arrayToDecrypt);
				
				while(readBytes != -1){
	                
					readBytes = in.read(bytesToBeDecoded, 0, bytesToBeDecoded.length);
					
					res = appendBytes(res, decrypter.decrypt(bytesToBeDecoded));
					
					//resultat.append(new String(decrypter.decrypt(bytesToBeDecoded)));
	            }
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			String s=new String(res);
			
			return s.substring(0, s.length()/3-1);
			*/
			byte [] decrypted = decrypter.decrypt(Base64.getDecoder().decode(data));
			return new String(decrypted);
    }
		 
     public static byte[] appendBytes(byte[] a, byte[] b){
            int totalLenght = a.length + b.length;
            byte[] resault = new byte[totalLenght];
            int offset = 0;
            for (int i = 0; i < a.length; i++){
                resault[offset] = a[i];
                offset++;
            }
            for (int i = 0; i < b.length; i++){
                resault[offset] = b[i];
                offset++;
            }
            return resault;
      }
}
