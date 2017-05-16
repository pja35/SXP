package protocol.impl.sigma;

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigInteger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.Application;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCD;
import util.TestInputGenerator;
import util.TestUtils;

/**
 * Trent unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class TrentTest {
	public static Application application;
	public static final int restPort = 5600;	
	public static byte[] msg;
	public static ElGamalKey key;
	public static Trent trent;

	@BeforeClass
	public static void initialize(){
		application = new Application();
		application.runForTests(restPort);
		msg = TestInputGenerator.getRandomBytes(50);
		key = ElGamalAsymKeyFactory.create(false);
		trent = new Trent(key);
	}
	
	@AfterClass
	public static void stop(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
		application.stop();
	}

	@Test
	public void getterTest(){
		assertTrue(trent.getKey().getPublicKey() == key.getPublicKey());
	}
	
	@Test
	public void sendResponseTest() {
		ElGamalEncrypt encrypt = (new ElGamal(key)).encryptForContract(msg);
		ResEncrypt res = new ResEncrypt(encrypt.getU(), encrypt.getV(), msg);
		ResponsesCCD response = trent.SendResponse(res);
		assertTrue(response.Verifies(key, res));
	}
	
	@Test
	public void sendResponseTest2() {
		ElGamalEncrypt encrypt = (new ElGamal(key)).encryptForContract(BigInteger.TEN.toByteArray());
		ResEncrypt res = new ResEncrypt(encrypt.getU(), encrypt.getV(), BigInteger.TEN.toByteArray());
		ResponsesCCD response = trent.SendResponse(res, msg);
		assertFalse(response.Verifies(key, res, res.getM()));
		assertTrue(response.Verifies(key, res, msg));
	}
	
	// Signature test : see in SigmaEstablisherTest
	
	@Test
	public void decryptionTest(){
		byte[] encrypted = (new ElGamal(key)).encryptWithPublicKey(msg);		
		byte[] decryptedMsg = trent.decryption(encrypted);
		assertArrayEquals(decryptedMsg, msg);
	}
	
}

