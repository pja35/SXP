/* Copyright 2015 Pablo Arrighi, Sarah Boukris, Mehdi Chtiwi, 
   Michael Dubuis, Kevin Perrot, Julien Prudhomme.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */
package model.entity.sigma;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * It's response to need to send in the protocol
 * it's abstract, for the different response in the protocol
 * @author Sarah Boukris
 * @author Julien Prudhomme
 */

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import model.entity.ElGamalKey;

@JsonTypeInfo(use = Id.CLASS,
         include = JsonTypeInfo.As.PROPERTY,
         property = "type")
@JsonSubTypes({
	@Type(value = ResponsesCCD.class),
	@Type(value = ResponsesCCE.class),
	@Type(value = ResponsesSchnorr.class),
})
public abstract class Responses{
	

	@XmlElement(name="masks")
	private Masks masks;

	@XmlElement(name="challenge")
	private BigInteger challenge;

	@XmlElement(name="response")
	private BigInteger response;
	
	public Responses(Masks masks, BigInteger challenge, BigInteger response)
	{
		super();
		this.setMasks(masks);
		this.setChallenge(challenge);
		this.setResponse(response);
	}
	
	/**
	 * Constructor
	 * used to transform json string to java
	 */
	public Responses(){
		super();
	}

    @JsonGetter("masks")
	public Masks getMasks() {
		return masks;
	}

    @JsonSetter("masks")
	public void setMasks(Masks masks) {
		this.masks = masks;
	}
	public BigInteger getResponse() {
		return response;
	}
	public void setResponse(BigInteger response) {
		this.response = response;
	}
	public BigInteger getChallenge() {
		return challenge;
	}
	public void setChallenge(BigInteger challenge) {
		this.challenge = challenge;
	}
	
	/**
	 * Verify the response according to the type of response
	 * @param Keys
	 * @param res
	 * @return
	 */
	public abstract Boolean Verifies(ElGamalKey Keys, ResEncrypt res);

	/**
	 * override equals to be able to compare two responses
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Responses))
			return false;
		
		Responses r = (Responses) o;
		if (r.getMasks() == null 
				|| r.getResponse() == null 
				|| r.getChallenge() == null)
			return false;
		
		boolean okM = r.getMasks().equals(this.getMasks());
		boolean okRes = r.getResponse().toString().equals(this.getResponse().toString());
		boolean okCha = r.getChallenge().toString().equals(this.getChallenge().toString());
		return okM && okRes && okCha;
	}
	
	/**
	 * override hashCode to be able to compare 2 responses
	 */
	@Override
	public int hashCode(){
		int hashM = masks.hashCode();
		int hashR = response.intValue();
		int hashC = response.intValue();
		return hashM + hashR + hashC;
	}
	
}
