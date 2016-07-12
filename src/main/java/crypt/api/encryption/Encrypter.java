package crypt.api.encryption;

/**
 * Encrypt or decrypt data according to a {@code Key}
 * @author Prudhomme Julien
 *
 * @param <Key> Type of the key
 */
public interface Encrypter<Key> {
	/**
	 * Set the key that will be used for encrypt or decrypt data.
	 * Note that the key could be different to encrypt or decrypt data.
	 * @param key The key.
	 */
	public void setKey(Key key);
	
	/** 
	 * Encrypt the {@code plaintText} according to previously settled {@code Key}
	 * @param plainText Clear data to encrypt
	 * @return encrypted {@code plainText}
	 */
	public byte[] encrypt(byte[] plainText);
	
	/**
	 * Decrypt the {@code cypher} according to the previously settled {@code Key}
	 * If can determine decrypting fail, return null
	 * @param cypher The encrypted data
	 * @return The decrypted data or null if fail.
	 */
	public byte[] decrypt(byte[] cypher);
}
