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


/**
 * Mask to send in the protocolSigma
 * @author sarah bourkis
 * @author Julien Prudhomme
 *
 */

public class Masks {

	private BigInteger a;
	private BigInteger aBis;
	
	/**
	 * Constructor
	 * @param a
	 * @param aBis
	 */
	public Masks (BigInteger a, BigInteger aBis)
	{
		this.setA(a);
		this.setaBis(aBis);
	}
	
	

	public BigInteger getA() {
		return a;
	}

	public void setA(BigInteger a) {
		this.a = a;
	}

	public BigInteger getaBis() {
		return aBis;
	}

	public void setaBis(BigInteger aBis) {
		this.aBis = aBis;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("<" + this.getClass().getSimpleName().toLowerCase() + ">");
		s.append("<a>" + a.toString(16) + "</a>");
		s.append("<aBis>" + aBis.toString(16) + "</aBis>");
		s.append("</" + this.getClass().getSimpleName().toLowerCase() + ">");
		return s.toString();
	}
}
