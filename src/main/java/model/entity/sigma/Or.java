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
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import protocol.impl.sigma.Receiver;

/**
 * This class is for the composability. This is clause Or.
 * @author sarah
 *
 */
public class Or {

	private Receiver receiver;
	
	@XmlElement(name="contract")
	public byte[] contract;
	
	@XmlElement(name="ands")
	public And[] ands;
	
	
	@JsonIgnore
	public ArrayList <BigInteger> challenges = new ArrayList <BigInteger>(); 
	

	@XmlElement(name="a")
	private BigInteger a;
	
	/**
	 * Constructor
	 * @param receiver
	 * @param a (a mask)
	 * @param ands (set of clause and to need to verify)
	 */
	public Or (BigInteger a, And ... ands)
	{
		this.receiver = new Receiver();
		this.ands  = ands;
		this.setA(a);
	}
	
	/**
	 * Constructor
	 * Useful to get back a json String into an Or object
	 */
	public Or(){
		receiver = new Receiver();
	}
	
	/**
	 * Verifies if clauses in the Or is true or not for the receiver
	 * @param resEncrypt
	 * @return Boolean
	 */
	public Boolean Verifies(byte[] m)
	{
		challenges.clear();
		for(And and : ands)
		{
			if (!receiver.Verifies(and, true))
			{
				System.out.println("Signature problem");
				return false;
			}
			for (Responses res : and.responses){
				challenges.add(res.getChallenge());
			}
		}
		if (!receiver.VerifiesChallenges(m, getA(), challenges))
		{
//			System.out.println("Problem in challenges");
			return false;
		}
		challenges=new ArrayList <BigInteger>();
		return true;
		
	}

	public BigInteger getA() {
		return a;
	}

	public void setA(BigInteger a) {
		this.a = a;
	}
}
