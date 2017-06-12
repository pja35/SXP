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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import controller.tools.MapResponseKeyDeserializer;
import controller.tools.MapSerializer;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Receiver;


/**
 * This class is for the composability of reponses. This is clause And.
 * @author sarah
 *
 */

public class And {
	
	private Receiver receiver;
	

	@XmlElement(name="resEncrypt")
	public ResEncrypt resEncrypt;
	

	@XmlElement(name="rK")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapResponseKeyDeserializer.class)
	public Map<Responses,ElGamalKey> rK  = new HashMap <>();
	

	@XmlElement(name="responses")
	public Responses[] responses; 
	

	/**
	 * Transform the And into a String
	 * @return the string corresponding to the AND
	 */
	@Override
	public String toString(){
		StringBuffer andS = new StringBuffer();
		andS.append("<" + this.getClass().getSimpleName().toLowerCase() + ">");
		andS.append(this.resEncrypt.toString());
		andS.append("<rK>" + rK.toString() + "</rK>");
		andS.append("<responses>");
		for (Responses r : responses){
			andS.append(r.toString());
		}
		andS.append("</responses>");
		andS.append("</" + this.getClass().getSimpleName().toLowerCase() + ">");
		return andS.toString();
	}
	
	/**
	 * Constructor
	 * Needed to transform json String to Java
	 */
	public And(){
		this.receiver = new Receiver();
	}
	/**
	 * Constructor
	 * @param receiver 
	 * @param rK (HashMap for each response associate with Keys)
	 * @param resEncrypt 
	 * @param responses (all responses to need verify)
	 */
	
	public And (HashMap <Responses,ElGamalKey> rK,  ResEncrypt resEncrypt, Responses ... responses)
	{
		this.receiver = new Receiver();
		this.rK  = rK;
		this.resEncrypt= resEncrypt;
		this.responses = responses;
	}
	
	/**
	 * Verify if set of responses is true or not for the receiver 
	 * @param or 
	 * if "or" the receiver doesn't verify if challenge it's good
	 * @return boolean 
	 */
	public Boolean Verifies(Boolean or)
	{
		for(Responses res : responses)
		{
			if (!or)
			{
				if (!receiver.VerifiesChallenge(res.getChallenge(), res.getMasks(), resEncrypt.getM()))
				{
					System.out.println("the challenge is fabricated");
					return false;
				}
			}

			if (!receiver.Verifies(res, rK.get(res), resEncrypt))
			{
				System.out.println("There is a problem in signatures");
				return false;
			}
		}
//		System.out.println("And signature verified");
		return true;
		
	}
}
