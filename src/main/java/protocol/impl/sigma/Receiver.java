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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import model.entity.ElGamalKey;


/**
 * this class simulate the receiver but in the end all users have this class
 * the receiver verifies if all he is received is good or not
 * @author sarah
 *
 */
public class Receiver {
	
	/**
	 * Verify a single response
	 * @param response
	 * @param tKeys
	 * @param res
	 * @return Boolean
	 */
	public Boolean Verifies (Responses response, ElGamalKey tKeys, ResEncrypt res)
	{
		return response.Verifies(tKeys, res);
	}
	
	/**
	 * Create the And response and Verify a And response
	 * @param or (for the condition in the verify method in An)
	 * @param rK
	 * @param resEncrypt
	 * @param responses
	 * @return
	 */
	public Boolean Verifies(Boolean or, HashMap <Responses,ElGamalKey> rK,ResEncrypt resEncrypt, Responses ... responses)
	{
		And and = new And(this, rK, resEncrypt, responses);
		return and.Verifies(or);
	}
	
	/**
	 * Verify a And response 
	 * @param and
	 * @param or (for the condition in the verify method in An)
	 * @return
	 */
	public Boolean Verifies(And and, Boolean or)
	{
		return and.Verifies(or);
	}
	
	/**
	 * Verify a Or response
	 * @param a
	 * @param resEncrypt
	 * @param ands
	 * @return Boolean
	 */
	public Boolean Verifies(BigInteger a,ResEncrypt resEncrypt, And... ands )
	{	
		Or or = new Or(this, a, ands);
		return or.Verifies(resEncrypt);
	}
	
	/**
	 * Verify if challenge is fabricated (not in Or)
	 * @param challenge
	 * @param mask
	 * @param message
	 * @return Boolean
	 */
	
	protected Boolean VerifiesChallenge(BigInteger challenge,Masks mask, byte[] message)
	{
		BigInteger test;
		byte[] buffer, resume;
		MessageDigest hash_function = null;
		
		String tmp = message.toString().concat(mask.getA().toString());
		
		buffer = tmp.getBytes();
		
		try {
			hash_function = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		resume = hash_function.digest(buffer);
		test = new BigInteger(resume);
		
		return (challenge.equals(test));
	}
	
	/**
	 * Verifies the composability of challenges for the Or
	 * @param message
	 * @param a
	 * @param challenge
	 * @return Boolean
	 */
	protected Boolean VerifiesChallenges(byte[] message, BigInteger a, ArrayList<BigInteger> challenge)
	{
		byte[] buffer, resume;
		MessageDigest hash_function = null;
		
		String tmp = message.toString().concat(a.toString());
		
		buffer = tmp.getBytes();
		
		try {
			hash_function = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		resume = hash_function.digest(buffer);
		BigInteger test = new BigInteger(resume);
		 
		BigInteger challenges = BigInteger.ZERO;
		for(BigInteger c : challenge)
			challenges = challenges.xor(c);
		
		return (challenges.equals(test));
	}
	

	
}
