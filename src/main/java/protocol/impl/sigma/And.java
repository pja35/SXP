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
package protocol.impl.sigma;
import java.util.HashMap;

import model.entity.ElGamalKey;


/**
 * This class is for the composability of reponses. This is clause And.
 * @author sarah
 *
 */

public class And {
	
	public Receiver receiver;
	public ResEncrypt resEncrypt;
	public HashMap <Responses,ElGamalKey> rK  = new HashMap <>();
	public Responses[] responses; 
	
	/**
	 * Constructor
	 * @param receiver 
	 * @param rK (HashMap for each response associate with Keys)
	 * @param resEncrypt 
	 * @param responses (all responses to need verify)
	 */
	
	public And (Receiver receiver, HashMap <Responses,ElGamalKey> rK,  ResEncrypt resEncrypt, Responses ... responses)
	{
		this.receiver = receiver;
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
				System.out.println("il y a un probleme");
				return false;
			}
		}
		System.out.println("tout est ok");
		return true;
		
	}
}
