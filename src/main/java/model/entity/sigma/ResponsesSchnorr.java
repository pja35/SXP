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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import model.entity.ElGamalKey;


/**
 * The Schnorr response
 * @author sarah
 *
 */
public class ResponsesSchnorr extends Responses{

	/**
	 * Constructor
	 * @param mask
	 * @param challenge
	 * @param response
	 */
	@JsonCreator
	public ResponsesSchnorr(@JsonProperty("masks") Masks mask, @JsonProperty("challenge") BigInteger challenge,@JsonProperty("response") BigInteger response) {
		super(mask, challenge, response);
	}
	
	public ResponsesSchnorr(){
		super();
	}
	
	/**
	 * Extends Responses
	 * Verify if the Schnorr response is good or not 
	 */
	
	@Override
	public Boolean Verifies(ElGamalKey tKeys, ResEncrypt res) {
		return (tKeys.getG().modPow(getResponse(), tKeys.getP()).equals(((tKeys.getPublicKey().modPow(getChallenge(), tKeys.getP())).multiply(getMasks().getA())).mod(tKeys.getP())));
	}
	
	
	/**
	 * Override equals to be able to compare two responses
	 */
	@Override
	public boolean equals(Object o){
		if (! (o instanceof ResponsesSchnorr)){
			return false;
		}
		return super.equals(o);
	}

	/**
	 * Override hashCode to be able to compare two responses
	 */
	@Override
	public int hashCode(){
		int hashS = super.hashCode();
		return hashS + 1;
	}
}
