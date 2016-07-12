package crypt.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BigIntegerRandom {
	private static SecureRandom random = new SecureRandom();
	static public BigInteger rand (int bitLength, BigInteger p)
	{
		BigInteger s;
		s = new BigInteger(bitLength,random);
		while(s.compareTo(BigInteger.ONE)<=0 && s.compareTo(p)>= 0)
			s = new BigInteger(bitLength,random);
		
		return s;
	}
}
