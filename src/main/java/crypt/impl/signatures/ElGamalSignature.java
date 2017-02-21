package crypt.impl.signatures;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object that represent an ElGamal signature
 * @author Prudhomme Julien
 *
 */
public class ElGamalSignature {

	@XmlElement(name="r")
	private BigInteger r;
	@XmlElement(name="s")
	private BigInteger s;
	
	/* For sigma protocols */
	@XmlElement(name="k")
	private BigInteger k;
	@XmlElement(name="m")
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
	@JsonCreator
	public ElGamalSignature(@JsonProperty("r") BigInteger r, @JsonProperty("s") BigInteger s, @JsonProperty("k") BigInteger k, @JsonProperty("m") byte[] m) {
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
