package model.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigInteger r;
	
	@ParamName("s")
	@XmlElement(name="s")
	@NotNull
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
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
	
	/*
	 * Help for debug only  
	 */
	@Override
	public String toString() {
		return "ElGamalSignEntity [r=" + r + ", s=" + s + "]";
	}
}
