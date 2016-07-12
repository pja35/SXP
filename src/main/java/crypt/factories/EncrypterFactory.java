package crypt.factories;

import crypt.api.encryption.Encrypter;
import crypt.impl.encryption.ElGamalEncrypter;
import crypt.impl.encryption.SerpentEncrypter;

/**
 * {@link Encrypter} factory
 * @author Julien Prudhomme
 *
 */
public class EncrypterFactory {
	
	/**
	 * Create the default implementation of a symetric {@link Encrypter}
	 * @return an {@link Encrypter}
	 */
	public static Encrypter<?> createDefaultSymetricEncrypter() {
		return createSerpentEncrypter();
	}
	
	/**
	 * Create the default implementation of an asymetric {@link Encrypter}
	 * @return an {@link Encrypter}
	 */
	public static Encrypter<?> createDefaultAsymetricEncrypter() {
		return createElGamalEncrypter();
	}
	
	/**
	 * Create a Serpent implementation of {@link Encrypter}
	 * @return a {@link SerpentEncrypter}
	 */
	public static SerpentEncrypter createSerpentEncrypter() {
		return new SerpentEncrypter();
	}
	
	/**
	 * Create an ElGamal implementation of {@link Encrypter}
	 * @return an {@link ElGamalEncrypter}
	 */
	public static ElGamalEncrypter createElGamalEncrypter() {
		return new ElGamalEncrypter();
	}
}
