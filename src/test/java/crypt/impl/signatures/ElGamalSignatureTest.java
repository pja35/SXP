package crypt.impl.signatures;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import util.TestInputGenerator;


/**
 * ElGamalSignature unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class ElGamalSignatureTest {
	
	private BigInteger r;
	private BigInteger s;
	private BigInteger r2;
	private BigInteger s2;
	private BigInteger k;
	private byte[] m;
	
	public ElGamalSignatureTest(BigInteger r, BigInteger s, BigInteger r2, BigInteger s2, BigInteger k, byte[] m) {
		this.r = r;
		this.s = s;
		this.r2 = r2;
		this.s2 = s2;
		this.k = k;
		this.m = m;
	}

	@Parameters
	public static Collection<Object[]> data() {
		int nbLine = 10;
		Object[][] ob = new Object[nbLine][];
		for(int i=1; i<=nbLine; i++){
			ob[i-1] = new Object[] {TestInputGenerator.getRandomBigInteger(10 * i), TestInputGenerator.getRandomBigInteger(10 * i), 
					TestInputGenerator.getRandomBigInteger(10 * i), TestInputGenerator.getRandomBigInteger(10 * i),
					TestInputGenerator.getRandomBigInteger(10 * i), TestInputGenerator.getRandomBytes(10 * i) };
		}
		return Arrays.asList(ob);
	}

	@Test
	public void test() {
		ElGamalSignature signature = new ElGamalSignature(r,s);
		assertTrue(r.equals(signature.getR()));
		assertTrue(s.equals(signature.getS()));

		signature = new ElGamalSignature(r,s,k,m);
		assertTrue(r.equals(signature.getR()));
		assertTrue(s.equals(signature.getS()));
		assertTrue(k.equals(signature.getK()));
		assertTrue(Arrays.equals(m, signature.getM()));

		signature.setR(r2);
		signature.setS(s2);
		assertTrue(r2.equals(signature.getR()));
		assertTrue(s2.equals(signature.getS()));
	}
}

