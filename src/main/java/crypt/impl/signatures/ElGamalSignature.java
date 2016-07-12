package crypt.impl.signatures;

import java.math.BigInteger;

/**
 * Object that represent an ElGamal signature
 * @author Prudhomme Julien
 *
 */
public class ElGamalSignature {
	private BigInteger r;
	private BigInteger s;
	
	/* For sigma protocols */
	private BigInteger k;
	private byte[] m;
	
	
	/**
	 * Create a new ElGamal signature with r & s parameters
	 * @param r R parameter
	 * @param s S parameter
	 */
	public ElGamalSignature(BigInteger r, BigInteger s) {
		this.r = r;
		this.s = s;
	}
	
	/**
	 * Constructor for sigma protocols
	 * TODO resee
	 * @param r
	 * @param s
	 * @param u
	 * @param m
	 */
	public ElGamalSignature(BigInteger r, BigInteger s, BigInteger k, byte[] m) {
		this.r = r;
		this.s = s;
		this.k = k;
		this.m = m;
	}

	/**
	 * Get the R parameter
	 * @return R
	 */
	public BigInteger getR() {
		return r;
	}
	
	/**
	 * Set the R parameter
	 * @param r R
	 */
	public void setR(BigInteger r) {
		this.r = r;
	}
	
	/**
	 * Get the S parameter
	 * @return S
	 */
	public BigInteger getS() {
		return s;
	}
	
	/**
	 * Set the S parameter
	 * @param s S
	 */
	public void setS(BigInteger s) {
		this.s = s;
	}
	
	public BigInteger getK() {
		return k;
	}
	
	public byte[] getM() {
		return m;
	}
}
