package model.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import crypt.api.key.AsymKey;

@Entity
public class ElGamalKey implements AsymKey<BigInteger>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6531626985325397645L;

	@NotNull
	@XmlElement(name="privateKey")
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	@JsonIgnore
	private BigInteger privateKey;
	
	@NotNull
	@XmlElement(name="publicKey")
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigInteger publicKey;
	
	@NotNull
	@XmlElement(name="p")
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigInteger p;
	
	@NotNull
	@XmlElement(name="g")
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigInteger g;
	
	@Override
	public BigInteger getPublicKey() {
		return publicKey;
	}
	@Override
	public BigInteger getPrivateKey() {
		return privateKey;
	}
	@Override
	public BigInteger getParam(String param) {
		switch(param) {
		case "p": return p;
		case "g": return g;
		default: throw new RuntimeException("param " + param + " undefined");
		}
	}
	@Override
	public void setPublicKey(BigInteger pbk) {
		publicKey = pbk;
	}
	@Override
	public void setPrivateKey(BigInteger pk) {
		privateKey = pk;
	}
	
	public void setG(BigInteger g) {
		this.g = g;
	}
	
	public void setP(BigInteger p) {
		this.p = p;
	}
	
	public BigInteger getP() {
		return p;
	}
	
	public BigInteger getG() {
		return g;
	}
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append("<" + this.getClass().getSimpleName().toLowerCase() + ">");
		s.append("<g>" + this.getG() + "</g>");
		s.append("<p>" + this.getP() + "</p>");
		s.append("<pbK>" + this.getPublicKey() + "</pbK>");
		s.append("</" + this.getClass().getSimpleName().toLowerCase() + ">");
		return s.toString();
	}
	
	@Override
	public boolean equals(Object o){
		if (! (o instanceof ElGamalKey))
			return false;
		ElGamalKey k = (ElGamalKey) o;
		return k.getP().equals(this.getP())
				&& k.getG().equals(this.getG())
				&& k.getPublicKey().equals(this.getPublicKey());
	}
	
	@Override
	public int hashCode(){
		return this.getPublicKey().hashCode();
	}
	
	public ElGamalKey copy(){
		ElGamalKey copy = new ElGamalKey();
		copy.setPrivateKey(this.getPrivateKey());
		copy.setPublicKey(this.getPublicKey());
		copy.setG(this.getG());
		copy.setP(this.getP());
		return copy;
	}
}
