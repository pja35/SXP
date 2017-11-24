package crypt.api.signatures;

/**
 * Signature API
 *
 * @param <T> type of signature params
 * @author Julien Prudhomme
 */
public interface Signature<T> {
    /**
     * Get the param p
     *
     * @param p
     * @return
     */
    public T getParam(String p);
}
