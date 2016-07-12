package model.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import crypt.api.signatures.ParamName;
import crypt.base.BaseSignature;

@Entity
public class ElGamalSignEntity extends BaseSignature<BigInteger> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7049291865908224884L;

	@ParamName("r")
	@XmlElement(name="r")
	@NotNull
	private BigInteger r;
	
	@ParamName("s")
	@XmlElement(name="s")
	@NotNull
	private BigInteger s;
	
	public BigInteger getR() {
		return r;
	}
	public void setR(BigInteger r) {
		this.r = r;
	}
	public BigInteger getS() {
		return s;
	}
	public void setS(BigInteger s) {
		this.s = s;
	}

}
