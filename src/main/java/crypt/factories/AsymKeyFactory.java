package crypt.factories;

import org.bouncycastle.crypto.params.ElGamalParameters;

import crypt.api.key.AsymKey;
import crypt.impl.key.ElGamalAsymKey;
import model.entity.ElGamalKey;

/**
 * {@link AsymKey} factory
 * @author Julien Prudhomme
 *
 */
public class AsymKeyFactory {

	/**
	 * Create the default implemented {@link AsymKey}
	 * @return an {@link AsymKey}
	 */
	public static AsymKey<?> createDefaultAsymKey() {
		return ElGamalAsymKeyFactory.create(false);
	}
	
	/**
	 * Create an {@link ElGamalAsymKey}
	 * @param generateParams true if the implementation should generate new parameters or false for default parameters
	 * @return an {@link ElGamalAsymKey}
	 */
	public static ElGamalKey createElGamalAsymKey(boolean generateParams) {
		return ElGamalAsymKeyFactory.create(generateParams);
	}
	
	/**
	 * Create an ElGamalAsymKey from existing {@link ElGamalParameters}
	 * @param params The parameters used for creating the keys
	 * @return an {@link ElGamalAsymKey}
	 */
	public static ElGamalKey createElGamalAsymKey(ElGamalParameters params) {
		return ElGamalAsymKeyFactory.createFromParameters(params);
	}
}
