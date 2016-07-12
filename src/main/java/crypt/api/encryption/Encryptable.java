package crypt.api.encryption;

/**
 * Instances of objects that implements Cryptable can be crypted. 
 * The encrypted data are stored in this instance.
 * @author Prudhomme Julien
 *
 */
public interface Encryptable {
	
	/**
	 * Encrypt instance's data.
	 * @param c the encrypter used for encrypting
	 */
	public void encrypt(Encrypter<?> c);
	
	/**
	 * Decrypt instance's data.
	 * @param c the encrypter used for encrypting
	 * @return {@code false} if decrypting fail.
	 */
	public boolean decrypt(Encrypter<?> c);
	
	/**
	 * This method can tell if object's data (typically attributes) are encrypted or not.
	 * @return True if object is encrypted, otherwise false.
	 */
	public boolean isEncrypted();
}
