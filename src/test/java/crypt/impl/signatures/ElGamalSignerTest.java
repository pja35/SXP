package crypt.impl.signatures;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.Rule;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import util.TestInputGenerator;

/**
 * ElGamalSigner unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class ElGamalSignerTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	private String message1;
	private String message2;
	
	
	
	public ElGamalSignerTest(String message1, String message2) {
		this.message1 = message1;
		this.message2 = message2;
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomIpsumText() }, 
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomIpsumText() },
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomIpsumText() }, 
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomIpsumText() }  
		});
	}
	

	@Test
	public void testSignExceptions() throws RuntimeException{
		ElGamalSigner signer = new ElGamalSigner();
		exception.expect(RuntimeException.class);
	    exception.expectMessage("Private key not set !");
		signer.sign(new byte[1]);
	}
	
	@Test
	public void testVerifyExceptions() throws RuntimeException{
		ElGamalSigner signer = new ElGamalSigner();
	    exception.expect(RuntimeException.class);
	    exception.expectMessage("public key not set !");
	    signer.verify(new byte[1], null);
	}
	
	@Test
	public void testSignExceptions2() throws RuntimeException{
		ElGamalSigner signer = new ElGamalSigner();
		ElGamalKey badKey = ElGamalAsymKeyFactory.create(false);
		badKey.setPublicKey(null);
		badKey.setPrivateKey(null);
		signer.setKey(badKey);
		exception.expect(RuntimeException.class);
	    exception.expectMessage("Private key not set !");
		signer.sign(new byte[1]);
	}
	
	@Test
	public void testVerifyExceptions2() throws RuntimeException{
		ElGamalSigner signer = new ElGamalSigner();
		ElGamalKey badKey = ElGamalAsymKeyFactory.create(false);
		badKey.setPublicKey(null);
		badKey.setPrivateKey(null);
		signer.setKey(badKey);
	    exception.expect(RuntimeException.class);
	    exception.expectMessage("public key not set !");
	    signer.verify(new byte[1], null);
	}
	
	@Test
	public void test() {
		ElGamalKey key1 = ElGamalAsymKeyFactory.create(false);
		ElGamalKey key2 = ElGamalAsymKeyFactory.create(false);
		
		ElGamalSigner signer = new ElGamalSigner();
		signer.setKey(key1);
		ElGamalSignature sign = signer.sign(message1.getBytes());
		assertTrue(message1, signer.verify(message1.getBytes(), sign));
		assertFalse(message1, signer.verify(message2.getBytes(), sign));
		signer.setKey(key2);
		assertTrue(key2 == signer.getKey());
		assertFalse(message2, signer.verify(message1.getBytes(), sign));
	}
}
