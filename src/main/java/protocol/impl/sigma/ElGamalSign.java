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
 * An ElGamal signature
 * @author Julien Prudhomme
 *
 */
public class ElGamalSign {
	private BigInteger r;
	private BigInteger s;
	
	/**
	 * Initialize the ElGamal signature with the right R & S
	 * @param r
	 * @param s
	 */
	public ElGamalSign(BigInteger r, BigInteger s) {
		this.r = r;
		this.s = s;
	}
	
	
	
	public BigInteger getR() {
		return r;
	}
	
	public BigInteger getS() {
		return s;
	}
	
	public String toString() {
		return "<signR>" + r.toString(16) + "</signR>" +
				"<signS>" + s.toString(16) + "</signS>";
	}
}
