package crypt.api.hashs;

/**
 * Hasher is an interface for creating a hash from {@code byte[]}, or an object that implement {@link Hashable}.
 * @author Prudhomme Julien
 * @see Hashable
 */
public interface Hasher {
	
	/**
	 * Set a salt that will be added while hashing.
	 * If the salt is not set, the {@link Hasher} will hash without any salt.
	 * @param salt A byte array that will be used during hashs.
	 */
	public void setSalt(byte[] salt);
	
	/**
	 * Hash the message. Hash with a salt if set.
	 * @param message - The content to be hashed
	 * @return a byte array, hash of the {@code message}.
	 */
	public byte[] getHash(byte[] message);
	
	/**
	 * Produce a hash from an object that implement the {@link Hashable} interface.
	 * Use salt if set.
	 * @param object - An object that implement {@link Hashable}
	 * @return the object's hash
	 */
	public byte[] getHash(Hashable object);
}
