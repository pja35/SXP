package crypt.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.bouncycastle.crypto.params.ElGamalParameters;

import controller.tools.LoggerUtilities;
import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptHashAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import crypt.api.annotation.ParserAction;
import crypt.api.hashs.Hasher;
import crypt.api.key.AsymKey;
import crypt.api.signatures.Signer;
import crypt.factories.AsymKeyFactory;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.EncrypterFactory;
import crypt.factories.HasherFactory;
import crypt.factories.SignerFactory;
import crypt.impl.encryption.ElGamalEncrypter;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import model.entity.ElGamalSignEntity;
import model.entity.User;

/**
 * Class represent the parser implementation. 
 * @author radoua abderrahim
 * @param <Entity> Type of Object generic type. 
 */
public class CryptoParser<Entity> extends AbstractParser<Entity> {

	public CryptoParser(Entity entity) {
		super(entity);
	}
	
	public CryptoParser(Entity entity,AsymKey<BigInteger> key) {
		super(entity,key);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entity parseAnnotation(ParserAction ... actions) {
		
		for (ParserAction action : actions) {
			switch (action) {
			case HasherAction:
				hasherFields();
				break;
			case CryptAction:
				cryptFields();
				break;
			case DecryptAction:
				decryptFields();
				break;
			case SigneAction:
				signeFields();
				break;
			case CheckAction:
				checkSignature();
				break;
			case Resilience:
				resilienceFields();
				break;
			}
		}

		return getEntity();
	}

	/**
	 * hasher action implementation
	 */
	private void hasherFields() {

		Hashtable<Field,CryptHashAnnotation> fieldsMap = getFieldsToHash(); //get fields to hash 
		
		for (Entry<Field, CryptHashAnnotation> entry : fieldsMap.entrySet()) {
			
			Field field = entry.getKey();
			
			CryptHashAnnotation annotation = entry.getValue();
			
			try {

				field.setAccessible(true);

				Object valueOfField = field.get(getEntity());

				Hasher hasher = HasherFactory.createDefaultHasher();

				hasher.setSalt(getSalt());

				valueOfField = hasher.getHash((byte[]) valueOfField);

				field.set(getEntity(), valueOfField);

			} catch (IllegalArgumentException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (IllegalAccessException e) {
				LoggerUtilities.logStackTrace(e);
			}
		}
	}

	/**
	 * encrypt action implementation
	 */
	private void cryptFields() {
		
		Hashtable<Field,CryptCryptAnnotation> fieldsMap = getFieldsToCrypt();
		
		for (Entry<Field, CryptCryptAnnotation> entry : fieldsMap.entrySet()) {
			
			Field field = entry.getKey();
			
			CryptCryptAnnotation annotation = entry.getValue();
			
			field.setAccessible(true);
			
			try {

				String valueOfField = String.valueOf(field.get(getEntity()));
				
				if(annotation.isCryptBySecondKey()){
					
					ElGamalKey elgamalkey = AsymKeyFactory.createElGamalAsymKey(false);
					
					Field keyField = getEntity().getClass().getDeclaredField(annotation.secondKey());
					
					//key is BigInteger
					keyField.setAccessible(true);
					
					if(! (keyField.get(getEntity()) instanceof BigInteger) ){ //not BigIntger throw exception
						throw new RuntimeException("Check-key must be a BigInteger!");
					}
					
					elgamalkey.setPublicKey((BigInteger) keyField.get(getEntity()));
					
					field.set(getEntity(), this.encrypt(valueOfField,elgamalkey,annotation.isEncryptKeyPublic()));
					
				}else{
					field.set(getEntity(), this.encrypt(valueOfField,annotation.isEncryptKeyPublic()));	
				}
				
			} catch (IllegalArgumentException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (IllegalAccessException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (NoSuchFieldException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (SecurityException e) {
				LoggerUtilities.logStackTrace(e);
			}
		}
	}

	/**
	 * decrypt action implementation
	 */
	private void decryptFields() {
		
		Hashtable<Field,CryptCryptAnnotation> fieldsMap = getFieldsToCrypt();
		
		for (Entry<Field, CryptCryptAnnotation> entry : fieldsMap.entrySet()) {
			
			Field field = entry.getKey();
			
			CryptCryptAnnotation annotation = entry.getValue();
			
			field.setAccessible(true);

			try {
				
				String valueOfField = String.valueOf(field.get(getEntity()));
				
				if(annotation.isCryptBySecondKey()){
					
					ElGamalKey elgamalkey = AsymKeyFactory.createElGamalAsymKey(false);
					
					Field keyField = getEntity().getClass().getDeclaredField(annotation.secondKey());
					
					//key is BigInteger
					keyField.setAccessible(true);
					
					if(! (keyField.get(getEntity()) instanceof BigInteger) ){
						throw new RuntimeException("Check-key must be a BigInteger!");
					}
					
					elgamalkey.setPublicKey((BigInteger) keyField.get(getEntity()));
					
					field.set(getEntity(), this.decrypt(valueOfField,elgamalkey,annotation.isEncryptKeyPublic()));
					
				}else{
					field.set(getEntity(), this.decrypt(valueOfField,annotation.isEncryptKeyPublic()));	
				}
				
			} catch (IllegalArgumentException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (IllegalAccessException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (NoSuchFieldException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (SecurityException e) {
				LoggerUtilities.logStackTrace(e);
			}
		}
	}

	/**
	 * signed action implementation
	 */
	private void signeFields() {
		
		Hashtable<Field,CryptSigneAnnotation> fieldsMap = getFieldsToSign();
		
		for (Entry<Field, CryptSigneAnnotation> entry : fieldsMap.entrySet()) {
			
			Field field = entry.getKey();
			
			CryptSigneAnnotation annotation = entry.getValue();
			
			field.setAccessible(true);
			
			StringBuilder sb = new StringBuilder();
			
			for (String nameOfField : annotation.signeWithFields()) {
				try {
					
					Field f = getEntity().getClass().getDeclaredField(nameOfField);
					
					f.setAccessible(true);
					
					if(f.get(getEntity()) instanceof byte[]){
						sb.append(new String((byte[])f.get(getEntity()), "UTF-8"));
					}else{
						sb.append(String.valueOf(f.get(getEntity())));
					}
				} catch (Exception e) {
					LoggerUtilities.logStackTrace(e);
				}
			}
			
			Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
			
			ElGamalKey elgamalkey = AsymKeyFactory.createElGamalAsymKey(false);
			
			elgamalkey.setPrivateKey(getPrivateKey());
			//elgamalkey.setPublicKey(null);
			
			signer.setKey(elgamalkey);
			//System.out.println("sign : { "+sb.toString()+" }");
			ElGamalSignature elGamalSignature = signer.sign(sb.toString().getBytes());
			
			ElGamalSignEntity signatureEntity = new ElGamalSignEntity(); //save signature in entity Item as a ElGamalSignEntity object
			signatureEntity.setR(elGamalSignature.getR());
		
			signatureEntity.setS(elGamalSignature.getS());

			
			try {
				
				field.set(getEntity(), signatureEntity);

			} catch (IllegalArgumentException e) {
				LoggerUtilities.logStackTrace(e);
			} catch (IllegalAccessException e) {
				LoggerUtilities.logStackTrace(e);
			}
			
		}
	}
	
	/**
	 * check the signature action implementation
	 */
	private void checkSignature(){
		
		Hashtable<Field,CryptSigneAnnotation> fieldsMap = getFieldsToSign();
		
		for (Entry<Field, CryptSigneAnnotation> entry : fieldsMap.entrySet()) {
			
			Field field = entry.getKey();
			
			CryptSigneAnnotation annotation = entry.getValue();
			
			field.setAccessible(true);
			
			StringBuilder sb = new StringBuilder();
			
			for (String nameOfField : annotation.signeWithFields()) {
				try {
					
					Field f = getEntity().getClass().getDeclaredField(nameOfField);
					
					f.setAccessible(true);
					
					if(f.get(getEntity()) instanceof byte[]){
						sb.append(new String((byte[])f.get(getEntity()), "UTF-8"));
					}else{
						sb.append(String.valueOf(f.get(getEntity())));
					}
					
				} catch (Exception e) {
					LoggerUtilities.logStackTrace(e);
				}
			}
			
			try {
				
				Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
				
				ElGamalKey elgamalkey = AsymKeyFactory.createElGamalAsymKey(false);
				
				//elgamalkey.setPrivateKey(null);
				
				Field keyField = getEntity().getClass().getDeclaredField(annotation.checkByKey());
				
				keyField.setAccessible(true);
				
				if(keyField.get(getEntity()) instanceof ElGamalKey){
					
					elgamalkey.setPublicKey(((ElGamalKey) keyField.get(getEntity())).getPublicKey());
					
				}else if(keyField.get(getEntity()) instanceof BigInteger){					//key is BigInteger
				
					elgamalkey.setPublicKey((BigInteger) keyField.get(getEntity()));
				
				}else{
					
					throw new RuntimeException("Check-key must be a BigInteger or ElGamalKey !");
				}
				
				signer.setKey(elgamalkey);
				
				ElGamalSignEntity signEntity = (ElGamalSignEntity) field.get(getEntity());
				
				ElGamalSignature signatue = new ElGamalSignature(signEntity.getR(), signEntity.getS());
				//System.out.println("check : { "+sb.toString()+" }");
				if(!signer.verify(sb.toString().getBytes(), signatue)){
					setEntityToNull();
					return;
				}
			} catch (Exception e) {
				LoggerUtilities.logStackTrace(e);
			}
		}
		
	}
	
	/**
	 * resilience action implementation
	 */
	private void resilienceFields() {

	}

}