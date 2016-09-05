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

import model.entity.ElGamalKey;


/**
 * The CCD response
 * @author sarah
 *
 */
public class ResponsesCCD extends Responses {

	/**
	 * Constructor
	 * @param mask
	 * @param challenge
	 * @param response
	 */
	public ResponsesCCD(Masks mask, BigInteger challenge, BigInteger response) {
		super(mask, challenge, response);
	}
	
	@Override
	/**
	 * Extends Responses
	 * Verify if the CCD response is good or not 
	 */
	public Boolean Verifies( ElGamalKey tKeys, ResEncrypt res) {
		if (!tKeys.getG().modPow(getResponse(), tKeys.getP()).equals(((tKeys.getPublicKey().modPow(getChallenge(), tKeys.getP())).multiply(getMasks().getA())).mod(tKeys.getP())))
		{
			return false;
		}
		BigInteger M = new BigInteger (res.getM());
		if(!res.getU().modPow(getResponse(),tKeys.getP() ).equals(res.getV().divide(M).modPow(getChallenge(), tKeys.getP()).multiply(getMasks().getaBis()).mod(tKeys.getP())))
		{
			return false;
		}
		
		return true;
	}

	

}
