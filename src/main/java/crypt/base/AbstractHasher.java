package crypt.base;

import crypt.api.hashs.Hashable;
import crypt.api.hashs.Hasher;

/**
 * An abstract class that implement {@link Hasher}
 *
 * @author Prudhomme Julien
 * @see Hasher
 * @see Hashable
 */
public abstract class AbstractHasher implements Hasher {

    protected byte[] salt;

    /**
     * Create a new {@link Hasher} without setting any salt.
     */
    public AbstractHasher() {
        salt = null;
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
