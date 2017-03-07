package crypt.impl.key;

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
 * ElGamalAsymKey unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class ElGamalAsymKeyTest {
	private BigInteger p;
	private BigInteger g;
	private BigInteger publicKey;
	private BigInteger privateKey;

	public ElGamalAsymKeyTest(BigInteger p, BigInteger g, BigInteger publicKey, BigInteger privateKey) {
		this.p = p;
		this.g = g;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}
	
	@Parameters
	public static Collection<Object[]> data() {
		int nbLine = 10;
		Object[][] ob = new Object[nbLine][];
		for(int i=1; i<=nbLine; i++){
			ob[i-1] = new Object[] {TestInputGenerator.getRandomBigInteger(10 * i), TestInputGenerator.getRandomBigInteger(10 * i), 
					TestInputGenerator.getRandomBigInteger(10 * i), TestInputGenerator.getRandomBigInteger(10 * i)};
		}
		return Arrays.asList(ob);
	}

	private void assertValuesAreSet(ElGamalAsymKey key) {
		assertTrue(p.equals(key.getP()));
		assertTrue(g.equals(key.getG()));
		assertTrue(p.equals(key.getParam("p")));
		assertTrue(g.equals(key.getParam("g")));
		assertTrue(publicKey.equals(key.getPublicKey()));
		assertTrue(privateKey.equals(key.getPrivateKey()));
	}

	@Test
	public void test() {
		ElGamalAsymKey key = new ElGamalAsymKey();
	
		key.setPublicKey(publicKey);
		key.setPrivateKey(privateKey);
		key.setP(p);
		key.setG(g);
		assertValuesAreSet(key);

		key = new ElGamalAsymKey(p, g, publicKey);
		key.setPrivateKey(privateKey);
		assertValuesAreSet(key);

		key = new ElGamalAsymKey(p, g, publicKey, privateKey);
		assertValuesAreSet(key);
	}
}

