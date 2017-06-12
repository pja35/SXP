package crypt.api.key;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import crypt.factories.AsymKeyFactory;
import util.TestInputGenerator;


/**
 * AsymKey api unit tests with AsymKeyFactory.createDefaultAsymKey()
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class AsymKeyTest {

	private BigInteger publicKey;
	private BigInteger privateKey;
	
	public AsymKeyTest(BigInteger publicKey, BigInteger privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	@Parameters
	public static Collection<Object[]> data() {
		int nbLine = 10;
		Object[][] ob = new Object[nbLine][];
		for(int i=1; i<=nbLine; i++){
			ob[i-1] = new Object[] {TestInputGenerator.getRandomBigInteger(10 * i), TestInputGenerator.getRandomBigInteger(10 * i)};
		}
		return Arrays.asList(ob);
	}

	@Test
	public void testAsymKey() {
		@SuppressWarnings("unchecked")
		AsymKey<BigInteger> key = (AsymKey<BigInteger>) AsymKeyFactory.createDefaultAsymKey();

		key.setPublicKey(publicKey);
		key.setPrivateKey(privateKey);

		assertTrue(publicKey.equals(key.getPublicKey()));
		assertTrue(privateKey.equals(key.getPrivateKey()));
	}
}

