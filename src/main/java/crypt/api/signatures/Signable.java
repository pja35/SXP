package crypt.api.signatures;

import crypt.api.hashs.Hashable;

/**
 * Object that implements Signable can be signed. They store a signature according to the object,
 * and the public key of owner signature
 * The signature can be obtained with a Signer that will use the {@link Hashable#getHashableData() getHashableData}
 * method to hash the object and produce a signature.
 * @author Prudhomme Julien
 * @param <Sign> The type of the signature
 */
public interface Signable<Sign> extends Hashable{
	/**
	 * Simple signature setter. You should'nt call this method directly and use a {@link Signer}
	 * @param s the signature
	 */
	public void setSign(Sign s);
	
	/**
	 * Get the stored signature
	 * @return The signature or null if the object isn't signed.
	 */
	public Sign getSign();
}
