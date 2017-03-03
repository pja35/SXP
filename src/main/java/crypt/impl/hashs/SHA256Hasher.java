package crypt.impl.hashs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import controller.tools.LoggerUtilities;
import crypt.base.AbstractHasher;

/**
 * SHA-256 {@link crypt.api.hashs.Hasher} implementation
 * @author Prudhomme Julien
 *
 */
public class SHA256Hasher extends AbstractHasher {

	/**
	 * Create a new SHA256Hasher instance that will hash with {@code SHA-256}
	 */
	public SHA256Hasher() {
		super();
	}
	
	@Override
	public byte[] getHash(byte[] message) {
		MessageDigest md = null;
		try {
			//Getting SHA-256 hashing instance
			md = MessageDigest.getInstance("SHA-256");
			md.reset();
			if(this.salt != null) {
				//if salt is set, adding salt to the hash.
				md.update(this.salt);
			}
			return md.digest(message);
		} catch (NoSuchAlgorithmException e) {
			LoggerUtilities.logStackTrace(e);
			//SHA-256 algorithm doesn't exist on this machine
			return null;
		}
	}

}
