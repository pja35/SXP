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
 * It's the result of encryption 
 * @author sarah
 *
 */
public class ResEncrypt {
	private BigInteger u;
	private BigInteger v;
	private byte[] M;
	
	/**
	 * Constructor
	 * @param u
	 * @param v
	 * @param M
	 */
	public ResEncrypt(BigInteger u, BigInteger v, byte[] M)
	{
		super();
		this.setU(u);
		this.setV(v);
		this.setM(M);
	}
	
	

	public BigInteger getU() {
		return u;
	}

	public void setU(BigInteger u) {
		this.u = u;
	}

	public BigInteger getV() {
		return v;
	}

	public void setV(BigInteger v) {
		this.v = v;
	}

	public byte[] getM() {
		return M;
	}

	public void setM(byte[] m) {
		M = m;
	}

	
	
}
