package crypt.impl.signatures;

import java.math.BigInteger;
import java.security.SecureRandom;

import crypt.api.hashs.Hasher;
import crypt.base.AbstractSigner;
import crypt.impl.hashs.SHA256Hasher;
import model.entity.ElGamalKey;

/**
 * Implementation of ElGamal signatures
 * @author Prudhomme Julien
 *
 */
public class ElGamalSigner extends AbstractSigner<ElGamalSignature, ElGamalKey> {

	@Override
	public ElGamalSignature sign(byte[] message) {
		if(key == null || key.getPrivateKey() == null) {
			throw new RuntimeException("Private key not set !");
		}
		SecureRandom random = new SecureRandom();
		Hasher hasher = new SHA256Hasher();
		BigInteger m = new BigInteger(hasher.getHash(message));
		
		BigInteger k;
		do {
			k = BigInteger.probablePrime(1023, random);
		} while(k.compareTo(BigInteger.ONE)<= 0 || k.gcd(key.getP()).compareTo(BigInteger.ONE)!= 0 );
		
		BigInteger l = k.modInverse(key.getP().subtract(BigInteger.ONE));
		BigInteger r = key.getG().modPow(k,key.getP());
		BigInteger s = l.multiply(m.subtract(r.multiply(key.getPrivateKey())).mod(key.getP().subtract(BigInteger.ONE)));
		
		return new ElGamalSignature(r, s);
	}

	@Override
	public boolean verify(byte[] message, ElGamalSignature sign) {
		if(key == null || key.getPublicKey() == null) {
			throw new RuntimeException("public key not set !");
		}
		
		Hasher hasher = new SHA256Hasher();
		
		BigInteger m = new BigInteger(hasher.getHash(message));
		BigInteger v = key.getG().modPow(m, key.getP());
		BigInteger w = (key.getPublicKey().modPow(sign.getR(), 
				key.getP()).multiply(sign.getR().modPow(sign.getS(), key.getP())).mod(key.getP()));
		
		return v.equals(w);
	}

	@Override
	public ElGamalKey getKey() {
		return this.key;
	}

}
