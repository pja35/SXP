package model.entity;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import util.TestInputGenerator;

/**
 * ElGamalKey unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class ElGamalKeyTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	ElGamalKey key = new ElGamalKey();
	BigInteger publicKey;
	BigInteger privateKey;
	
	
	
	public ElGamalKeyTest(BigInteger publicKey, BigInteger privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{TestInputGenerator.getRandomBigInteger(200), TestInputGenerator.getRandomBigInteger(200)}, 
			{TestInputGenerator.getRandomBigInteger(10), TestInputGenerator.getRandomBigInteger(10)} 
		});
	}

	@Test
	public void setgetKeysTest(){
		key.setPublicKey(publicKey);
		assertTrue(key.getPublicKey().equals(publicKey));		
		key.setPrivateKey(privateKey);
		assertTrue(key.getPrivateKey().equals(privateKey));		
	}
	
	@Test
	public void undefinedParamTest(){
		String param = TestInputGenerator.getRandomIpsumText(1);
		exception.expect(RuntimeException.class);
		exception.expectMessage("param " + param + " undefined");	
		key.getParam(param);
	}
	
	@Test
	public void paramTest() {
		BigInteger g = TestInputGenerator.getRandomBigInteger(100); 
		key.setG(g);
		assertTrue(key.getParam("g").equals(g));
		assertTrue(key.getG().equals(g));

		BigInteger p = TestInputGenerator.getRandomBigInteger(100); 
		key.setP(p);
		assertTrue(key.getParam("p").equals(p));
		assertTrue(key.getP().equals(p));
		
	}
}
