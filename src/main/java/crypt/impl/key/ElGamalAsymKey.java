package crypt.impl.key;

import java.math.BigInteger;

import crypt.base.AbstractAsymKey;

/**
 * ElGamal key implementations.
 * Contain public, private keys and g, p parameters.
 * @author Prudhomme Julien
 *
 */
public class ElGamalAsymKey extends AbstractAsymKey<BigInteger>{
	
	/**
	 * Create a new ElGamalAsymKey from p, g, and a public key, typically used for encrypting
	 * The private key is not set.
	 * @param p P parameters
	 * @param g g parameters
	 * @param publicKey the public key
	 */
	public ElGamalAsymKey(BigInteger p, BigInteger g, BigInteger publicKey) {
		this(p, g, publicKey, null);
	}
	
	/**
	 * Create a new, full ElGamalAsymKey, used for signing and decrypting
	 * @param p P parameters
	 * @param g g parameters
	 * @param publicKey the public key
	 * @param privateKey the private key
	 */
	public ElGamalAsymKey(BigInteger p, BigInteger g, BigInteger publicKey, BigInteger privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.params.put("p", p);
		this.params.put("g", g);
	}
	
	/**
	 * Create a new, empty ElGamalAsymKey
	 */
	public ElGamalAsymKey() {}
	
	/**
	 * Possible values : p, g
	 */
	@Override
	public BigInteger getParam(String p) {
		return super.getParam(p);
	}
	
	/**
	 * Get the P parameters of the ElGamal key
	 * @return the P parameters
	 */
	public BigInteger getP() {
		return params.get("p");
	}
	
	/**
	 * Get the G parameters of the ElGamal key
	 * @return the G parameters
	 */
	public BigInteger getG() {
		return params.get("g");
	}
	
	/**
	 * Set the P parameters of the ElGamal key
	 * @param p
	 */
	public void setP(BigInteger p) {
		params.put("p", p);
	}
	
	/**
	 * Set the G parameters of the ElGamal key
	 * @param g
	 */
	public void setG(BigInteger g) {
		params.put("g", g);
	}
}
