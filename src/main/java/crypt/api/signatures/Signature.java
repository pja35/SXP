package crypt.api.signatures;

/**
 * Signature API
 * @author Julien Prudhomme
 *
 * @param <T> type of signature params
 */
public interface Signature<T> {
	/**
	 * Get the param p
	 * @param p
	 * @return
	 */
	public T getParam(String p);
}
