package crypt.api.signatures;

/**
 * The signer can sign or verify signature on a byte array or object that implements signable.
 * @author Prudhomme Julien
 * @param <Sign> The type of the signature.
 * @param <Key> The type of the Key used to sign or verify signature.
 *
 */
public interface Signer<Sign, Key> {
	
	/**
	 * Set the key used to sign messages or verify signatures
	 * @param key A key that will be used to sign or verify.
	 */
	public void setKey(Key key);
	
	/**
	 * Retrieve the settled key
	 * @return the key used to sign
	 */
	public Key getKey();
	
	/**
	 * Sign the message according to previously settled {@code Key}
	 * @param message data to sign
	 * @return the message's signature
	 */
	public Sign sign(byte[] message);
	
	/**
	 * Sign an object that implement Signable. The signature is stored inside the object.
	 * @param signable the object to sign.
	 * @return The same signature that is stored in the object.
	 */
	public Sign sign(Signable<Sign> signable);
	
	/**
	 * Verify if the signature is valid, according to the previously settled key and the message.
	 * @param message The message
	 * @param sign the signature to verify
	 * @return True if signature is valid and correspond to the message.
	 */
	public boolean verify(byte[] message, Sign sign);
	
	/**
	 * Verify if the signature is valid, according to the previously settled key and the message.
	 * @param signable the object to verify
	 * @return True if the object signature correspond to the object and the settled key.
	 */
	public boolean verify(Signable<Sign> signable);
}
