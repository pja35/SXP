package crypt.utils;

import java.math.BigInteger;

import org.junit.Test;

import util.TestInputGenerator;

/**
 * BigIntergerRandom unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class BigIntegerRandomTest {

	@Test
	public void testRand(){
		@SuppressWarnings("unused")
		BigIntegerRandom bg = new BigIntegerRandom();
		BigInteger bigInt = TestInputGenerator.getRandomBigInteger(100);
		BigInteger randBigInt = BigIntegerRandom.rand(bigInt.bitLength(), bigInt);
		assert(randBigInt.compareTo(BigInteger.ONE) >= 0 && randBigInt.compareTo(bigInt) <= 0);	
	}
}

