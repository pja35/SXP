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

public class ElGamalEncrypt {
		private BigInteger u;
		private BigInteger v;
		private BigInteger k; // only for SigmaProtocol 
		private byte [] m;
		/**
		 * Initialize the ElGamal signature with the right R & S
		 * @param r
		 * @param s
		 */
		public ElGamalEncrypt(BigInteger u, BigInteger v, BigInteger k, byte[] m) {
			this.u = u;
			this.v = v;
			this.k = k;
			this.m = m;
		}
		
		public BigInteger getU() {
			return u;
		}
		
		public BigInteger getV() {
			return v;
		}
		
		public BigInteger getK() {
			return k;
		}

		public byte [] getM() {
			return m;
		}

		public void setM(byte [] m) {
			this.m = m;
		}
		
}
