package crypt.base;

import crypt.api.hashs.Hashable;
import crypt.api.hashs.Hasher;

/**
 * An abstract class that implement {@link Hasher}
 * 
 * @see Hasher
 * @see Hashable
 * @author Prudhomme Julien
 *
 */
public abstract class AbstractHasher implements Hasher {

	protected byte[] salt;
	
	/**
	 * Create a new {@link Hasher} without setting any salt.
	 */
	public AbstractHasher() {
		salt = null;
	}
	
	/**
	 * Create a new {@link Hasher} providing a {@code salt}
	 * @param salt A salt that will be added for each hashs.
	 * @see crypt.api.hashs.Hasher#setSalt(byte[]) Hasher.setSalt(...)
	 */
	public AbstractHasher(byte[] salt) {
		setSalt(salt);
	}
	
	@Override
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	@Override
	public abstract byte[] getHash(byte[] message);

	@Override
	public byte[] getHash(Hashable object) {
		return getHash(object.getHashableData());
	}

}
