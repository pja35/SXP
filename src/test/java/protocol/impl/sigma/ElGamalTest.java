package protocol.impl.sigma;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import crypt.factories.ElGamalAsymKeyFactory;
import crypt.impl.signatures.ElGamalSignature;
import model.entity.ElGamalKey;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCD;
import util.TestInputGenerator;


/**
 * ElGamal unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class ElGamalTest {
	private final static Logger log = LogManager.getLogger(ElGamalTest.class);
	public static final int restPort = 5600;
	@Rule public ExpectedException exception = ExpectedException.none();

	private ElGamalKey keys;
	private byte[] message;
	ElGamal elg;

	@BeforeClass
	public static void setUpClass() {
		log.debug("**************** Starting test");
	}

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
		ElGamalSignature sign = new ElGamalSignature(null, TestInputGenerator.getRandomBigInteger(20));
		exception.expect(NullPointerException.class);
		elg.verifySignature(message, sign);
	}

	@Test
	public void verifySignatureException2Test(){
		ElGamalSignature sign = new ElGamalSignature(TestInputGenerator.getRandomBigInteger(20), null);
		exception.expect(NullPointerException.class);
		elg.verifySignature(message, sign);
	}

	@Test
	public void verifySignatureException3Test(){
		ElGamalSignature sign = elg.getMessageSignature(message);
		ElGamalKey keys2 = new ElGamalKey();
		log.debug("-- verifySignatureException3Test --");
		try{
			keys2.setPublicKey(TestInputGenerator.getRandomBigInteger(64));
			keys2.setG(TestInputGenerator.getRandomBigInteger(64));
			keys2.setP(TestInputGenerator.getRandomBigInteger(64));
			elg.setAsymsKeys(keys2);
			elg.verifySignature(message, sign);
		}catch(ArithmeticException e){
			// BigInteger not invertible case.
			log.debug(e.getMessage());
			keys2.setPrivateKey(BigInteger.valueOf(547937788733L));
			log.debug("BigInteger (" + keys2.getPrivateKey().bitCount() + " bits) : " + keys2.getPrivateKey());
			keys2.setG(BigInteger.valueOf(2647123923768671488L));
			log.debug("BigInteger (" + keys2.getG().bitCount() + " bits) : " + keys2.getG());
			keys2.setP(BigInteger.valueOf(148548281L));
			log.debug("BigInteger (" + keys2.getP().bitCount() + " bits) : " + keys2.getP());
			elg.setAsymsKeys(keys2);			
			elg.verifySignature(message, sign);
		}catch(NullPointerException e){
			exception.expect(NullPointerException.class);
			throw e;
		}		
	}

	@Test
	public void signatureVerifyTest(){
		ElGamalSignature sign = elg.getMessageSignature(message);
		assertTrue(elg.verifySignature(message, sign));		
	}

	@Test
	public void encryptDecryptTest(){
		byte[] encrMess = elg.encryptWithPublicKey(message);
		assertArrayEquals(message, elg.decryptWithPrivateKey(encrMess));
	}

	@Test
	public void encryptForContractTest(){Trent trent = new Trent(keys);
		ElGamalEncrypt encrMess = elg.encryptForContract(message);
		ResEncrypt res = new ResEncrypt(encrMess.getU(), encrMess.getV(), message);
		ResponsesCCD response = trent.SendResponse(res);
		assertTrue(response.Verifies(keys, res));
	}
}

