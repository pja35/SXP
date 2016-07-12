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
import java.util.ArrayList;

/**
 * This class is for the composability. This is clause Or.
 * @author sarah
 *
 */
public class Or {

	public Receiver receiver;
	
	public And[] ands;
	public ArrayList <BigInteger> challenges = new ArrayList <BigInteger>(); 
	private BigInteger a;
	
	/**
	 * Constructor
	 * @param receiver
	 * @param a (a mask)
	 * @param ands (set of clause and to need to verify)
	 */
	public Or (Receiver receiver, BigInteger a, And ... ands)
	{
		this.receiver = receiver;
		this.ands  = ands;
		this.setA(a);
	}
	
	/**
	 * Verifies if clauses in the Or is true or not for the receiver
	 * @param resEncrypt
	 * @return Boolean
	 */
	public Boolean Verifies(ResEncrypt resEncrypt)
	{
		for(And and : ands)
		{
			if (!receiver.Verifies(and, true))
			{
				System.out.println("il y a un probleme");
				return false;
			}
			
			for (Responses res : and.responses)
				challenges.add(res.getChallenge());
		}
		
		if (!receiver.VerifiesChallenges(resEncrypt.getM(), getA(), challenges))
		{
			System.out.println("probleme dans les challenges");
		}
		return true;
		
	}

	public BigInteger getA() {
		return a;
	}

	public void setA(BigInteger a) {
		this.a = a;
	}
}
