package protocol.impl.sigma;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.BeforeClass;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCD;
import util.TestInputGenerator;

/**
 * Trent unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class TrentTest {
	public static byte[] msg;
	public static ElGamalKey key;
	public static Trent trent;

	@BeforeClass
	public static void initialize(){
		msg = TestInputGenerator.getRandomBytes(50);
		key = ElGamalAsymKeyFactory.create(false);
		trent = new Trent(key);
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

