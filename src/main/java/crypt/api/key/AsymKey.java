package crypt.api.key;

/**
 * Key for asymmetric encryption. Contain a public and a private key, and if needed some paramaters.
 * @author Prudhomme Julien
 *
 * @param <T> Type of a key.
 */
public interface AsymKey<T> {
	/**
	 * Get the public key.
	 * @return a public key
	 */
	public T getPublicKey();
	
	/**
	 * Get the private key if defined.
	 * @return a private key or null
	 */
	public T getPrivateKey();
	
	/**
	 * Return a parameter
	 * @param p the parameter name
	 * @return the parameter of name p, or null
	 */
	public T getParam(String p);
	
	/**
	 * Set the public key
	 * @param pbk the public key
	 */
	public void setPublicKey(T pbk);
	
	/**
	 * Set the private key
	 * @param pk the private key
	 */
	public void setPrivateKey(T pk);
}
