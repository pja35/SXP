package crypt.impl.encryption;

import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.encryption.Encrypter;
import crypt.factories.EncrypterFactory;
import model.entity.ElGamalKey;

/**
 * Encrypter that uses an asymmetric protocol to encrypt a password and
 * 	encrypt data using this password by a symmetric system
 * 
 * @author Nathanaël Eon
 *
 */
public class ElGamalSerpentEncrypter implements Encrypter<ElGamalKey> {
	
	private ElGamalKey key;
	
	
	@Override
	public void setKey(ElGamalKey k){
		key = k;
	}
	
	@Override
	public byte[] encrypt(byte[] buffer){
		return encryptMsg(buffer).getBytes();
	}
	
	@Override
	public byte[] decrypt(byte[] buffer){
		return decryptMsg(new String(buffer));
	}
	
	
	/**
	 * The encryption works as follow :
	 * 		set a password, encrypt it through elgamal
	 * 		encrypt the message with Serpent thanks to the password
	 * @param msg : msg to encrypt
	 * @param key : receiver ElGamalKey (using public key to encrypt) 
	 * @return : encrypted key and msg as a json String
	 */
	public String encryptMsg(byte[] msg){
		// Create a password
		String pwd = createPwd(20);
		
		// Set the encrypter for the password with ElGamal
		Encrypter<ElGamalKey> encrypter1 = EncrypterFactory.createElGamalEncrypter();
		encrypter1.setKey(key);
		
		// set the encrypter for the message with the password
		Encrypter<byte[]> encrypter2 = EncrypterFactory.createSerpentEncrypter();
		encrypter2.setKey(pwd.getBytes());
		
		// create the message
		JsonTools<byte[][]> json = new JsonTools<>(new TypeReference<byte[][]>(){});
		byte[][] content = new byte[2][];
		content[0] = encrypter1.encrypt(pwd.getBytes());
		content[1] = encrypter2.encrypt(msg);

		return json.toJson(content, true);
	}
	
	/**
	 * 	decrypt the password using ElGamal private key
	 * 	decrypt the message with password
	 * @param msg : crypted message
	 * @param key : ElGamalKey of the receiver (using private key here)
	 * @return : decrypted message
	 */
	private byte[] decryptMsg(String msg){
		// Get the two different encrypted data (password and message)
		JsonTools<byte[][]> json = new JsonTools<>(new TypeReference<byte[][]>(){});
		byte[][] content = json.toEntity(msg, true);
		
		// Decrypt the password
		Encrypter<ElGamalKey> encrypter1 = EncrypterFactory.createElGamalEncrypter();
		encrypter1.setKey(key);
		byte[] pwd = encrypter1.decrypt(content[0]);
		
		// Decrypt the message
		Encrypter<byte[]> encrypter2 = EncrypterFactory.createSerpentEncrypter();
		encrypter2.setKey(pwd);
		return encrypter2.decrypt(content[1]);
	}
	
	
	public String encryptMsg(byte[] msg,ElGamalKey secondKey){

		String pwd = createPwd(20);
		
		Encrypter<ElGamalKey> encrypter1 = EncrypterFactory.createElGamalEncrypter();
		encrypter1.setKey(key);
		
		Encrypter<ElGamalKey> encrypter2 = EncrypterFactory.createElGamalEncrypter();
		encrypter2.setKey(secondKey);
		
		Encrypter<byte[]> encrypter3 = EncrypterFactory.createSerpentEncrypter();
		encrypter3.setKey(pwd.getBytes());
		
		JsonTools<byte[][]> json = new JsonTools<>(new TypeReference<byte[][]>(){});
		byte[][] content = new byte[3][];
		content[0] = encrypter1.encrypt(pwd.getBytes());
		content[1] = encrypter2.encrypt(pwd.getBytes());
		content[2] = encrypter3.encrypt(msg);

		return json.toJson(content, true);
	}
	
	
	public byte[] decryptMsg(String msg,ElGamalKey secondKey){
		
		JsonTools<byte[][]> json = new JsonTools<>(new TypeReference<byte[][]>(){});
		byte[][] content = json.toEntity(msg, true);
		
		byte[] pwd;
		
		Encrypter<ElGamalKey> encrypter1 = EncrypterFactory.createElGamalEncrypter();
		encrypter1.setKey(key);
		
		if(key.getPublicKey().equals(secondKey.getPublicKey())){//sender
			pwd= encrypter1.decrypt(content[1]);
		}else{//receiver
			pwd= encrypter1.decrypt(content[0]);
		}
		
		Encrypter<byte[]> encrypter2 = EncrypterFactory.createSerpentEncrypter();
		encrypter2.setKey(pwd);
		return encrypter2.decrypt(content[2]);
	}
	
	
	
	/**
	 * Create a password using predefinite 
	 * @param len : length of the password wanted
	 * @return : a randomly generated String
	 */
	private String createPwd(int len){
		// Characters we will use to encrypt
		char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?".toCharArray();
		
		// Build a random String from the characters
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int j = 0; j < len; j++) {
		    char c = characters[random.nextInt(characters.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
}
