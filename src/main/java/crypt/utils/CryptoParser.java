package crypt.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.bouncycastle.crypto.params.ElGamalParameters;

import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptHashAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import crypt.api.annotation.ParserAction;
import crypt.api.hashs.Hasher;
import crypt.api.signatures.Signer;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.EncrypterFactory;
import crypt.factories.HasherFactory;
import crypt.factories.SignerFactory;
import crypt.impl.encryption.ElGamalEncrypter;
import crypt.impl.signatures.ElGamalSignature;
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
	
	
	public CryptoParser(Entity entity,User user) {
		super(entity,user);
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
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
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

			try {

				field.setAccessible(true);

				Object valueOfField = field.get(getEntity());

				ElGamalEncrypter encrypter = EncrypterFactory.createElGamalEncrypter();

				encrypter.setKey(getKey());
				
				valueOfField = encrypter.encrypt((byte[]) valueOfField);

				field.set(getEntity(), valueOfField);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
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
			
			try {
				
				field.setAccessible(true);

				Object valueOfField = field.get(getEntity());
					
				String nameOfFieldKey = annotation.decryptByKey();
				
				Field keyField = getEntity().getClass().getDeclaredField(nameOfFieldKey);
				keyField.setAccessible(true);
				
				ElGamalEncrypter encrypterElGamal = EncrypterFactory.createElGamalEncrypter();
				
				if( keyField.get(getEntity()) instanceof ElGamalKey){
					
					encrypterElGamal.setKey((ElGamalKey) keyField.get(getEntity()));
					
				}else if(keyField.get(getEntity()) instanceof BigInteger){
					//key is BigInteger
					
					ElGamalKey elGamalKey;
					
					if(annotation.isDecryptKeyPublic()){
						elGamalKey = ElGamalAsymKeyFactory.createFromParameters(new ElGamalParameters((BigInteger) keyField.get(getEntity()),null));
					}else{
						elGamalKey = ElGamalAsymKeyFactory.createFromParameters(new ElGamalParameters(null, (BigInteger) keyField.get(getEntity())));
					}
					
					encrypterElGamal.setKey(elGamalKey);
				}
				
				valueOfField = encrypterElGamal.decrypt((byte[]) valueOfField);

				field.set(getEntity(), valueOfField);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
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
					
					sb.append(String.valueOf(f.get(getEntity())));
					
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			Signer<ElGamalSignature, ElGamalKey> signer = (Signer<ElGamalSignature, ElGamalKey>) SignerFactory.createDefaultSigner();
			
			signer.setKey(getKey());
			
			ElGamalSignature elGamalSignature = signer.sign(sb.toString().getBytes());
			
			ElGamalSignEntity signatureEntity=new ElGamalSignEntity();
			
			signatureEntity.setR(elGamalSignature.getR());
			signatureEntity.setS(elGamalSignature.getS());
			
			try {
				
				field.set(getEntity(), signatureEntity);
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
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
					
					sb.append(String.valueOf(f.get(getEntity())));
					
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			Signer<ElGamalSignature, ElGamalKey> signer = (Signer<ElGamalSignature, ElGamalKey>) SignerFactory.createDefaultSigner();
			
			try {
			
				Field keyField = getEntity().getClass().getDeclaredField(annotation.checkByKey());
				
				keyField.setAccessible(true);
				
				if(keyField.get(getEntity()) instanceof ElGamalKey){
					
					signer.setKey((ElGamalKey) keyField.get(getEntity()));
					
				}else if(keyField.get(getEntity()) instanceof BigInteger){					//key is BigInteger
					
					ElGamalKey elGamalKey = ElGamalAsymKeyFactory.createFromParameters(new ElGamalParameters((BigInteger) keyField.get(getEntity()),BigInteger.ZERO));
					
					signer.setKey(elGamalKey);
				}
			
				ElGamalSignEntity  signEntity = (ElGamalSignEntity) field.get(getEntity());
				
				if(!signer.verify(sb.toString().getBytes(), new ElGamalSignature(signEntity.getR(), signEntity.getS()))){
					field.set(getEntity(), null);
				}
				
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * resilience action implementation
	 */
	private void resilienceFields() {

	}

}