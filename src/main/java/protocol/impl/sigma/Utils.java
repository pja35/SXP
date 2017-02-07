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
import java.security.SecureRandom;

/**
 * this class is util class
 * @author sarah
 *
 */
public class Utils {

	static SecureRandom  random = new SecureRandom();
	
	/**
	 * random value
	 * @param bitLength
	 * @param object
	 * @return
	 */
	static public BigInteger rand (int bitLength, BigInteger p)
	{
		BigInteger s;
		s = new BigInteger(bitLength,random);
		while(s.compareTo(BigInteger.ONE) < 0 || s.compareTo(p) > 0)
			s = new BigInteger(bitLength,random);
		
		return s;
	}
    
	public static String toHex(byte[] donnees) {
	return javax.xml.bind.DatatypeConverter.printHexBinary(donnees);
    }
	/*static public BigInteger randPrime(int bitLength)
	{
		
	}*/
}
