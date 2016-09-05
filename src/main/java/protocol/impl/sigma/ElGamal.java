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

import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import crypt.ElGamalEngine;
import model.entity.ElGamalKey;

/**
 * This class is used for encryption, decryption, signs and verify signature.
 * @author michael
 *
 */
public class ElGamal  {
	
	public SecureRandom  random = new SecureRandom();
	
	private ElGamalKey keys;
	
	
	/**
	 * Constructor
	 * @param keys2
	 */
	public ElGamal(ElGamalKey keys2){
		this.keys = keys2;
	}
	
	/**
	 * Empty constructor
	 */
	public ElGamal(){}
	
	public void setKeys(ElGamalKey keys){
		this.keys = keys;
	}
	
	/**
	 * To sign a message
	 * @param M - byte[]
	 */
	public ElGamalSign getMessageSignature(byte[] M)
	{
		if(keys.getPrivateKey() == null)
			try {
				throw new Exception("Private key unknown");
			} catch (Exception e) {
				e.printStackTrace();
			}
		BigInteger k;
		BigInteger l;
		BigInteger r;
		BigInteger s;
		
		BigInteger m = new BigInteger(Hasher.SHA256(M).getBytes());
		k = BigInteger.probablePrime(1023, random);
		while(k.compareTo(BigInteger.ONE)<= 0 || k.gcd(keys.getP()).compareTo(BigInteger.ONE)!= 0 )
		{
			k = BigInteger.probablePrime(1023, random);
		}
		l = k.modInverse(keys.getP().subtract(BigInteger.ONE));
		
		r = keys.getG().modPow(k,keys.getP());
		s = l.multiply(m.subtract(r.multiply(keys.getPrivateKey())).mod(keys.getP().subtract(BigInteger.ONE)));
		return new ElGamalSign(r, s);
	}
	
	/**
	 * To verify a signature
	 * @param M - byte[]
	 * @return true if the signature is from public Key, false else
	 */
	public boolean verifySignature(byte[] M, ElGamalSign sign){
		try {
			if(sign == null || sign.getR() == null || sign.getS() == null){
				throw new Exception("R or S unknown");
			}
			if(keys.getPublicKey() == null){
				throw new Exception("Public key unknown");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		BigInteger m = new BigInteger(Hasher.SHA256(M).getBytes());
		BigInteger v = keys.getG().modPow(m, keys.getP());
		BigInteger w = (keys.getPublicKey().modPow(sign.getR(), keys.getP()).multiply(sign.getR().modPow(sign.getS(), keys.getP())).mod(keys.getP()));
		
		return (v.equals(w));
	}
	
	
	public byte[] encryptWithPublicKey(byte[] data) {
		ElGamalParameters params = new ElGamalParameters(keys.getP(), keys.getG());
		ElGamalPublicKeyParameters pubKey = new ElGamalPublicKeyParameters(keys.getPublicKey(), params);
		
		ElGamalEngine e = new ElGamalEngine();
		e.init(true, pubKey);
        return e.processBlock(data, 0, data.length) ;
	}
	
	public ElGamalEncrypt encryptForContract(byte[] data) {
		ElGamalParameters params = new ElGamalParameters(keys.getP(), keys.getG());
		ElGamalPublicKeyParameters pubKey = new ElGamalPublicKeyParameters(keys.getPublicKey(), params);
		
		ElGamalEngine e = new ElGamalEngine();
		e.init(true, pubKey);
		byte[] m = e.processBlock(data, 0, data.length);
		BigInteger k = e.getK();
        BigInteger u = keys.getG().modPow(k,keys.getP());
        BigInteger v = (keys.getPublicKey().modPow(e.getK(), keys.getP()).multiply(new BigInteger(data)));
        return new ElGamalEncrypt (u,v,k, m);
	}

	
	public  byte[] decryptWithPrivateKey(byte[] data) {
		ElGamalParameters params = new ElGamalParameters(keys.getP(), keys.getG());
		ElGamalPrivateKeyParameters privKey = new ElGamalPrivateKeyParameters(keys.getPrivateKey(), params);
		
		ElGamalEngine e = new ElGamalEngine();
		e.init(false, privKey);
		
        return e.processBlock(data, 0, data.length) ;
	}

	
	public void setAsymsKeys(ElGamalKey keys) {
		this.keys = keys;
	}
}
