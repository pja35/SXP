package protocol.impl.sigma;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import model.entity.ElGamalKey;
import crypt.factories.ElGamalAsymKeyFactory;
import protocol.impl.sigma.ElGamal;
import util.TestInputGenerator;


/**
 * ElGamal unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class ElGamalTest {
	private final static Logger log = LogManager.getLogger(ElGamalTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();
	
	private ElGamalKey keys;
	private byte[] message;
	ElGamal elg;

	@Before
	public void instantiate(){
		keys = ElGamalAsymKeyFactory.create(false);
		message = TestInputGenerator.getRandomIpsumText(10).getBytes();
		elg = new ElGamal(keys);
	}
	
	@Test
	public void privateKeyNullExceptionTest(){
		ElGamal elg2 = new ElGamal();
		ElGamalKey keys2 = new ElGamalKey();
		keys2.setPublicKey(TestInputGenerator.getRandomBigInteger(64));
		keys2.setG(TestInputGenerator.getRandomBigInteger(64));
		keys2.setP(TestInputGenerator.getRandomBigInteger(64));
		elg2.setKeys(keys2);
		exception.expect(NullPointerException.class);
		elg2.getMessageSignature(message);
	}
	
	@Test
	public void verifySignatureException0Test(){
		exception.expect(NullPointerException.class);
		elg.verifySignature(message, null);
	}
	
	@Test
	public void verifySignatureException1Test(){
		ElGamalSign sign = new ElGamalSign(null, TestInputGenerator.getRandomBigInteger(20));
		exception.expect(NullPointerException.class);
		elg.verifySignature(message, sign);
	}
	
	@Test
	public void verifySignatureException2Test(){
		ElGamalSign sign = new ElGamalSign(TestInputGenerator.getRandomBigInteger(20), null);
		exception.expect(NullPointerException.class);
		elg.verifySignature(message, sign);
	}
	
	@Test
	public void verifySignatureException3Test(){
		ElGamalSign sign = elg.getMessageSignature(message);
		ElGamalKey keys2 = new ElGamalKey();
		keys2.setPrivateKey(TestInputGenerator.getRandomBigInteger(64));
		keys2.setG(TestInputGenerator.getRandomBigInteger(64));
		keys2.setP(TestInputGenerator.getRandomBigInteger(64));
		elg.setAsymsKeys(keys2);
		exception.expect(NullPointerException.class);
		elg.verifySignature(message, sign);
	}
	
	@Test
	public void signatureVerifyTest(){
		ElGamalSign sign = elg.getMessageSignature(message);
		assertTrue(elg.verifySignature(message, sign));		
	}
	
	@Test
	public void encryptDecryptTest(){
		byte[] encrMess = elg.encryptWithPublicKey(message);
		assertArrayEquals(message, elg.decryptWithPrivateKey(encrMess));
	}
	
	@Test
	public void encryptForContractTest(){
		Trent trent = new Trent(keys);
		ElGamalEncrypt encrMess = elg.encryptForContract(message);
		ResEncrypt res = new ResEncrypt(encrMess.getU(), encrMess.getV(), message);
		ResponsesCCD response = trent.SendResponse(res);
		assertTrue(response.Verifies(keys, res));
	}
}

