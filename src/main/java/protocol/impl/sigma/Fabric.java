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
 * this class is used for the PCS and especially for the OR
 * it's a fabric for challenge, Mask and Response good for the different check
 * @author sarah
 *
 */

public class Fabric {

	/**
	 * fabric Challenge
	 * @param publicKeys
	 * @return
	 */
	private BigInteger FabricChallenge(ElGamalKey keys)
	{
		BigInteger c = Utils.rand(160, keys.getP());
		return c;
	}
	
	/**
	 * fabric Response
	 * @param publicKeys
	 * @return
	 */
	private BigInteger FabricResponse(ElGamalKey keys)
	{
		BigInteger r = Utils.rand(160, keys.getP());
		return r;
	}
	
	/**
	 * fabric a good Mask to send for the SchnorrProtocol
	 * @param c
	 * @param r
	 * @param publicKeys
	 * @return mask
	 */
	private Masks FabricMaskSchnorr(BigInteger c, BigInteger r,ElGamalKey keys)
	{
		BigInteger gPowr = keys.getG().modPow(r, keys.getP());
		BigInteger modInv = keys.getPublicKey().modPow(c,  keys.getP()).modInverse(keys.getP());
		BigInteger a = gPowr.multiply(modInv);
		Masks mask = new Masks(a,null);		
		return mask;
	}
	
	/**
	 * response to have send for Schnorr
	 * @param publicKeys
	 * @return responseSchnorr
	 */
	public ResponsesSchnorr SendResponseSchnorrFabric(ElGamalKey keys)
	{
		BigInteger challenge = this.FabricChallenge( keys);
		BigInteger response = this.FabricResponse(keys);
		Masks mask = this.FabricMaskSchnorr(challenge, response,keys);
		return new ResponsesSchnorr(mask,challenge,response);
	}
	
	/**
	 * fabric a good Mask to send for the CCE
	 * @param c
	 * @param r
	 * @param res
	 * @param keys
	 * @return
	 */
	private Masks FabricMaskCCE(BigInteger c, BigInteger r, ResEncrypt res, ElGamalKey keys)
	{
		BigInteger gPowr = keys.getG().modPow(r, keys.getP());
		BigInteger modInv = res.getU().modPow(c,  keys.getP()).modInverse(keys.getP());
		BigInteger a = gPowr.multiply(modInv);
		
		BigInteger pubPowr = keys.getPublicKey().modPow(r, keys.getP());
		BigInteger M = new BigInteger (res.getM());
		BigInteger vDivMPwc = res.getV().divide(M).modPow(c, keys.getP());
		BigInteger ModInv = vDivMPwc.modInverse(keys.getP());
		BigInteger aBis = pubPowr.multiply(ModInv);
		
		
		Masks mask = new Masks(a,aBis);		
		return mask;
	}
	
	/**
	 * response to have send for CCE
	 * @param publicKeys
	 * @return responseCCE
	 */
	public ResponsesCCE SendResponseCCEFabric(ResEncrypt res,ElGamalKey keys)
	{
		BigInteger challenge = this.FabricChallenge( keys);
		BigInteger response = this.FabricResponse(keys);
		Masks mask = this.FabricMaskCCE(challenge, response,res,keys);
		return new ResponsesCCE(mask,challenge,response);
	}
}
